package it.polimi.sr.wasp.rsp.publisher.syntax;

import org.parboiled.Rule;

public class ExtendedLexter extends Lexer<StreamPublicationBuilder> {

    public Rule STREAM() {
        return StringIgnoreCaseWS("STREAM");
    }

    public Rule REGISTER() {
        return StringIgnoreCaseWS("REGISTER");
    }

    public Rule SOURCE() {
        return StringIgnoreCaseWS("SOURCE");
    }

    public Rule THIS() {
        return StringIgnoreCaseWS("THIS");
    }

    public Rule PUBLISHER() {
        return StringIgnoreCaseWS("PUBLISHER");
    }
}
