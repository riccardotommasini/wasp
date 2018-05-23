package it.polimi.sr.wasp.vocals.annotations.services;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PublishingService {
    String host();

    int port() default 4000;
}
