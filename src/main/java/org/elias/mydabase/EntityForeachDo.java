package org.elias.mydabase;

@FunctionalInterface
public interface EntityForeachDo<E> {
    void apply(Entity<E> entity,E element);
}