package com.musialowski.sqlcontainer;

import com.sun.codemodel.*;
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

    public void generateSqlContainerClass(String fileName, String packageName, File outputDirectory, Set<SqlStatementFieldVar> sqlStatementFieldVars) {
        JCodeModel codeModel = new JCodeModel();
        try {
            JDefinedClass _class = codeModel._class(packageName + "." + getClassName(fileName));
            for (SqlStatementFieldVar sqlStatementFieldVar : sqlStatementFieldVars) {
                JFieldVar fieldVar = _class.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, sqlStatementFieldVar.getName());
                fieldVar.javadoc().append("<pre>{@code\n" + sqlStatementFieldVar.getComment() + "\n}</pre>");
                fieldVar.init(JExpr.lit(sqlStatementFieldVar.getValue()));
            }
            outputDirectory.mkdirs();
            codeModel.build(outputDirectory);
        } catch (JClassAlreadyExistsException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private String getClassName(String baseName) {
        return baseName.substring(0, 1).toUpperCase() + baseName.substring(1);
    }
}
