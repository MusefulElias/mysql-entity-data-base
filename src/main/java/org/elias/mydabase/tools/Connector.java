package org.elias.mydabase.tools;


import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.elias.mydabase.SqlConfig;
import org.elias.mydabase.markers.Eobject;
import org.elias.mydabase.markers.MyconConfiguration;
import org.elias.mydabase.markers.PrimaryKey;
import org.elias.mydabase.markers.TypeOf;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.*;

import java.util.*;
import java.util.function.Consumer;

public class Connector {
    private static Connection con;
    private static Map<String, String> typeMap = new HashMap<>();
    private static List<String> sqlList = new ArrayList<>();
    @Setter
    private static boolean showSql = false;

    @SneakyThrows
    public static void toMapping() {
        createTableConstruct();
        setPrimaryKey();
    }

    @SneakyThrows
    private static void putNotStaticField(Class<?> clazz, Statement statement) {
        StringBuilder sql = new StringBuilder("create table ");
        sql.append(clazz.getSimpleName());
        sql.append("(");
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            sql.append(field.getName());
            sql.append(" ");
            if (Checker.hasAnnotation(field, TypeOf.class)) {
                sql.append(field.getAnnotation(TypeOf.class).value());
            } else {
                sql.append(typeMap.get(field.getType().getSimpleName()));
            }
            sql.append(",");
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(");");
        statement.executeUpdate(sql.toString());
    }

    @SneakyThrows
    private static void createTableConstruct() {
        @Cleanup Statement statement = con.createStatement();
        List<Class<?>> classes = Checker.getAnnoFileInJavaPath(Eobject.class);
        for (Class<?> clazz : classes) {
            putNotStaticField(clazz, statement);
        }
    }


