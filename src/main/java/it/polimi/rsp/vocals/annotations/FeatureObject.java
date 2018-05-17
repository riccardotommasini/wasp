package it.polimi.rsp.vocals.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureObject {

    Class<?> input() default String.class;

}
