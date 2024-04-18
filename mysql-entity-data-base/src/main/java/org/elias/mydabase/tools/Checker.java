package org.elias.mydabase.tools;

import lombok.SneakyThrows;
import org.elias.mydabase.markers.MyconConfiguration;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Checker {

    public static <A extends java. lang. annotation. Annotation> boolean hasAnnotation(Field field,Class<A> annotationClass){
        return field.getDeclaredAnnotation(annotationClass) == null ? false : true;
    }




    /**
     * 判断某个类是否被某个注解标注
     * @param clazz
     * @param annotationClass
     * @return
     */
    public static <A extends java. lang. annotation. Annotation> boolean hasAnnotation(Class<?> clazz, Class<A> annotationClass) {
        return clazz.getDeclaredAnnotation(annotationClass) == null ? false : true;
    }

    /**
     * 获取完整类名
     * @param files
     * @return
     */
    public static List<String> getTypeName(List<File> files){
        List<String> typeNames = new ArrayList<>();
        files.forEach(file -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                try {
                    String head = bufferedReader.readLine();
                    if (!head.matches("package\\s+.+;")) {
                        System.out.println("Error: Checker.getPageName(List<File> files) First line format error.");
                    } else {
                        String[] split = head.split("\\s|;");
                        int length = file.getName().length() - 5;
                        typeNames.add(split[1]+"."+file.getName().substring(0,length));
                    }
                } catch (IOException e) {
                    System.out.println("Error: Checker.getPageName(List<File> files) File's content is null.");
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: Checker.getPageName(List<File> files) Don't open file.");
            }

        });
        return typeNames;
    }

    /**
     * 得到src/main/java下所有带指定注解的类
     * @param annoClass
     * @return
     */
    @SneakyThrows
    public static <A extends java. lang. annotation. Annotation> List<Class<?>> getAnnoFileInJavaPath(Class<A> annoClass){
        List<File> files = filterFile(searchFile("src/main/java"), ".java");
        List<String> typeNames = getTypeName(files);
        List<Class<?>> classes = new ArrayList<>();
        for (String typeName : typeNames) {
            Class<?> clazz = Class.forName(typeName);
            if (hasAnnotation(clazz, annoClass)) {
                classes.add(clazz);
            }
        }
        return classes;
    }


    /**
     * 用于筛选指定后缀名的文件
     * @param fileList
     * @param dis
     * @return
     */
    public static List<File> filterFile(List<File> fileList,String dis){
        return fileList.stream().filter(file -> file.getName().endsWith(dis)).toList();
    }

    /**
     *  用于通过路径来搜索文件夹下的所有文件
     * @param path
     * @return
     */
    public static List<File> searchFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("This is not exists!");
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>();
        fileList = searchFile(file, fileList);
        if (fileList == null) {
            System.out.println("This is a empty directory.");
            return new ArrayList<>();
        }
        return fileList;
    }


    /**
     * 检查文件夹下所有文件并收集，如果是文件夹就忽略
     * @param file  搜索的文件夹源
     * @param fileList  文件收集器
     * @return 将传进来的fileList返回
     */
    public static List<File> searchFile(File file, List<File> fileList) {
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                searchFile(f, fileList);
            } else {
                fileList.add(f);
            }
        }
        return fileList;
    }
}
