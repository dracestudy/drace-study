#!/bin/bash

set +e
trap 'cleanup_all; exit 0' SIGTERM SIGINT

repo="$1"
alg="$2"

OUT="/path/to/results.csv" # results CSV for different granularity
RV_JAR="/path/to/rv-predict.jar" # Sakra jar
EXT_JAR="/path/to/extension-1.0.jar" # extension jar

org="${repo%%/*}"
proj="${repo##*/}"

sha=$(grep "^${repo}," \
    /path/to/final_projects.csv \
    | head -n1 | cut -d',' -f2)

OUTPUT_ROOT="/path/to/outputs-rv-predict-all-granularity" # root directory for outputs
ARCHIVE_ROOT="/path/to/final-archives" # root directory for final compressed outputs
LOCKFILE="/path/to/results.lock" # lock file for synchronized file writes

scratch_dir="$OUTPUT_ROOT/${org}_${proj}-${alg}"
archive="$OUTPUT_ROOT/${org}_${proj}-${alg}.tar.gz"
final_archive="$ARCHIVE_ROOT/${org}_${proj}-${alg}.tar.gz"

remove_and_wait() {
    local dir="$1"
    for pid in $(pgrep -f "java|mvn" 2>/dev/null); do
        cwd=$(readlink /proc/$pid/cwd 2>/dev/null || true)
        if [[ "$cwd" == "$dir"* ]]; then
            kill -9 "$pid" 2>/dev/null || true
        fi
    done
    rm -rf "$dir" 2>/dev/null || true
}

wait_until_quiet() {
    path="$1"
    start=$(date +%s)
    while true; do
        if ! lsof +D "$path" >/dev/null 2>&1; then
            break
        fi
        now=$(date +%s)
        if (( now - start >= 300 )); then
            fuser -k "$path" 2>/dev/null || true
        fi
        sleep 1
    done
}

cleanup_all() {
    remove_and_wait "$scratch_dir"
    remove_and_wait "$proj_dir"

    BASE="/path/to/scratch-base"
    OUTPUTS="$BASE/outputs-rv-predict-all-granularity"

    if [ -d "$BASE" ]; then
        base_count=$(ls -A "$BASE" 2>/dev/null | wc -l)

        if [ "$base_count" -eq 0 ]; then
            remove_and_wait "$BASE"
        elif [ -d "$OUTPUTS" ]; then
            out_count=$(ls -A "$OUTPUTS" 2>/dev/null | wc -l)

            if [ "$base_count" -eq 1 ] && [ "$out_count" -eq 0 ]; then
                remove_and_wait "$BASE"
            fi
        fi
    fi
}

run_baseline() {
    mvn clean test-compile -Drat.skip=true >/dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo "test_compile_fail,"
        return 1
    fi

    memfile="$scratch_dir/test.mem"
    start=$(date +%s%3N)

    /usr/bin/time -q -f "%M" -o "$memfile" \
    timeout 5h mvn surefire:test \
        -Drat.skip=true \
        -Dstyle.color=never \
        >> "$scratch_dir/test.log" 2>&1

    status=$?
    end=$(date +%s%3N)

    rt=$(printf "%.2f" "$(echo "scale=4; ($end-$start)/1000" | bc)")
    mem=$(cat "$memfile" 2>/dev/null)

    if [ $status -eq 124 ]; then
        echo "test_timeout,"
        return 1
    elif [ $status -ne 0 ]; then
        echo "test_fail,"
        return 1
    fi

    echo "$rt,$mem"
    return 0
}

count_races_rv() {
    grep -c "Data race" "$1/result/result.txt" 2>/dev/null || echo 0
}

count_races_cluster() {
python3 - <<EOF
from collections import defaultdict
import re

text = open("$1", "r", errors="ignore").read()

pair_to_blocks = defaultdict(list)
blocks = text.split("---------------------------------------")

for block in blocks:
    if "Count:" not in block:
        continue
    m = re.search(r"Count:\s*(\d+)", block)
    if not m:
        continue

    body = block[:m.start()].strip()
    if "-" not in body:
        continue

    left, right = body.split("-", 1)

    def first(lines):
        for l in lines:
            l = l.strip()
            if l:
                return l
        return None

    l0 = first(left.splitlines())
    r0 = first(right.splitlines())

    if not l0 or not r0:
        continue

    pair = tuple(sorted([l0, r0]))
    pair_to_blocks[pair].append(block)

print(len(pair_to_blocks))
EOF
}

