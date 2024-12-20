package com.fishsoup.fishweb.util;

import com.fishsoup.fishweb.domain.Footstep;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DDLGenerator {

    public static String generateDDL(Class<?> clazz) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE ").append("f_").append(getTableName(clazz).toLowerCase()).append(" (\n");

        List<String> columns = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            String columnName = getColumnName(field);
            if (Objects.equals("serial_version_uid", columnName)) {
                continue;
            }
            String columnType = getColumnType(field);
            String columnDefinition = columnName + " " + columnType;
            columns.add(columnDefinition);
        }

        ddl.append(String.join(",\n", columns));
        ddl.append(",\nPRIMARY KEY (id)\n);\n");

        return ddl.toString();
    }

    private static String getTableName(Class<?> clazz) {
        return StringUtils.convertCamelToUnderscore(clazz.getSimpleName()).toLowerCase().replaceAll("([a-z])([A-Z]+)", "$1_$2");
    }

    private static String getColumnName(Field field) {
        return StringUtils.convertCamelToUnderscore(field.getName()).toLowerCase();
    }

    private static String getColumnType(Field field) {
        String type = field.getType().getSimpleName();
        return switch (type) {
            case "Integer" -> "INT";
            case "boolean" -> "BOOLEAN";
            case "Date" -> "datetime";
            default -> "VARCHAR(32)";
        };
    }

    public static void main(String[] args) {
        String ddl = generateDDL(Footstep.class);
        System.out.println(ddl);
    }
}