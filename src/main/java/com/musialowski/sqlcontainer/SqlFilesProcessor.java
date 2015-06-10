package com.musialowski.sqlcontainer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tomasz Musiałowski
 */
public class SqlFilesProcessor {

    private static final String DEFAULT_METHOD_DEFINITION_PREFIX = "--#";
    private static final String DEFAULT_EOF = "\n";
    private static final String DEFAULT_SQL_STATEMENT_SEPARATOR = ";";
    private final Log logger;

    public SqlFilesProcessor(Log logger) {
        this.logger = logger;
    }

    public boolean processSqlFile(File sqlFile, File outputDirectory, String packageName) {
        List<String> fileLines;
        try {
            try (InputStream sqlFileInputStream = new FileInputStream(sqlFile)) {
                fileLines = IOUtils.readLines(sqlFileInputStream);
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
            return false;
        }

        Set<SqlStatementFieldVar> sqlStatementFieldVars = new HashSet<>();
        StringBuffer sqlStatementCommentBuffer = new StringBuffer();
        StringBuffer sqlStatementValueBuffer = new StringBuffer();
        String lastestKey = "";
        for (String line : fileLines) {
            if (line.startsWith(DEFAULT_METHOD_DEFINITION_PREFIX)) {
                if (StringUtils.isNotBlank(lastestKey)) {
                    sqlStatementFieldVars.add(new SqlStatementFieldVar(lastestKey, removeLastSqlStatementSeparator(sqlStatementCommentBuffer.toString().trim()), inlineSqlStatement(removeLastSqlStatementSeparator(sqlStatementValueBuffer.toString().trim()))));
                }
                sqlStatementValueBuffer = new StringBuffer();
                sqlStatementCommentBuffer = new StringBuffer();
                String methodName = prepareSqlStatementName(line);
                lastestKey = methodName;
                continue;
            }
            sqlStatementCommentBuffer.append(line).append(DEFAULT_EOF);
            sqlStatementValueBuffer.append(line.trim()).append(" ");
        }
        sqlStatementFieldVars.add(new SqlStatementFieldVar(lastestKey, removeLastSqlStatementSeparator(sqlStatementCommentBuffer.toString().trim()), inlineSqlStatement(removeLastSqlStatementSeparator(sqlStatementValueBuffer.toString().trim()))));

        SqlClassGenerator sqlClassGenerator = new SqlClassGenerator(logger);
        sqlClassGenerator.generateSqlContainerClass(FilenameUtils.removeExtension(sqlFile.getName()), packageName, outputDirectory, sqlStatementFieldVars);
        return true;
    }

    private String removeLastSqlStatementSeparator(String sqlStatement) {
        return StringUtils.reverse(StringUtils.reverse(sqlStatement).replaceFirst(DEFAULT_SQL_STATEMENT_SEPARATOR, ""));
    }

    private String inlineSqlStatement(String sqlStatement) {
        return sqlStatement.replaceAll("[\\t\\n\\r]+", " ");
    }

    private String prepareSqlStatementName(String sqlStatement) {
        return StringUtils.removeStart(sqlStatement, DEFAULT_METHOD_DEFINITION_PREFIX).replaceAll("(.)([A-Z][a-z]+)", "$1_$2").replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase();
    }
}
