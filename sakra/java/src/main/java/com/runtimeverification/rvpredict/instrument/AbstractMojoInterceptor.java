package com.runtimeverification.rvpredict.instrument;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.List;

/*
 * This class is copied from STARTs:
 * https://github.com/TestingResearchIllinois/starts/blob/master/starts-core/src/main/java/edu/illinois/starts/maven/AbstractMojoInterceptor.java
 * Removed the LOGGER since it was unused
 */

public abstract class AbstractMojoInterceptor {

    public static URL extractJarURL(URL url) throws IOException {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        return connection.getJarFileURL();
    }

    public static URL extractJarURL(Class<?> clz) throws IOException {
        return extractJarURL(getResource(clz));
    }

    public static URL getResource(Class<?> clz) {
        URL resource = clz.getResource("/" + clz.getName().replace('.', File.separatorChar) + ".class");
        return resource;
    }

    protected static void throwMojoExecutionException(Object mojo, String message, Exception cause) throws Exception {
        Class<?> clz = mojo.getClass().getClassLoader().loadClass("org.apache.maven.plugin.MojoExecutionException");
        Constructor<?> con = clz.getConstructor(String.class, Exception.class);
        Exception ex = (Exception) con.newInstance(message, cause);
        throw ex;
    }

    protected static void setField(String fieldName, Object mojo, Object value) throws Exception {
        Field field;
        try {
            field = mojo.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            field = mojo.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        field.set(mojo, value);
    }

    protected static Object getField(String fieldName, Object mojo) throws Exception {
        Field field;
        try {
            field = mojo.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            field = mojo.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        return field.get(mojo);
    }

    protected static List<String> getListField(String fieldName, Object mojo) throws Exception {
        return (List<String>) getField(fieldName, mojo);
    }
}
