package it.polimi.rsp.vocals.annotations.services;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ProcessingService {
    String host();

    int port() default 4000;
}
