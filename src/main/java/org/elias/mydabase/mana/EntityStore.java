package org.elias.mydabase.mana;


import lombok.SneakyThrows;
import org.elias.mydabase.Entity;
import org.elias.mydabase.buyer.EntityBuyer;
import org.elias.mydabase.markers.Eobject;
import org.elias.mydabase.tools.Checker;
import org.elias.mydabase.tools.Connector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityStore {
    private final Map<Class<?>, EntityBuyer> entityMap = new HashMap<>();

    private static EntityStore store;

    private static boolean isOverWrite = false;

    private EntityStore() {
        load();
    }

    public <E> Entity<E> getEntityMap(Class<E> clazz){
        return (Entity<E>)entityMap.get(clazz).get();
    }

    /**
     * 去注册EntityMap对象
     * @param clazz 查找对应类的数据对象集合
     */
    @SneakyThrows
    private void addEntity(Class<?> clazz){
        entityMap.put(clazz, new EntityBuyer(clazz));
    }

    /**
     * 进行覆盖初始化
     */
    private void init(){
        List<Class<?>> classes = Checker.getAnnoFileInJavaPath(Eobject.class);
        classes.forEach(this::addEntity);
        Connector.toMapping();
    }

    /**
     * 对加载进行分支判断
     */
    private void load(){
        Connector.tryInit();
        if(!isOverWrite){
            Connector.tryClose();
            return;
        }
        Connector.clear();
        init();
        Connector.tryClose();
    }

    /**
     * 尝试得到EntityStore, 没有则获得一个新对象
     */
    public static EntityStore tryGet() {
        return store == null ? store = new EntityStore() : store;
    }

    /**
     * 设置是否覆盖原有表数据
     * @param status 覆盖状态
     */
    public static void setOverWrite(boolean status){
        isOverWrite = status;
    }

    public static void setShowSql(boolean status){
        Connector.setShowSql(status);
    }
}
