package com.musialowski.sqlcontainer;

import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tomasz Musiałowski
 */
public class SqlFilesProcessor {

    private final Log logger;
    private static final String METHOD_NAME_PREFIX = "--#";
    private static final String NEW_LINE = "\n";
    private static final String SQL_PLACEHOLDER = "";

    public SqlFilesProcessor(Log logger) {
        this.logger = logger;
    }

    public boolean processSqlFile(@NonNull FileProcessingConfiguration configuration, @NonNull List<File> sqlResources) {

        for (File sqlFile : sqlResources) {
            if (!sqlFile.isFile()) {
                logger.error("Given path '" + sqlFile.getAbsolutePath() + "' is not a file.");
                return false;
            }
            List<String> fileLines;
            InputStream sqlFileInputStream = null;
            Map<String, String> methodNameReturnedStringMap = new HashMap<>();
            try {
                fileLines = IOUtils.readLines(sqlFileInputStream = new FileInputStream(sqlFile));
            } catch (IOException ex) {
                logger.error(ex.getLocalizedMessage(), ex);
                IOUtils.closeQuietly(sqlFileInputStream);
                return false;
            }
            StringBuffer sqlStatementBuffer = new StringBuffer();
            String latestKey = "";
            for (String line : fileLines) {
                line = StringUtils.remove(line, ";");
                if (line.startsWith(METHOD_NAME_PREFIX)) {
                    methodNameReturnedStringMap.put(latestKey, sqlStatementBuffer.toString());
                    sqlStatementBuffer = new StringBuffer();
                    String methodName = StringUtils.removeStart(line, METHOD_NAME_PREFIX);
                    latestKey = methodName;
                    methodNameReturnedStringMap.put(methodName, SQL_PLACEHOLDER);
                    continue;
                }
                sqlStatementBuffer.append(line).append(NEW_LINE);
            }
            methodNameReturnedStringMap.put(latestKey, sqlStatementBuffer.toString());
            methodNameReturnedStringMap.remove("");
            SqlClassGenerator sqlClassGenerator = new SqlClassGenerator(logger);
            sqlClassGenerator.generateSqlContainerClass(
                    configuration,
                    FilenameUtils.removeExtension(sqlFile.getName()),
                    methodNameReturnedStringMap);
        }
        return true;
    }
}
