package predict.maven.plugin;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;

/**
 * This Mojo is used to define the lifecycle.
 * There is a need for a Mojo that is
 * executed in test phase to active execution of tests.
 */
@Mojo(name = "start", defaultPhase = LifecyclePhase.TEST)
@Execute(goal = "start", phase = LifecyclePhase.TEST, lifecycle = "modify-arg")
public class LifecycleMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException {
        // Nothing.
    }
}
