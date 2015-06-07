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

    public void generateSqlContainerClass(String fileName, String packageName, File outputDirectory, Map<String, String> methodNameReturnedStringMap) {
        JCodeModel codeModel = new JCodeModel();
        try {
            JDefinedClass _class = codeModel._class(packageName + "." + getClassName(fileName));
            Set<Map.Entry<String, String>> methodsEntires = methodNameReturnedStringMap.entrySet();
            for (Map.Entry<String, String> methodEntry : methodsEntires) {
                JMethod method = _class.method(JMod.PUBLIC | JMod.STATIC, String.class, methodEntry.getKey());
                String javadoc = "<pre>\n{@code\t" + methodEntry.getValue() + "\n}\n</pre>";
                method.javadoc().addReturn().add(javadoc);
                method.body()._return(JExpr.lit(methodEntry.getValue()));
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
