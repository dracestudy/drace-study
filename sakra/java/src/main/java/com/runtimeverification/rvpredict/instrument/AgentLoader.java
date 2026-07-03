package com.runtimeverification.rvpredict.instrument;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;

import com.sun.tools.attach.VirtualMachine;

/**
 * This class is duplicated from Ekstazi:
 * https://github.com/gliga/ekstazi/blob/6567da0534c20eeee802d2dfb8d216cbcbf6883c/org.ekstazi.core/src/main/java/org/ekstazi/agent/AgentLoader.java
 * Simplified class to add VirtualMachine
 * Loading the Agent is easier and cleaner
 */
public final class AgentLoader {
    private static final String AGENT_INIT = AgentLoader.class.getName() + " Initialized";

    public static boolean loadDynamicAgent() {
        try {
            if (System.getProperty(AGENT_INIT) != null) {
                return true;
            }
            System.setProperty(AGENT_INIT, "");

            URL agentJarURL = AbstractMojoInterceptor.class.getResource("SurefireAgent.class");
            URL agentJarURLConnection = AbstractMojoInterceptor.extractJarURL(agentJarURL);
            return loadAndAttachAgent(agentJarURLConnection);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean loadAndAttachAgent(URL aju) throws Exception {
        String pid = getPID();
        String agentAbsolutePath = new File(aju.toURI().getSchemeSpecificPart()).getAbsolutePath();
        System.out.println(agentAbsolutePath);
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentAbsolutePath);
        vm.detach();

        return true;
    }

    private static String getPID() {
        String vmName = ManagementFactory.getRuntimeMXBean().getName();
        return vmName.substring(0, vmName.indexOf("@"));
    }
}
