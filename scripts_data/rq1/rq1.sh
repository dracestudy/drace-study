#!/bin/bash

set +e
trap 'exit 0' SIGTERM SIGINT

line="$1"
IFS=',' read -r repo sha <<< "$line"

OUT="/path/to/results.csv" # results CSV for runtime overhead
OUT_MEM="/path/to/results_mem.csv" # results CSV for memory overhead
TMP_OUT="/path/to/tmp.csv" # temporary results CSV for runtime overhead (for resumption)
TMP_OUT_MEM="/path/to/tmp_mem.csv" # temporary results CSV for memory overhead (for resumption)
RV_JAR="/path/to/rv-predict.jar" # Sakra jar
EXT_JAR="/path/to/extension-1.0.jar" # extension jar

org="${repo%%/*}"
proj="${repo##*/}"

OUTPUT_ROOT="/path/to/outputs-rv-predict-all-revised" # root directory for outputs
ARCHIVE_ROOT="/path/to/final-archives" # root directory for final compressed outputs
LOCKFILE="/path/to/results.lock" # lock file for synchronized file writes
CLUSTER_PY="/path/to/cluster.py" # cluster script for linear-time algorithms' unique races 

proj_dir="$OUTPUT_ROOT/${proj}"
scratch_dir="$OUTPUT_ROOT/${org}_${proj}"

archive="$OUTPUT_ROOT/${org}_${proj}.tar.gz"
final_archive="$ARCHIVE_ROOT/${org}_${proj}.tar.gz"

ORDER=(baseline rv hb hbepoch shb shbepoch fhb lockset goldilocks wcp syncp)

declare -A RT
declare -A MEM

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

load_partial() {
    RESUME_FROM="baseline"
    [ ! -f "$TMP_OUT" ] && return

    last_alg=""
    while IFS=',' read -r r alg rt post; do
        [ "$r" != "$repo" ] && continue
        RT["$alg"]="$rt"
        if [ -n "${post:-}" ]; then
            RT["${alg}_post"]="$post"
        fi
        last_alg="$alg"
    done < "$TMP_OUT"

    if [ -f "$TMP_OUT_MEM" ]; then
        while IFS=',' read -r r alg mem; do
            [ "$r" != "$repo" ] && continue
            MEM["$alg"]="$mem"
        done < "$TMP_OUT_MEM"
    fi

    if [ -n "$last_alg" ]; then
        found=0
        RESUME_FROM=""
        for a in "${ORDER[@]}"; do
            if [ "$found" -eq 1 ]; then
                RESUME_FROM="$a"
                break
            fi
            if [ "$a" = "$last_alg" ]; then
                found=1
            fi
        done
    fi
}

mkdir -p "$scratch_dir"
load_partial

cleanup_from() {
    local start="$1"

    for a in "${ORDER[@]}"; do
        if [ "$a" = "$start" ]; then
            if [ "$a" = "rv" ]; then
                remove_and_wait "$scratch_dir/project"
                rm -f "$scratch_dir/rv_predict.log" 2>/dev/null || true
            else
                remove_and_wait "$scratch_dir/project-$a"
                rm -f "$scratch_dir/$a.log" 2>/dev/null || true
            fi
            break
        fi
    done
}

[ -n "$RESUME_FROM" ] && cleanup_from "$RESUME_FROM"

run_rv() {
    mkdir -p "$scratch_dir/project/log"
    timefile="$scratch_dir/rv.mem"
    start=$(date +%s%3N)

    /usr/bin/time -q -f "%M" -o "$timefile" \
    timeout 5h mvn predict:predict \
        -Drat.skip=true \
        -Drv.jar-path="$RV_JAR" \
        -Dmaven.ext.class.path="$EXT_JAR" \
        -Drv.base-log-dir="$scratch_dir/project/log" \
        -Drv.log-dirname=result \
        -Drv.exclude=org.apache.maven,org.junit,com.runtimeverification.rvpredict \
        -Dstyle.color=never \
        >> "$scratch_dir/rv_predict.log" 2>&1

    status=$?
    end=$(date +%s%3N)

    rt=$(printf "%.2f" "$(echo "scale=4; ($end-$start)/1000" | bc)")
    [ $status -eq 124 ] && rt="TO"
    [ $status -ne 0 ] && rt="FAIL"

    mem=$(cat "$timefile" 2>/dev/null)
    echo "$rt,$mem"
}

run_surefire() {
    timefile="$scratch_dir/test.mem"
    start=$(date +%s%3N)

    /usr/bin/time -q -f "%M" -o "$timefile" \
    timeout 5h mvn surefire:test \
        -Drat.skip=true \
        -Dstyle.color=never \
        >> "$scratch_dir/test.log" 2>&1

    status=$?
    end=$(date +%s%3N)

    if [ $status -eq 124 ]; then
        rt="TO"
    elif [ $status -ne 0 ]; then
        rt="FAIL"
    else
        rt=$(printf "%.2f" "$(echo "scale=4; ($end-$start)/1000" | bc)")
    fi

    mem=$(cat "$timefile" 2>/dev/null)
    echo "$rt,$mem"
}

