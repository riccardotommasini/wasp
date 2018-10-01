package it.polimi.test;/*
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

import it.polimi.deib.rsp.vocals.rdf4j.VocalsFactoryRDF4J;
import it.polimi.rsp.vocals.core.annotations.VocalsStreamStub;
import it.polimi.sr.wasp.rsp.publisher.syntax.Parser;
import it.polimi.sr.wasp.rsp.publisher.syntax.StreamPublicationBuilder;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.common.FileUtils;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SparqlTest {

    enum Result {
        OK, FAIL
    }

    class TextInfo {
        Result result;
        String text;

        public TextInfo(Result result, String text) {
            super();
            this.result = result;
            this.text = text;
        }
    }

    Parser parser = Parboiled.createParser(Parser.class);

    @Test
    public void test() throws Exception {
        int failures = 0;
        String input = getQuery();
        System.out.println(input);
        ParsingResult<StreamPublicationBuilder> result = new ReportingParseRunner(parser.Query()).run(input);

        if (result.hasErrors()) {
            List<ParseError> parseErrors = result.parseErrors;
            parseErrors.forEach(e -> System.out.println(
                    input.substring(0, e.getStartIndex()) + "|->" +
                            input.substring(e.getStartIndex(), e.getEndIndex()) + "<-|" +
                            input.substring(e.getEndIndex() + 1, input.length() - 1)
            ));
        }

        StreamPublicationBuilder value
                = result.parseTreeRoot.getChildren().get(0).getValue();

    }

    public static void main(String[] args) throws IOException {
        try {
            VocalsStreamStub fetch = new VocalsFactoryRDF4J().fetch("http://localhost:8181/pub/streams/stream1");
            System.out.println(fetch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getQuery() throws IOException {
        URL resource = SparqlTest.class.getResource("/SparqlTest.sparql");
        System.out.println(resource.getPath());
        File file = new File(resource.getPath());
        return FileUtils.readAllText(file);
    }

    private TextInfo parseText(BufferedReader in) throws Exception {
        Pattern unicodes = Pattern.compile("\\\\u([0-9a-fA-F]{4})");

        StringBuffer text = new StringBuffer();
        while (in.ready()) {
            String line = in.readLine();
            if (line.startsWith(">>")) {
                return new TextInfo(line.toLowerCase().contains("ok") ? Result.OK : Result.FAIL, text.toString());
            } else {
                Matcher matcher = unicodes.matcher(line);
                while (matcher.find()) {
                    matcher.appendReplacement(text, Character.toString((char) Integer.parseInt(matcher.group(1), 16)));
                }
                matcher.appendTail(text);

                text.append('\n');
            }
        }
        throw new NoSuchElementException("Expected \">>\"");
    }

}