run_one() {
    local mode="$1"
    local idx="$2"

    suffix="$idx"
    log_dir="$OUTPUT_ROOT/${org}_${proj}-${alg}/project-${suffix}/log"

    memfile="$scratch_dir/${mode}-${suffix}.mem"
    logfile="$scratch_dir/${mode}-${suffix}.log"

    mkdir -p "$log_dir"

    extra=""
    if [ "$mode" = "method" ]; then
        extra="-Drv.test-scope=method"
    elif [ "$mode" = "class" ]; then
        extra="-Drv.test-scope=class"
    fi

    start=$(date +%s%3N)
    if [ "$alg" = "rv" ]; then
        /usr/bin/time -q -f "%M" -o "$memfile" \
        timeout 5h mvn predict:predict \
            -Drat.skip=true \
            -Drv.jar-path="$RV_JAR" \
            -Dmaven.ext.class.path="$EXT_JAR" \
            -Drv.base-log-dir="$log_dir" \
            -Drv.log-dirname=result \
            $extra \
            -Drv.exclude=org.apache.maven,org.junit,com.runtimeverification.rvpredict \
            -Dstyle.color=never \
            >> "$logfile" 2>&1
    else
        /usr/bin/time -q -f "%M" -o "$memfile" \
        timeout 5h mvn predict:predict \
            -Drat.skip=true \
            -Drv.algorithm="$alg" \
            -Drv.jar-path="$RV_JAR" \
            -Dmaven.ext.class.path="$EXT_JAR" \
            -Drv.base-log-dir="$log_dir" \
            -Drv.pair=true \
            -Drv.log-dirname=result \
            $extra \
            -Drv.exclude=org.apache.maven,org.junit,com.runtimeverification.rvpredict \
            -Dstyle.color=never \
            >> "$logfile" 2>&1
    fi

    status=$?
    end=$(date +%s%3N)

    rt=$(printf "%.2f" "$(echo "scale=4; ($end-$start)/1000" | bc)")
    [ $status -eq 124 ] && rt="TO"
    [ $status -ne 0 ] && rt="FAIL"

    mem=$(cat "$memfile" 2>/dev/null)
    if [ "$rt" = "FAIL" ] || [ "$rt" = "TO" ]; then
        if [ "$alg" = "rv" ]; then
            echo "$rt,,"
        else
            echo "$rt,,,"
        fi
        return 0
    fi

    pair_dir="$log_dir/result"
    pair_dat="$pair_dir/pair_info.dat"
    pair_txt="$pair_dir/pair_info.txt"
    clustered="$pair_dir/clustered.txt"

    pp="0.00"
    if [ "$alg" != "rv" ]; then
        pp_start=$(date +%s%3N)
    fi

    if [ -s "$pair_dat" ]; then
        if [[ "$alg" == "lockset" || "$alg" == "goldilocks" ]]; then
            java -cp "$RV_JAR" \
                com.runtimeverification.rvpredict.util.PairInfo \
                "$pair_dat" "$pair_txt" "$alg"
        else
            java -cp "$RV_JAR" \
                com.runtimeverification.rvpredict.util.PairInfo \
                "$pair_dat" "$pair_txt"
        fi
        python3 /path/to/cluster.py \
            "$pair_txt" "$clustered"
    fi

    if [ "$alg" != "rv" ]; then
        pp_end=$(date +%s%3N)
        pp=$(printf "%.2f" "$(echo "scale=4; ($pp_end-$pp_start)/1000" | bc)")
    fi

    if [ "$alg" = "rv" ]; then
        races=$(count_races_rv "$log_dir")
        echo "$rt,$mem,$races"
    else
        if [ -f "$clustered" ]; then
            races=$(count_races_cluster "$clustered")
        else
            races=0
        fi
        echo "$rt,$pp,$mem,$races"
    fi
}

proj_dir="$OUTPUT_ROOT/${proj}-${alg}"
remove_and_wait "$scratch_dir"
remove_and_wait "$proj_dir"
mkdir -p "$scratch_dir"

git clone "https://$GITHUB_TOKEN@github.com/$repo.git" "$proj_dir" >/dev/null 2>&1
if [ $? -ne 0 ]; then
    (
        flock -x 200
        echo "$repo,$alg,clone_fail" >> "$OUT"
    ) 200>"$LOCKFILE"
    cd ..
    cleanup_all
    exit 0
fi

cd "$proj_dir" || {
    (
        flock -x 200
        echo "$repo,$alg,cd_fail" >> "$OUT"
    ) 200>"$LOCKFILE"
    cd ..
    cleanup_all
    exit 0
}

git checkout -f "$sha" >/dev/null 2>&1
if [ $? -ne 0 ]; then
    (
        flock -x 200
        echo "$repo,$alg,checkout_fail" >> "$OUT"
    ) 200>"$LOCKFILE"
    cd ..
    cleanup_all
    exit 0
fi

baseline=$(run_baseline)
IFS=',' read -r base_rt base_mem <<< "$baseline"

if [[ "$base_rt" == test* ]]; then
    (
        flock -x 200
        echo "$repo,$alg,$base_rt" >> "$OUT"
    ) 200>"$LOCKFILE"
    cd ..
    cleanup_all
    exit 0
fi

res1=$(run_one "method" 1)
res2=$(run_one "class" 2)
res3=$(run_one "all" 3)

if [ "$alg" = "rv" ]; then
    IFS=',' read -r rt1 mem1 races1 <<< "$res1"
    IFS=',' read -r rt2 mem2 races2 <<< "$res2"
    IFS=',' read -r rt3 mem3 races3 <<< "$res3"
    (
        flock -x 200
        echo "$repo,$alg,$base_rt,$base_mem,$rt1,$mem1,$races1,$rt2,$mem2,$races2,$rt3,$mem3,$races3" >> "$OUT"
    ) 200>"$LOCKFILE"

else
    IFS=',' read -r rt1 pp1 mem1 races1 <<< "$res1"
    IFS=',' read -r rt2 pp2 mem2 races2 <<< "$res2"
    IFS=',' read -r rt3 pp3 mem3 races3 <<< "$res3"

    (
        flock -x 200
        echo "$repo,$alg,$base_rt,$base_mem,$rt1,$pp1,$mem1,$races1,$rt2,$pp2,$mem2,$races2,$rt3,$pp3,$mem3,$races3" >> "$OUT"
    ) 200>"$LOCKFILE"
fi

wait_until_quiet
tar -czf "$archive" -C "$OUTPUT_ROOT" "${org}_${proj}-${alg}"
mv "$archive" "$final_archive"

cd ..
cleanup_all
exit 0
