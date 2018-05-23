package it.polimi.sr.wasp.vocals.annotations.features;

import it.polimi.sr.wasp.server.enums.HttpMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RSPService {
    String endpoint();
    HttpMethod method() default HttpMethod.GET;
}
