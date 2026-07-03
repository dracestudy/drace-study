package com.runtimeverification.rvpredict.instrument;

import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.runner.Description;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class TestBoundaryTracker {
    private final Map<String, Integer> remainingTestsByClass = new HashMap<>();

    synchronized void registerJUnit4Tree(Description description) {
        remainingTestsByClass.clear();
        if (description != null) {
            collectJUnit4(description);
        }
    }

    synchronized void registerJUnit5Plan(TestPlan plan) {
        remainingTestsByClass.clear();
        if (plan == null) {
            return;
        }
        for (TestIdentifier root : plan.getRoots()) {
            collectJUnit5(plan, root);
        }
    }

    synchronized void registerJUnit5DynamicTest(TestPlan plan, TestIdentifier id) {
        if (plan == null || id == null || !id.isTest()) {
            return;
        }
        String className = resolveJUnit5ClassNameInternal(plan, id);
        if (className != null) {
            increment(className);
        }
    }

    synchronized boolean methodFinished(String className) {
        Integer remaining = remainingTestsByClass.get(className);
        if (remaining == null) {
            return false;
        }
        remaining = remaining - 1;
        if (remaining <= 0) {
            remainingTestsByClass.remove(className);
            return true;
        }
        remainingTestsByClass.put(className, remaining);
        return false;
    }

    synchronized String resolveJUnit5ClassName(TestPlan plan, TestIdentifier id) {
        return resolveJUnit5ClassNameInternal(plan, id);
    }

    private void collectJUnit4(Description description) {
        if (description.isTest()) {
            String className = description.getClassName();
            if (className != null) {
                increment(className);
            }
            return;
        }
        for (Description child : description.getChildren()) {
            collectJUnit4(child);
        }
    }

    private void collectJUnit5(TestPlan plan, TestIdentifier node) {
        if (node.isTest()) {
            String className = resolveJUnit5ClassNameInternal(plan, node);
            if (className != null) {
                increment(className);
            }
        }
        for (TestIdentifier child : plan.getChildren(node)) {
            collectJUnit5(plan, child);
        }
    }

    private void increment(String className) {
        Integer current = remainingTestsByClass.get(className);
        remainingTestsByClass.put(className, current == null ? 1 : current + 1);
    }

    private String resolveJUnit5ClassNameInternal(TestPlan plan, TestIdentifier id) {
        TestIdentifier current = id;
        while (current != null) {
            String className = classNameFromSource(current.getSource().orElse(null));
            if (className != null) {
                return className;
            }
            Optional<TestIdentifier> parent = plan.getParent(current);
            current = parent.orElse(null);
        }
        return null;
    }

    private String classNameFromSource(TestSource source) {
        if (source instanceof MethodSource) {
            return ((MethodSource) source).getClassName();
        }
        if (source instanceof ClassSource) {
            return ((ClassSource) source).getClassName();
        }
        return null;
    }
}
