package org.elias.mydabase.buyer;

import lombok.SneakyThrows;
import org.elias.mydabase.Entity;
import org.elias.mydabase.entity.model.NorEntity;

public class EntityBuyer {
    private final Object object;
    @SneakyThrows
    public EntityBuyer(Class<?> clazz){
        var constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        var instance = constructor.newInstance();
        object =  Entity.create(instance);
    }

    public Entity get(){
        return (Entity) object;
    }
}
