package it.polimi.sr.wasp.rsp.publisher.syntax;/*
 * Copyright (c) 2009 Ken Wenzel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.parboiled.Rule;

/**
 * SPARQL Parser
 *
 * @author Ken Wenzel, adapted by Mathias Doenitz
 */
@SuppressWarnings({"InfiniteRecursion"})
public class Parser extends ExtendedLexter {
    // <Parser>

    public Rule Query() {
        return Sequence(push(new StreamPublicationBuilder()), WS(), Prologue(), RegisterClause(), SourceClause(), SGraph(), EOI);
    }

    public Rule SourceClause() {
        return Sequence(FROM(), SOURCE(), IriRef(), pushQuery(((StreamPublicationBuilder) pop(0)).setSource(trimMatch())));
    }

    public Rule RegisterClause() {
        return Sequence(REGISTER(), STREAM(), IriRef(), pushQuery(((StreamPublicationBuilder) pop(0)).setId(trimMatch())));
    }

    public Rule Prologue() {
        return Sequence(Optional(BaseDecl()), ZeroOrMore(PrefixDecl()));
    }

    public Rule BaseDecl() {
        return Sequence(BASE(), IRI_REF(),
                pushQuery(((StreamPublicationBuilder) pop(0)).setBaseURI(trimMatch())), WS());
    }

    public Rule PrefixDecl() {
        return Sequence(PREFIX(), PNAME_NS(), pushQuery(((StreamPublicationBuilder) pop(0)).addPrefix(StreamPublicationBuilder.TODO_PREFIX, trimMatch())), IRI_REF(), pushQuery(((StreamPublicationBuilder) pop(0)).addPrefix(trimMatch(), trimMatch())));
    }

    public Rule SGraph() {
        return Sequence(Optional(WHERE()), OPEN_CURLY_BRACE(),
                push(new StreamPublicationBuilder.QuadBuilder()),
                Optional(
                        Sequence(TriplesBlock(),
                                pushQuery(popQuery(-1).addQuad((StreamPublicationBuilder.QuadBuilder) pop())))),
                ZeroOrMore(Sequence(
                        GraphGraphPattern(),
                        Optional(DOT()), Optional(
                                Sequence(TriplesBlock(),
                                        pushQuery(popQuery(-1).addQuad((StreamPublicationBuilder.QuadBuilder) pop()))))
                )),

                CLOSE_CURLY_BRACE()
        );
    }

    public Rule GraphGraphPattern() {
        return Sequence(GRAPH(), IriRef(), push(new StreamPublicationBuilder.QuadBuilder(trimMatch())), GroupGraphPattern(), pushQuery(popQuery(-1).addQuad((StreamPublicationBuilder.QuadBuilder) pop())));
    }

    public Rule GroupGraphPattern() {
        return Sequence(OPEN_CURLY_BRACE(), TriplesBlock(), CLOSE_CURLY_BRACE());
    }

    public Rule TriplesBlock() {
        return Sequence(TriplesSameSubject(), Optional(DOT()), Optional(TriplesBlock()));
    }


    public Rule TriplesSameSubject() {
        return FirstOf(Sequence(GraphTerm(),
                push(((StreamPublicationBuilder.QuadBuilder) pop()).subject(trimMatch())),
                PropertyListNotEmpty()),
                Sequence(TriplesNode(), PropertyList()));
    }

    public Rule PropertyListNotEmpty() {
        return Sequence(Verb(),
                push(((StreamPublicationBuilder.QuadBuilder) pop()).predicate(trimMatch())),
                ObjectList(), ZeroOrMore(Sequence(SEMICOLON(),
                        Optional(Sequence(Verb(),
                                push(((StreamPublicationBuilder.QuadBuilder) pop()).predicate(trimMatch())),
                                ObjectList())))));
    }

    public Rule PropertyList() {
        return Optional(PropertyListNotEmpty());
    }

    public Rule ObjectList() {
        return Sequence(Object_(), push(((StreamPublicationBuilder.QuadBuilder) pop()).object(trimMatch())), ZeroOrMore(Sequence(COMMA(), Object_(), push(((StreamPublicationBuilder.QuadBuilder) pop()).object(trimMatch())))));
    }

    public Rule Object_() {
        return GraphNode();
    }

    public Rule Verb() {
        return FirstOf(IriRef(), A());
    }

    public Rule TriplesNode() {
        return FirstOf(Collection(), BlankNodePropertyList());
    }

    public Rule BlankNodePropertyList() {
        return Sequence(OPEN_SQUARE_BRACE(), PropertyListNotEmpty(),
                CLOSE_SQUARE_BRACE());
    }

    public Rule Collection() {
        return Sequence(OPEN_BRACE(), OneOrMore(GraphNode()), CLOSE_BRACE());
    }

    public Rule GraphNode() {
        return FirstOf(GraphTerm(), TriplesNode());
    }

    public Rule GraphTerm() {
        return FirstOf(MagicTerm(), IriRef(), RdfLiteral(), NumericLiteral(),
                BooleanLiteral(), BlankNode(), Sequence(OPEN_BRACE(),
                        CLOSE_BRACE()));
    }

    public Rule MagicTerm() {
        return Sequence(OPEN_CURLY_BRACE(), FirstOf(SOURCE(), PUBLISHER(), THIS(), STREAM()), CLOSE_CURLY_BRACE(), WS());
    }


    public Rule RdfLiteral() {
        return Sequence(String(), Optional(FirstOf(LANGTAG(), Sequence(
                REFERENCE(), IriRef()))));
    }

    public Rule NumericLiteral() {
        return FirstOf(NumericLiteralUnsigned(), NumericLiteralPositive(),
                NumericLiteralNegative());
    }

    public Rule NumericLiteralUnsigned() {
        return FirstOf(DOUBLE(), DECIMAL(), INTEGER());
    }

    public Rule NumericLiteralPositive() {
        return FirstOf(DOUBLE_POSITIVE(), DECIMAL_POSITIVE(),
                INTEGER_POSITIVE());
    }

    public Rule NumericLiteralNegative() {
        return FirstOf(DOUBLE_NEGATIVE(), DECIMAL_NEGATIVE(),
                INTEGER_NEGATIVE());
    }

    public Rule BooleanLiteral() {
        return FirstOf(TRUE(), FALSE());
    }

    public Rule String() {
        return FirstOf(STRING_LITERAL_LONG1(), STRING_LITERAL1(),
                STRING_LITERAL_LONG2(), STRING_LITERAL2());
    }

    public Rule IriRef() {
        return FirstOf(IRI_REF(), PrefixedName());
    }

    public Rule PrefixedName() {
        return FirstOf(PNAME_LN(), PNAME_NS());
    }

    public Rule BlankNode() {
        return FirstOf(BLANK_NODE_LABEL(), Sequence(OPEN_SQUARE_BRACE(), CLOSE_SQUARE_BRACE()));
    }
}