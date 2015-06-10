package com.musialowski.sqlcontainer;

/**
 * @author Rafał Klukiewicz <rafal.klukiewicz@gmail.com>
 */
public class SqlStatementFieldVar {

    private String name;
    private String comment;
    private String value;

    public SqlStatementFieldVar(String name, String comment, String value) {
        this.name = name;
        this.comment = comment;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getValue() {
        return value;
    }

}
