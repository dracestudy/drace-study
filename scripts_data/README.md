This directory contains the scripts used for the evaluation and results:

- `rq1/`
  - `rq1_e2e.csv` - End-to-end time and races found in all projects by all techniques
  - `rq1_memory.csv` - Memory usage measurement for all projects and techniques
- `rq2/`
  - `rq2_all_threads.csv` - Profiling results for all running threads
  - `rq2_main_thread.csv` - Profiling results for the main thread
- `rq3/`
  - `races/` - Sets of sampled races we inspected for RQ3
  - `rq3_inspection.csv` - Inspection results of races in RQ3
- `rq4/`
  - `raw/` - Raw data from three runs with different test granularity
  - `rq4_e2e_memory_linear.csv` - Results of different test granularity for linear-time techniques
  - `rq4_e2e_memory_rv.csv` - Results of different test granularity for RV-Predict
- `cluster.py` - Helper script for clustering unique races for linear-time techniques
- `oom.csv` - Results of checking out-of-memory failures
- `proj_info.csv` - Characteristics and SHA of our 216 evaluated projects
