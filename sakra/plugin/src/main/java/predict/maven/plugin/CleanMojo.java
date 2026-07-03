package predict.maven.plugin;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;

/**
 * Removes default output directory for predict
 * /tmp/rv-predict*
 */
@Mojo(name = "clean", requiresDirectInvocation = true)
public class CleanMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException {
        File directory = new File(File.separator + "tmp" + File.separator);
        for (File f : directory.listFiles()) {
            if (f.getName().startsWith("rv-predict")) {
                getLog().info("Deleting: " + File.separator + "tmp" + File.separator + f.getName());
                try {
                    FileUtils.deleteDirectory(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
