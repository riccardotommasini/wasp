package it.polimi.sr.wasp.utils;


import it.polimi.rsp.vocals.core.annotations.features.RSPService;

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

    public static String cleanProtocols(String id1) {
        return id1.replace("http://", "")
                .replace("http://", "")
                .replace("https://", "")
                .replace("ws://", "")
                .replace("wss://", "");
    }

    public static boolean isUri(String id) {
        return id.contains("http://") || id.contains("https://");
    }

    public static String getQueryUri(String base, String id) {
        return base + URIUtils.SLASH + "queries" + URIUtils.SLASH + id;
    }

    public static String getStreamUri(String base, String id) {
        return base + URIUtils.SLASH + "streams" + URIUtils.SLASH + id;
    }
}
