package it.polimi.rsp.vocals.annotations;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Param {

    String name();

    boolean uri() default false;
}
