package org.elias.mydabase;

import lombok.Getter;

@Getter
public abstract class SqlConfig {
    protected String url;
    protected String user;
    protected String passwd;


    protected String byteType = "binary";
    protected String shortType = "tinyint";
    protected String intType = "int";
    protected String longType = "bigint";
    protected String floatType = "real";
    protected String doubleType = "double";
    protected String BigDecimalType = "decimal";
    protected String booleanType = "bit";
    protected String StringType = "varchar(255)";
    protected String SQLXMLType = "SQLXML";
    protected String TimeType = "time";
    protected String DateType = "date";
    protected String TimestampType = "timestamp";
}
