package org.elias.mydabase;


import lombok.Setter;
import org.elias.mydabase.entity.model.NorEntity;
import org.elias.mydabase.tools.Connector;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Entity<E> {
    @Setter
    private static String type = NorEntity.class.getSimpleName();
    public static <E> Entity<E> create(E e){
        if(type.equals(NorEntity.class.getSimpleName())){
            return new NorEntity<>(e);
        }
        return new NorEntity<>(e);
    }

    public abstract Entity<E> add(E e);
    public abstract Entity<E> add(Collection<E> es);

    public abstract Entity<E> delete();
    public abstract Entity<E> delete(E e);
    public abstract Entity<E> delete(Collection<E> es);

    public abstract Entity<E> update(E e);
    public abstract Entity<E> update(Collection<E> es);
    public abstract Entity<E> update(Consumer<E> consumer);

    public abstract Entity<E> select();
    public abstract Entity<E> select(Class<?> clazz);
    public abstract Entity<E> select(E e);
    public abstract Entity<E> select(Predicate<E> predicate);

    public abstract Entity<E> foreachDo(EntityForeachDo<E> entityForeachDo);


    public abstract Collection<E> toCollection();
    public abstract Entity<E> save();
}
