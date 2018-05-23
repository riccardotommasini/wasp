package it.polimi.rsp.vocals.annotations.features;

import it.polimi.rsp.server.enums.HttpMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RSPService {
    String endpoint();
    HttpMethod method() default HttpMethod.GET;
}
