package it.polimi.sr.wasp.vocals.annotations.features;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Param {

    String name();

    boolean uri() default false;
}