    @SneakyThrows
    private static void setPrimaryKey() {
        @Cleanup PreparedStatement prep = con.prepareStatement("ALTER TABLE ? ADD PRIMARY KEY(?);");
        List<Class<?>> classes = Checker.getAnnoFileInJavaPath(Eobject.class);
        commit(() -> {
            for (Class<?> clazz : classes) {
                try {
                    prep.setString(1, clazz.getSimpleName());
                    for (Field field : clazz.getDeclaredFields()) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            continue;
                        }
                        if (!Checker.hasAnnotation(field, PrimaryKey.class)) {
                            continue;
                        }
                        prep.setString(2, field.getName());
                        break;
                    }
                    prep.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @SneakyThrows
    public static void clear() {
        String show_sql = "show tables;";
        String drop_sql = "drop table ";
        @Cleanup Statement statement = con.createStatement();
        @Cleanup ResultSet rs = statement.executeQuery(show_sql);
        List<String> names = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString(1);
            names.add(name);
        }
        if (!names.isEmpty()) {
            commit(() -> {
                for (String name : names) {
                    try {
                        statement.executeUpdate(drop_sql + name);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    @SneakyThrows
    private static void commit(Runnable runnable) {
        con.setAutoCommit(false);
        try {
            runnable.run();
            con.commit();
        } catch (Exception e) {
            con.rollback();
        }
    }

    @SneakyThrows
    private static Connection init() {
        List<Class<?>> classes = Checker.getAnnoFileInJavaPath(MyconConfiguration.class);
        Class<?> clazz = classes.get(0);
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        SqlConfig config = (SqlConfig) constructor.newInstance();
        con = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPasswd());
        initTypeMap(config);
        return con;
    }

    private static void initTypeMap(SqlConfig config) {
        typeMap.put("byte", config.getByteType());
        typeMap.put("short", config.getShortType());
        typeMap.put("int", config.getIntType());
        typeMap.put("float", config.getFloatType());
        typeMap.put("double", config.getDoubleType());
        typeMap.put("BigDecimal", config.getBigDecimalType());
        typeMap.put("boolean", config.getBooleanType());
        typeMap.put("String", config.getStringType());
        typeMap.put("SQLXML", config.getSQLXMLType());
        typeMap.put("Time", config.getTimeType());
        typeMap.put("Date", config.getDateType());
        typeMap.put("Timestamp", config.getTimestampType());
    }


    public static void tryInit() {
        if (con == null)
            init();
    }

    @SneakyThrows
    public static void tryClose() {
        if (con != null)
            con.close();
    }

    @SneakyThrows
    public static boolean hasOriginal() {
        ResultSet tables = con.getMetaData().getTables(null, null, null, new String[]{"table"});
        return tables.next();
    }

    @SneakyThrows
    public static <E> void checkPrep(PreparedStatement prep, Field field, E e, int index) {
        Class<?> type = field.getType();
        Object o = field.get(e);
        if (type == int.class || type == Integer.class) {
            prep.setInt(index, (Integer) o);

        } else if (type == double.class || type == Double.class) {
            prep.setDouble(index, (Double) o);
        } else if (type == short.class || type == Short.class) {
            prep.setShort(index, (Short) o);
        } else if (type == boolean.class || type == Boolean.class) {
            prep.setBoolean(index, (Boolean) o);
        } else if (type == long.class || type == Long.class) {
            prep.setLong(index, (Long) o);
        } else if (type == Blob.class) {
            prep.setBlob(index, (Blob) o);
        } else if (type == Array.class) {
            prep.setArray(index, (Array) o);
        } else if (type == BigDecimal.class) {
            prep.setBigDecimal(index, (BigDecimal) o);
        } else if (type == byte.class || type == Byte.class) {
            prep.setByte(index, (Byte) o);
        } else if (type == float.class || type == Float.class) {
            prep.setFloat(index, (Float) o);
        } else {
            prep.setString(index, o.toString());
        }
    }

    @SneakyThrows
    public static <E> void add(E e) {
        StringBuilder sql = new StringBuilder("insert into ");
        Class<?> clazz = e.getClass();
        sql.append(clazz.getSimpleName());
        sql.append(" value(");
        List<Field> fields = getNoStaticFields(clazz.getDeclaredFields());
        sql.append("?,".repeat(fields.size()));
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(");");
        fillPrepByFields(e, sql, fields,1, 1);
    }

    @SneakyThrows
    private static <E> void fillPrepByFields(E e, StringBuilder sql, List<Field> fields,int startIndex, int interval) {
        con = init();
        PreparedStatement prep = con.prepareStatement(sql.toString());
        for (int i = 0; i < fields.size(); i+=interval) {
            checkPrep(prep, fields.get(i), e, startIndex + i);
        }
        sqlList.add(prep.toString().substring(43));
        prep.close();
        con.close();
    }

    @SneakyThrows
    public static <E> void delete(E e) {
        StringBuilder sql = new StringBuilder("delete from ");
        Class<?> clazz = e.getClass();
        sql.append(clazz.getSimpleName());
        sql.append(" where ");
        List<Field> fields = getNoStaticFields(clazz.getDeclaredFields());
        for (int i = 0; i < fields.size(); i++) {
            sql.append(fields.get(i).getName());
            sql.append(" = ? and ");
        }
        int start = sql.lastIndexOf("and");
        sql.delete(start,start+3);
        sql.append(";");
        sqlList.add("set sql_safe_updates = 0;");
        fillPrepByFields(e, sql, fields,1,1);
    }

    private static List<Field> getNoStaticFields(Field[] clazz) {
        Field[] declaredFields = clazz;
        List<Field> fields = new ArrayList<>();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (!Modifier.isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }
        return fields;
    }

    @SneakyThrows
    public static <E> void update(Consumer<E> consumer, E e) {
            delete(e);
            consumer.accept(e);
            add(e);
    }

    @SneakyThrows
    public static void save() {
        con = init();
        commit(() -> {
            try {
                for (String string : sqlList) {
                    if (showSql) {
                        System.out.println(string);
                    }
                    PreparedStatement prep = con.prepareStatement(string);
                    prep.executeUpdate();
                    prep.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        con.close();
        sqlList.clear();
    }
}
