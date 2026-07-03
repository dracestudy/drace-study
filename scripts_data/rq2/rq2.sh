#!/bin/bash

set +e
trap 'cleanup_all; exit 0' SIGTERM SIGINT

line="$1"
IFS=',' read -r repo sha <<< "$line"

OUT="/path/to/results.csv" # results CSV for end-to-end time 
RV_JAR="/path/to/rv-predict.jar" # Sakra jar
EXT_JAR="/path/to/extension-1.0.jar" # extension jar

org="${repo%%/*}"
proj="${repo##*/}"

OUTPUT_ROOT="/path/to/outputs-rv-predict-all-profiling" # root directory for outputs
ARCHIVE_ROOT="/path/to/final-archives" # root directory for final compressed outputs

proj_dir="$OUTPUT_ROOT/${proj}"
scratch_dir="$OUTPUT_ROOT/${org}_${proj}"

archive="$OUTPUT_ROOT/${org}_${proj}.tar.gz"
final_archive="$ARCHIVE_ROOT/${org}_${proj}.tar.gz"

remove_and_wait() {
    fuser -k "$1" 2>/dev/null || true 
    sleep 2 
    rm -rf "$1" 2>/dev/null || true
    while [ -e "$1" ]; do
        fuser -k "$1" 2>/dev/null || true 
        sleep 1
        rm -rf "$1" 2>/dev/null || true
    done
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
    OUTPUTS="$BASE/outputs-rv-predict-all-profiling"

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

run_rv() {
    mkdir -p "$scratch_dir/project/log"

    start=$(date +%s%3N)
    RV_PROJECT_DIR="${org}_${proj}" timeout 5h mvn predict:predict \
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

    mv "$scratch_dir"/profile-*.jfr "$scratch_dir/project/" 2>/dev/null || true
    runtime=$(printf "%.2f" "$(echo "scale=4; ($end - $start)/1000" | bc)")

    if [ $status -eq 124 ]; then runtime="TO"; fi
    if [ $status -ne 0 ]; then runtime="FAIL"; fi

    echo "$runtime"
}

run_predict() {
    alg="$1"
    mkdir -p "$scratch_dir/project-$alg/log"

    start=$(date +%s%3N)
    RV_PROJECT_DIR="${org}_${proj}" timeout 5h mvn predict:predict \
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

    mv "$scratch_dir"/profile-*.jfr "$scratch_dir/project-$alg/" 2>/dev/null || true
    runtime=$(printf "%.2f" "$(echo "scale=4; ($end - $start)/1000" | bc)")

    if [ $status -eq 124 ]; then runtime="TO"; fi
    if [ $status -ne 0 ]; then runtime="FAIL"; fi

    echo "$runtime"
}

remove_and_wait "$proj_dir"
remove_and_wait "$scratch_dir"
remove_and_wait "$final_archive"
mkdir -p "$scratch_dir"

git clone "https://$GITHUB_TOKEN@github.com/$repo.git" "$proj_dir" >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "$repo,clone_fail" >> "$OUT"
    cleanup_all
    exit 0
fi

cd "$proj_dir" || {
    echo "$repo,cd_fail" >> "$OUT"
    cleanup_all
    exit 0
}

git checkout -f "$sha" >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "$repo,checkout_fail" >> "$OUT"
    cd ..
    cleanup_all
    exit 0
fi

mvn clean test-compile -Drat.skip=true >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "$repo,test_compile_fail" >> "$OUT"
    cd ..
    cleanup_all
    exit 0
fi

rv=$(run_rv)
hb=$(run_predict hb)
hbepoch=$(run_predict hbepoch)
shb=$(run_predict shb)
shbepoch=$(run_predict shbepoch)
fhb=$(run_predict fhb)
lockset=$(run_predict lockset)
goldilocks=$(run_predict goldilocks)
wcp=$(run_predict wcp)
syncp=$(run_predict syncp)

echo "$repo,$rv,$hb,$hbepoch,$shb,$shbepoch,$fhb,$lockset,$goldilocks,$wcp,$syncp" >> "$OUT"

wait_until_quiet "$scratch_dir"
tar -czf "$archive" \
    --exclude="${org}_${proj}/project*/log" \
        -C "$OUTPUT_ROOT" \
        "${org}_${proj}"

mv "$archive" "$final_archive"

cd ..
cleanup_all
