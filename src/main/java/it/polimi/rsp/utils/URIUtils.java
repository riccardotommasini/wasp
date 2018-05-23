package it.polimi.rsp.utils;

import it.polimi.rsp.vocals.annotations.features.RSPService;

import java.lang.reflect.Method;

public class URIUtils {

    /* Properties */
    public static final String SLASH = "/";

    public static final String COLON = ":";

    public static String addParam(String uri, String param) {
        return uri + SLASH + COLON + param;
    }

    public static String build(Method m, String base, String param) {
        RSPService service = m.getAnnotation(RSPService.class);
        return base + SLASH + service + SLASH + param;
    }
}
