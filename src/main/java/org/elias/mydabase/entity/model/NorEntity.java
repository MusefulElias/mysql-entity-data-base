package org.elias.mydabase.entity.model;

import org.elias.mydabase.Entity;
import org.elias.mydabase.EntityForeachDo;
import org.elias.mydabase.mana.EntityStore;
import org.elias.mydabase.tools.Connector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class NorEntity<E> extends Entity<E> {
    private List<E> list = new ArrayList<>();
    private E eObject;

    private NorEntity() {
    }

    private NorEntity(List<E> list) {
        this.list = list;
        this.eObject = list.getFirst();
    }

    public NorEntity(E e) {
        list.add(e);
        list.remove(e);
        this.eObject = e;
    }


    @Override
    public Entity<E> add(E e) {
        boolean status = select(e.getClass()).toCollection().add(e);
        if (status) {
            Connector.add(e);
        }
        return this;
    }

    @Override
    public Entity<E> add(Collection<E> es) {
        select(es.stream().findFirst().getClass()).toCollection().addAll(es);
        return this;
    }

    @Override
    public Entity<E> delete() {
        Collection<E> collection = select(eObject.getClass()).toCollection();
        for (E e : collection) {
            Connector.delete(e);
        }
        collection.clear();
        return this;
    }

    @Override
    public Entity<E> delete(E e) {
        boolean status = select(e.getClass()).toCollection().remove(e);
        if (status) {
            Connector.delete(e);
        }
        return this;
    }

    @Override
    public Entity<E> delete(Collection<E> es) {
        Collection<E> collection = select(eObject.getClass()).toCollection();
        for (E e : es) {
            boolean status = collection.remove(e);
            if (status) {
                Connector.delete(e);
            }
        }
        return this;
    }

    @Override
    public Entity<E> update(E e) {
        if (select().toCollection().contains(e)) {
            return this;
        }
        if (select().toCollection().size() == 1) {
            delete();
            add(e);
        }
        return this;
    }

    @Override
    public Entity<E> update(Collection<E> es) {
        delete();
        add(es);
        return this;
    }

    @Override
    public Entity<E> update(Consumer<E> consumer) {
        for (E e : list) {
            Connector.update(consumer, e);
        }
        return this;
    }

    @Override
    public Entity<E> select() {
        return select(eObject.getClass());
    }


    @Override
    public Entity<E> select(Class<?> clazz) {
        return (Entity<E>) EntityStore.tryGet().getEntityMap(clazz);
    }

    @Override
    public Entity<E> select(E e) {
        if (list.contains(e)) {
            List<E> newList = Collections.singletonList(e);
            return new NorEntity<>(newList);
        }
        return this;
    }

    @Override
    public Entity<E> select(Predicate<E> predicate) {
        List<E> newlist = list.stream().filter(predicate).toList();
        return new NorEntity<>(newlist);
    }

    @Override
    public Entity<E> foreachDo(EntityForeachDo<E> entityForeachDo) {
        for (E e : list) {
            entityForeachDo.apply(select(e.getClass()), e);
        }
        return this;
    }

    @Override
    public Collection<E> toCollection() {
        return list;
    }

    @Override
    public Entity<E> save() {
        Connector.save();
        return select(eObject.getClass());
    }
}
