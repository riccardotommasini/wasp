package it.polimi.rsp.vocals.annotations;

import it.polimi.rsp.server.HttpMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RSPService {
    String endpoint();

    HttpMethod method() default HttpMethod.GET;
}
