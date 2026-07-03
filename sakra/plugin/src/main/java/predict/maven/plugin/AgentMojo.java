package predict.maven.plugin;

import com.runtimeverification.rvpredict.instrument.AgentLoader;
import com.runtimeverification.rvpredict.util.Logger;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.List;

/**
 * MOJO for attaching the agent
 * Please use "start" to attach the agent and run tests
 */

@Mojo(name = "predict", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class AgentMojo extends AbstractMojo {

    private Logger logger;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    /**
     * The name of the algorithm we want to run with, default to RV-Predict
     */
    @Parameter(property = "rv.algorithm", defaultValue = "rvpredict", required = true)
    String algorithm;

    /**
     * Path to the base directory where tool creates log directories
     * Defaults to /tmp/
     */
    @Parameter(property = "rv.base-log-dir", defaultValue = "/tmp/")
    String baseLogDir;

    /**
     * The name of the directory of where log files are stored
     * Defaults to rv-predict + string of random numbers
     */
    @Parameter(property = "rv.log-dirname", defaultValue = "rv-predict...")
    String logDirname;

    /**
     * List of packages to include during race detection, separated by commas
     * Has higher precedent over exclude
     */
    @Parameter(property = "rv.exclude", defaultValue = "")
    String include;

    /**
     * List of packages to exclude from race detection, separated by commas
     */
    @Parameter(property = "rv.exclude", defaultValue = "")
    String exclude;

    /**
     * Choice of verbose output or not (true or false)
     */
    @Parameter(property = "rv.verbose", defaultValue = "false")
    String verboseLevel;

    /**
     * Include library stack frames in the race report (RV-Predict Only)
     */
    @Parameter(property = "rv.lib-stacks", defaultValue = "false")
    String libStacks;

    /**
     * Write event pair information to a file (Rapid Algorithms Only)
     * (true or false)
     */
    @Parameter(property = "rv.pair", defaultValue = "false")
    String pair;

    /**
     * Log the execution trace of the program
     * (true or false)
     */
    @Parameter(property = "rv.log", defaultValue = "false")
    String log;

    public void execute() throws MojoExecutionException {
        this.logger = Logger.getGlobal();

        if (!algorithm.equals("rvpredict")) {
            System.setProperty("rv.window", "no-window");
        } else {
            System.setProperty("rv.window", "");
        }

        List<MavenProject> projects = mavenSession.getAllProjects();
        MavenProject targetProject = projects.get(0);
        getLog().info(projects.get(0).toString());

        List<Plugin> plugins = targetProject.getBuildPlugins();
        String version = "";
        Xpp3Dom config = new Xpp3Dom("configuration");
        for (Plugin plugin : plugins) {
            if (plugin.getArtifactId().equals("maven-surefire-plugin")) {
                version = plugin.getVersion();
                config = (Xpp3Dom) plugin.getConfiguration();

                getLog().info("Surefire Version: " + version);
                break;
            }
        }

        getLog().info("Agent is being loaded");
        startAgent();

        executeMojo(
            plugin(
                groupId("org.apache.maven.plugins"),
                artifactId("maven-surefire-plugin"),
                version(version)
            ),
            goal("test"),
            config,
            executionEnvironment(
                mavenProject,
                mavenSession,
                pluginManager
            )
        );
    }

    private void startAgent() throws MojoExecutionException {
        if (AgentLoader.loadDynamicAgent()) {
            logger.report("AGENT LOADED", Logger.MSGTYPE.VERBOSE);
        } else {
            throw new MojoExecutionException("COULD NOT ATTACH THE AGENT");
        }
    }
}
