package predict.extension;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Named
@Singleton
public class PredictExtension extends AbstractEventSpy {
    private static final String PREDICT_JAR_STRING =
            "-javaagent:" + System.getProperty("rv.jar-path");

    private static final String PLUGIN_GROUP_ID = "predict.maven.plugin";
    private static final String PLUGIN_ARTIFACT_ID = "predict-maven-plugin";
    private static final String PLUGIN_VERSION = "2.1.3-SNAPSHOT";

    private String getProjectOutputDir() {
        String outputDir = System.getenv("RV_PROFILING_OUTPUT_DIR");
        if (outputDir == null || outputDir.isEmpty()) {
            // Replace with a writable output directory for your environment.
            throw new IllegalStateException("Set RV_PROFILING_OUTPUT_DIR=/path/to/output/dir");
        }

        return outputDir;
    }

    private void addPredictPlugin(MavenProject project) {
        PluginManagement pluginManagement = project.getPluginManagement();
        Plugin plugin = new Plugin();
        plugin.setGroupId(PLUGIN_GROUP_ID);
        plugin.setArtifactId(PLUGIN_ARTIFACT_ID);
        plugin.setVersion(PLUGIN_VERSION);
        pluginManagement.addPlugin(plugin);
    }

    private void updateConfig(Xpp3Dom config) {
        Xpp3Dom argLine = config.getChild("argLine");
        String profilerJar = System.getenv("RV_ASYNC_PROFILER_PATH");
        if (profilerJar == null || profilerJar.isEmpty()) {
            // Replace with the local path to libasyncProfiler.so.
            throw new IllegalStateException("Set RV_ASYNC_PROFILER_PATH=/path/to/libasyncProfiler.so");
        }

        String profilerArg =
                "-agentpath:" + profilerJar +
                "=start,interval=5ms,event=wall,file=" + getProjectOutputDir() + "/profile-%p-%t.jfr";

        if (argLine != null) {
            argLine.setValue(argLine.getValue() + " " + PREDICT_JAR_STRING + " " + profilerArg);
        } else {
            argLine = new Xpp3Dom("argLine");
            argLine.setValue(PREDICT_JAR_STRING + " " + profilerArg);
            config.addChild(argLine);
        }
    }

    private void checkAndUpdateConfiguration(ConfigurationContainer container) {
        Xpp3Dom configNode = (Xpp3Dom) container.getConfiguration();
        if (configNode == null) {
            configNode = new Xpp3Dom("configuration");
            container.setConfiguration(configNode);
        }
        updateConfig(configNode);
    }

    private void updateSurefire(MavenProject project) {
        addPredictPlugin(project);
        for (Plugin plugin : project.getBuildPlugins()) {
            if (plugin.getGroupId().equals("org.apache.maven.plugins")
                    && plugin.getArtifactId().equals("maven-surefire-plugin")) {
                checkAndUpdateConfiguration(plugin);

                for (PluginExecution exe : plugin.getExecutions()) {
                    checkAndUpdateConfiguration(exe);
                }
            }
        }
    }

    @Override
    public void onEvent(Object event) {
        if (event instanceof ExecutionEvent) {
            ExecutionEvent e = (ExecutionEvent) event;
            if (e.getType() == ExecutionEvent.Type.SessionStarted) {
                List<MavenProject> sortedProjects = e.getSession().getProjectDependencyGraph().getSortedProjects();
                for (MavenProject project : sortedProjects) {
                    updateSurefire(project);
                }
            }
        }
    }
}
