package predict.extension;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Dependency;
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

    private static final String JUNIT4_LISTENER =
            "com.runtimeverification.rvpredict.instrument.RVPredictJUnit4BoundaryListener";

    private static final String PLUGIN_GROUP_ID = "predict.maven.plugin";
    private static final String PLUGIN_ARTIFACT_ID = "predict-maven-plugin";
    private static final String PLUGIN_VERSION = "2.1.3-SNAPSHOT";

    private boolean hasJUnit4(MavenProject project) {
        for (Dependency dependency : project.getDependencies()) {
            if ("junit".equals(dependency.getGroupId())
                    && "junit".equals(dependency.getArtifactId())) {
                return true;
            }
        }
        return false;
    }

    private void addJUnit4Listener(ConfigurationContainer container) {
        if (System.getProperty("rv.test-scope") == null) {
            return;
        }

        Xpp3Dom config = (Xpp3Dom) container.getConfiguration();
        if (config == null) {
            config = new Xpp3Dom("configuration");
            container.setConfiguration(config);
        }

        Xpp3Dom properties = config.getChild("properties");
        if (properties == null) {
            properties = new Xpp3Dom("properties");
            config.addChild(properties);
        }

        for (Xpp3Dom propertyNode : properties.getChildren()) {
            Xpp3Dom nameNode = propertyNode.getChild("name");
            if (nameNode != null && "listener".equals(nameNode.getValue())) {
                Xpp3Dom valueNode = propertyNode.getChild("value");
                if (valueNode == null) {
                    valueNode = new Xpp3Dom("value");
                    propertyNode.addChild(valueNode);
                }

                String current = valueNode.getValue();
                if (current == null || current.trim().isEmpty()) {
                    valueNode.setValue(JUNIT4_LISTENER);
                } else if (!current.contains(JUNIT4_LISTENER)) {
                    valueNode.setValue(current + "," + JUNIT4_LISTENER);
                }
                return;
            }
        }

        Xpp3Dom property = new Xpp3Dom("property");
        Xpp3Dom name = new Xpp3Dom("name");
        name.setValue("listener");
        Xpp3Dom value = new Xpp3Dom("value");
        value.setValue(JUNIT4_LISTENER);
        property.addChild(name);
        property.addChild(value);
        properties.addChild(property);
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
        if (argLine != null) {
            argLine.setValue(argLine.getValue() + " " + PREDICT_JAR_STRING);
        } else {
            argLine = new Xpp3Dom("argLine");
            argLine.setValue(PREDICT_JAR_STRING);
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
        boolean junit4Project = hasJUnit4(project);

        for (Plugin plugin : project.getBuildPlugins()) {
            if (plugin.getGroupId().equals("org.apache.maven.plugins") &&
                    plugin.getArtifactId().equals("maven-surefire-plugin")) {
                checkAndUpdateConfiguration(plugin);
                if (junit4Project) {
                    addJUnit4Listener(plugin);
                }

                for (PluginExecution exe : plugin.getExecutions()) {
                    checkAndUpdateConfiguration(exe);
                    if (junit4Project) {
                        addJUnit4Listener(exe);
                    }
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
