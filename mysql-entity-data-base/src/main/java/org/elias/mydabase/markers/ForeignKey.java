package org.elias.mydabase.markers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
    Mode deleteMode();
    Mode updateMode();
    Class<?> relyType();
    enum Mode{
        NO_ACTION,
        CASCADE,
        SET_NULL,
        SET_DEFAULT
    }
}