run_predict() {
    local alg="$1"
    mkdir -p "$scratch_dir/project-$alg/log"
    timefile="$scratch_dir/${alg}.mem"
    start=$(date +%s%3N)

    /usr/bin/time -q -f "%M" -o "$timefile" \
    timeout 5h mvn predict:predict \
        -Drat.skip=true \
        -Drv.algorithm="$alg" \
        -Drv.jar-path="$RV_JAR" \
        -Dmaven.ext.class.path="$EXT_JAR" \
        -Drv.base-log-dir="$scratch_dir/project-$alg/log" \
        -Drv.pair=true \
        -Drv.log-dirname=result \
        -Drv.exclude=org.apache.maven,org.junit,com.runtimeverification.rvpredict \
        -Dstyle.color=never \
        >> "$scratch_dir/${alg}.log" 2>&1

    status=$?
    end=$(date +%s%3N)

    rt=$(printf "%.2f" "$(echo "scale=4; ($end-$start)/1000" | bc)")
    post=""

    if [ $status -eq 124 ]; then
        rt="TO"
    elif [ $status -ne 0 ]; then
        rt="FAIL"
    else
        post_start=$(date +%s%3N)

        if [ -s "$scratch_dir/project-$alg/log/result/pair_info.dat" ]; then
            java -cp "$RV_JAR" com.runtimeverification.rvpredict.util.PairInfo \
                "$scratch_dir/project-$alg/log/result/pair_info.dat" \
                "$scratch_dir/project-$alg/log/result/pair_info.txt"

            python3 "$CLUSTER_PY" \
                "$scratch_dir/project-$alg/log/result/pair_info.txt" \
                "$scratch_dir/project-$alg/log/result/clustered_pair_info.txt"
        fi

        post_end=$(date +%s%3N)
        post=$(printf "%.2f" "$(echo "scale=4; ($post_end-$post_start)/1000" | bc)")
    fi

    mem=$(cat "$timefile" 2>/dev/null)
    echo "$rt,$post,$mem"
}

all_done() {
    for a in "${ORDER[@]}"; do
        [ -z "${RT[$a]}" ] && return 1
    done
    return 0
}

write_partial() {
    local alg="$1"
    (
        flock -x 200
        if [ "$alg" = "baseline" ] || [ "$alg" = "rv" ]; then
            echo "$repo,$alg,${RT[$alg]}" >> "$TMP_OUT"
        else
            echo "$repo,$alg,${RT[$alg]},${RT[${alg}_post]}" >> "$TMP_OUT"
        fi
        echo "$repo,$alg,${MEM[$alg]}" >> "$TMP_OUT_MEM"
    ) 200>"$LOCKFILE"
}

write_outputs() {
    row="$repo"
    memrow="$repo"

    for a in "${ORDER[@]}"; do
        if [ "$a" = "baseline" ] || [ "$a" = "rv" ]; then
            row+=",${RT[$a]}"
            memrow+=",${MEM[$a]}"
        else
            row+=",${RT[$a]},${RT[${a}_post]}"
            memrow+=",${MEM[$a]}"
        fi
    done
    (
        flock -x 200
        echo "$row" >> "$OUT"
        echo "$memrow" >> "$OUT_MEM"
    ) 200>"$LOCKFILE"
}

cleanup_fs_only() {
    remove_and_wait "$scratch_dir"
    remove_and_wait "$proj_dir"

	    BASE="/path/to/scratch-base"
	    OUTPUTS="$BASE/outputs-rv-predict-all-revised"

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

cleanup_all() {
    write_outputs
    if [ "$FAILED" -eq 1 ] || all_done; then
        wait_until_quiet "$scratch_dir"
	        tar -czf "$archive" -C "$OUTPUT_ROOT" "${org}_${proj}"
        mv "$archive" "$final_archive"
        cleanup_fs_only
    fi
}

run_fresh() {
    remove_and_wait "$archive"
    remove_and_wait "$proj_dir"
    remove_and_wait "$scratch_dir"
    remove_and_wait "$final_archive"
    mkdir -p "$scratch_dir"


    git clone "https://$GITHUB_TOKEN@github.com/$repo.git" "$proj_dir" >/dev/null 2>&1 || {
        (
            flock -x 200
            echo "$repo,$alg,clone_fail" >> "$OUT"
        ) 200>"$LOCKFILE"
        cleanup_fs_only
        exit 0
    }

    cd "$proj_dir" || {
        (
            flock -x 200
            echo "$repo,cd_fail" >> "$OUT"
        ) 200>"$LOCKFILE"
        cleanup_fs_only
        exit 0
    }

    git checkout -f "$sha" >/dev/null 2>&1 || {
        (
            flock -x 200
            echo "$repo,checkout_fail" >> "$OUT"
        ) 200>"$LOCKFILE"
        cd ..
        cleanup_fs_only
        exit 0
    }

    mvn clean test-compile -Drat.skip=true >/dev/null 2>&1 || {
        (
            flock -x 200
            echo "$repo,test_compile_fail" >> "$OUT"
        ) 200>"$LOCKFILE"
        cd ..
        cleanup_fs_only
        exit 0
    }
}

if [ "$RESUME_FROM" = "baseline" ]; then
    run_fresh
fi

cd "$proj_dir"
started=0
FAILED=0

for a in "${ORDER[@]}"; do
    if [ "$a" = "$RESUME_FROM" ]; then
        started=1
    fi
    if [ -n "$RESUME_FROM" ] && [ $started -eq 0 ]; then
        continue
    fi
    if [ "$a" = "rv" ]; then
        IFS=',' read -r rt mem <<< "$(run_rv)"
        RT[rv]="$rt"
        MEM[rv]="$mem"
        write_partial rv
    elif [ "$a" = "baseline" ]; then
        IFS=',' read -r rt mem <<< "$(run_surefire)"
        RT[baseline]="$rt"
        MEM[baseline]="$mem"
        if [ "$rt" = "FAIL" ]; then
            FAILED=1
            break
        fi
        write_partial baseline
    else
        IFS=',' read -r r p mem <<< "$(run_predict "$a")"
        RT[$a]="$r"
        RT[${a}_post]="$p"
        MEM[$a]="$mem"
        write_partial "$a"
    fi
done

cleanup_all
