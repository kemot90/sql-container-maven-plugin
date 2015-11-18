package com.musialowski.sqlcontainer;

import com.musialowski.sqlcontainer.enums.StorageMethod;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Tomasz Musiałowski
 */
@Mojo(name = "sql-container-class-generator", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class SqlContainerPlugin extends AbstractMojo {

    @Parameter(required = true, defaultValue = "${project.build.directory}/generated-sources/sql")
    private File outputDirectory;

    @Parameter
    private List sqlResources;

    @Parameter
    private String packageName;

    @Parameter(defaultValue = "both")
    private String storageMethod;

    private SqlFilesProcessor sqlFilesProcessor = new SqlFilesProcessor(getLog());

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (outputDirectory != null) {
            getLog().info("Your output directory: " + outputDirectory.getAbsolutePath());
        } else {
            getLog().info("Your output directory is not set.");
        }

        if (sqlResources != null) {
            List<String> resourcesPaths = (List<String>) (List<?>) sqlResources;
            List<File> sqlResources = new LinkedList<>();
            for (String path : resourcesPaths) {
                String fixedPath = path.replace("/", File.separator);
                sqlResources.add(new File(fixedPath));
            }
            FileProcessingConfiguration configuration = FileProcessingConfiguration.builder()
                    .outputDirectory(outputDirectory)
                    .packageName(packageName)
                    .storageMethod(getStorageMethod(storageMethod))
                    .build();
            if (!sqlFilesProcessor.processSqlFile(configuration, sqlResources)) {
                getLog().error("Generating class from SQL files failed.");
            }
        }
    }

    private StorageMethod getStorageMethod(String storageMethodLiteral) {
        return StorageMethod.valueOf(storageMethodLiteral.toUpperCase());
    }
}
