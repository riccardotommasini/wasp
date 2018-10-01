package it.polimi.sr.wasp.rsp.publisher.syntax;

import org.parboiled.BaseParser;

/**
 * Created by Riccardo on 09/08/16.
 */
public class ParserUtils<T> extends BaseParser<Object> {

    // This is the map used allocate blank node labels during sparql11.
    // 1/ It is different between CONSTRUCT and the query pattern
    // 2/ Each BasicGraphPattern is a scope for blank node labels so each
    // BGP causes the map to be cleared at the start of the BGP

    public T getQuery(int i) {
        if (i == -1) {
            int size = getContext().getValueStack().size();
            i = size > 0 ? size - 1 : 0;
        }
        return (T) peek(i);
    }

    public T popQuery(int i) {
        if (i == -1) {
            int size = getContext().getValueStack().size();
            i = size > 0 ? size - 1 : 0;
        }
        return (T) pop(i);
    }

    public boolean pushQuery(T q) {
        return push(0, q);
    }

    public String trimMatch() {
        String trim = match().trim();
        return trim;
    }

    public String stringMatch() {
        String trim = match().trim();
        return trim.substring(1, trim.length() - 1);
    }

    public String URIMatch() {
        return resolve(trimMatch().replace(">", "").replace("<", ""));
    }

    private String resolve(String replace) {
        return null;
    }

}
