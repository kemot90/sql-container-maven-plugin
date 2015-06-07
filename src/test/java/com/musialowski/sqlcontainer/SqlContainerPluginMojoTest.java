package com.musialowski.sqlcontainer;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

public class SqlContainerPluginMojoTest {

    @Rule
    public MojoRule rule = new MojoRule();

    @Test
    public void testMojo() throws Exception {
        String projectBaseDir = System.getProperty("user.dir");
        String basePath = this.getClass().getClassLoader().getResource("").getFile();
        File pom = new File(basePath);
        SqlContainerPlugin sqlContainerPlugin = (SqlContainerPlugin) rule.lookupConfiguredMojo(pom, "sql-container-class-generator");
        sqlContainerPlugin.execute();
    }
}