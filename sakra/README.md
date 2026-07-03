#### Prerequisites
`setup.sh` expects a Debian/Ubuntu-like environment with `apt` and `sudo`.
It should be run from a sudo-capable user account. It installs the following
packages:

- Java 8 JDK
- Maven
- `pandoc`
- `sed`
- `bison`
- `flex`

## Using the Predict Plugin
#### Setup and Installation
Please run `setup.sh` to install the necessary packages to run the tool. If
your environment does not provide `sudo`, you will need to install the
dependencies manually or run the script in a sudo-capable account.

In this artifact, the bundled jars and native libraries are already provided
under `predict-reqs/`, so `setup.sh` uses those local files instead of
fetching anything from an external URL.

#### Building the Main Jar
From the repository root, build and install the main Sakra jar first:

```bash
cd /path/to/drace-study/sakra
mvn clean install -DskipTests
```

This produces the main jar under `jar/target/`. Rename
`jar/target/jar-2.1.3-SNAPSHOT.jar` to `jar/target/rv-predict.jar`, and use
that file for `-Drv.jar-path` in later steps.

#### Building the Plugin
After the main jar is installed locally, run:

```bash
cd /path/to/drace-study/sakra/plugin
mvn clean install
```

This installs the Maven plugin in your local `m2` repository.

#### Adding to `pom.xml`:
1. Add the rv-predict jar as a java agent to surefire.
2. Add the predict plugin as a plugin.
```
<build>
    <plugins>
        ...
        <plugin>
           <groupId>org.apache.maven.plugins</groupId>
           <artifactId>maven-surefire-plugin</artifactId>
           <version>${surefire-version}</version>
           <configuration>
               <argLine> ${jvmOptions} -javaagent:${rvPath}/rv-predict.jar="${rvOptions}" </argLine>
           </configuration>
        </plugin>

        <plugin>
           <groupId>predict.maven.plugin</groupId>
           <artifactId>predict-maven-plugin</artifactId>
           <version>2.1.3-SNAPSHOT</version>
        </plugin>
    ...
    </plugins>
</build>
```

#### Running with Extension
Instead of adding the jar to `pom.xml`, you can choose to use an extension to
add the jar. Run `mvn clean install` in `extension` to package the extension
jar.

Add `-Drv.jar-path=/path/to/drace-study/sakra/jar/target/rv-predict.jar -Dmaven.ext.class.path=/path/to/drace-study/sakra/extension/target/extension-1.0.jar` to your Maven commands.

If you want profiling output, build the separate `profiling-extension`
project by running `mvn clean install` in `profiling-extension`, then use its
jar instead of the base extension jar. You need to set these environment
variables and make sure the output directory exists:

```bash
export RV_ASYNC_PROFILER_PATH=/path/to/libasyncProfiler.so
export RV_PROFILING_OUTPUT_DIR=/path/to/output/dir
```

Then run Maven with the profiling extension jar.

## Licensing
The upstream RV-Predict sources in this repository are under BSD 3-Clause
terms in [LICENSE.md](LICENSE.md). The Sakra-specific additions
(logging engine for linear-time algorithms, pair finder, test granularity
support, and extensions) in this artifact are covered by
[LICENSE-MIT.md](LICENSE-MIT.md).

### Available Goals
Only current goal is `mvn predict:start`

### Available Options
1. `rv.algorithm` - Allows users to choose which algorithm they want
    - Defaults to RV-Predict (no value needed)
    - **All available algorithms**:
       - SHB - `shb`
       - SHBEpoch - `shbepoch`
       - HB - `hb`
       - HBEpoch - `hbepoch`
       - FHB - `fhb`
       - Lockset - `lockset`
       - Goldilocks - `goldilocks`
       - WCP - `wcp`
       - SyncPreserving - `syncp`
    - Ordering the algorithms by runtime and memory usage:
       - `hb, hbepoch, shb, shbepoch, lockset, fhb | goldilocks, wcp, syncp`
       - `goldilocks`, `wcp`, and `syncp` are very time and memory intensive
2. `rv.base-log-dir` - Path to the base directory where tool creates log directories
    - Default: `/tmp/`
4. `rv.log-dirname` - The name of the directory of where log files are stored
    - Default: `rv-predict` with random numbers
    - i.e. `rv-predict348725878`
6. `rv.include` - List of packages to include during race detection, separated by commas
    - Has higher precedent over exclude
7. `rv.exclude` - List of packages to exclude from race detection, separated by commas
8. `rv.verbose` - Choice of verbose output or not
    - Default: `false`
9. `rv.lib-stacks` - Include library stack frames in the race report (RV-Predict Only)
    - Default: `false`
10. `rv.pair` - Output binary file to use for pair finder
    - Default: `false`
    - Using this option outputs `pair_info.dat`, generate racy pairs with this command:
        - ```java -cp $HOME/rv-predict.jar com.runtimeverification.rvpredict.util.PairInfo ./pair_info.dat ./out.txt```
        - For `lockset` or `goldilocks`, pass the algorithm name as one more argument:
          `java -cp $HOME/rv-predict.jar com.runtimeverification.rvpredict.util.PairInfo ./pair_info.dat ./out.txt lockset`
11. `rv.test-scope` - Reset analysis between tests
    - Use `-Drv.test-scope=method` to reset after each test method, or `-Drv.test-scope=class` to reset after each test class
    - Otherwise, leave unset to keep the default behavior (no reset)

### Example command:

`mvn predict:start -Drv.algorithm=shb -Drv.base-log-dir=project/log -Drv.log-dirname=result -Drv.exclude=org.apache.maven,org.junit,com.runtimeverification.rvpredict -Drv.verbose=true -Drv.jar-path=/path/to/drace-study/sakra/jar/target/rv-predict.jar -Dmaven.ext.class.path=/path/to/drace-study/sakra/extension/target/extension-1.0.jar`

This command will run the plugin using the race detection algorithm SHB.

The selected log directory is `project/log`. Sakra creates `project/log` and
saves output in `project/log/result` if `project/` exists (but fails if not).
Thus a user will need to look in `project/log/result` to find the output logs
of this run.

Finally we set `verbose` to `true`, and since `lib-stacks` is not mentioned,
that parameter will be set to its default value: `false`
