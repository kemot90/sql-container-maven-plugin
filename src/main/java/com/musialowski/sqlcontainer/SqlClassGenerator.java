package com.musialowski.sqlcontainer;

import com.sun.codemodel.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomasz Musiałowski
 */
public class SqlClassGenerator {

    private final Log logger;

    public SqlClassGenerator(Log logger) {
        this.logger = logger;
    }

    public void generateSqlContainerClass(FileProcessingConfiguration configuration, String fileName, Map<String, String> methodNameReturnedStringMap) {
        JCodeModel codeModel = new JCodeModel();
        String packageName = configuration.getPackageName();
        File outputDirectory = configuration.getOutputDirectory();
        try {
            JDefinedClass _class = codeModel._class(packageName + "." + getClassName(fileName));
            Set<Map.Entry<String, String>> sqlStatementEntries = methodNameReturnedStringMap.entrySet();
            for (Map.Entry<String, String> sqlStatementEntry : sqlStatementEntries) {
                switch (configuration.getStorageMethod()) {
                    case FIELDS:
                        addSqlStaticField(_class, sqlStatementEntry);
                        break;
                    case METHODS:
                        addSqlStaticMethod(_class, sqlStatementEntry);
                        break;
                    default:
                        addSqlStaticField(_class, sqlStatementEntry);
                        addSqlStaticMethod(_class, sqlStatementEntry);
                        break;
                }
            }
            if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                logger.error("Creating output directory '" + outputDirectory.getAbsolutePath() + "' failed.");
                return;
            }
            codeModel.build(outputDirectory);
        } catch (JClassAlreadyExistsException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void addSqlStaticField(JDefinedClass _class, Map.Entry<String, String> sqlStatementEntry) {
        String staticFieldName = toUnderscoreCase(sqlStatementEntry.getKey());
        JFieldVar fieldVar = _class.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, staticFieldName);
        fieldVar.javadoc().append("<pre>{@code\n" + sqlStatementEntry.getValue() + "\n}</pre>");
        fieldVar.init(JExpr.lit(sqlStatementEntry.getValue()));
    }

    private void addSqlStaticMethod(JDefinedClass _class, Map.Entry<String, String> sqlStatementEntry) {
        JMethod method = _class.method(JMod.PUBLIC | JMod.STATIC, String.class, sqlStatementEntry.getKey());
        String javadoc = "<pre>\n{@code\t" + sqlStatementEntry.getValue() + "\n}\n</pre>";
        method.javadoc().addReturn().add(javadoc);
        method.body()._return(JExpr.lit(sqlStatementEntry.getValue()));
    }

    private String toUnderscoreCase(@NonNull String camelCase) {
        if (!StringUtils.isBlank(camelCase)) {
            return camelCase.replaceAll("([A-Z])", "_$1").toUpperCase();
        }
        throw new IllegalArgumentException("Statement key cannot be empty.");
    }

    private String getClassName(String baseName) {
        return baseName.substring(0, 1).toUpperCase() + baseName.substring(1);
    }
}
