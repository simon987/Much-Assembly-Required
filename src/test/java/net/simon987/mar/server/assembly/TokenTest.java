package net.simon987.mar.server.assembly;

import net.simon987.mar.server.assembly.exception.AssemblyException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TokenTest {
    @Test
    public void constantTokenParsing() throws Exception {
        Map<String, Character> labels = new HashMap<>();
        labels.put("alpha", (char)29);
        labels.put("beta", (char)28);
        labels.put("gamma", (char)-5);
        labels.put("epsilon", (char)0);
        TokenParser parser = new TokenParser(
                " 27 0x27 #27 0o27 027 0b11011 alpha beta gamma delta epsilon 0 1a 0xG 0o8 0b2",
                0, labels);

        assertEquals(TokenParser.TokenType.Space, parser.getNextToken(false, TokenParser.ParseContext.Value));
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(false, TokenParser.ParseContext.Value));
        assertEquals((char)27, parser.lastInt);
        assertEquals(TokenParser.TokenType.Space, parser.getNextToken(false, TokenParser.ParseContext.TackOn));
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)39, parser.lastInt);
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)39, parser.lastInt);
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)23, parser.lastInt);
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)23, parser.lastInt);
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)27, parser.lastInt);

        // Labels
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)29, parser.lastInt);
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)28, parser.lastInt);
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)-5, parser.lastInt);
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException expected) {
        }
        try {
            parser.getNextToken(true, TokenParser.ParseContext.TackOn);
            fail();
        } catch (AssemblyException expected) {
        }
        try {
            parser.getNextToken(true, TokenParser.ParseContext.TackOn);
            fail();
        } catch (AssemblyException expected) {
        }
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException expected) {
        }
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException expected) {
        }
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException expected) {
        }
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException expected) {
        }
    }
    @Test
    public void operatorTokenParsing() throws Exception {
        String ops = "+~-";
        TokenParser parser = new TokenParser(ops, 0, new HashMap<>());
        assertEquals(TokenParser.TokenType.BinaryOperator,
                parser.getNextToken(true, TokenParser.ParseContext.TackOn));
        assertEquals(TokenParser.BinaryOperatorType.Add, parser.lastBinary);
        try {
            parser.getNextToken(true, TokenParser.ParseContext.TackOn);
            fail();
        } catch (AssemblyException expected) {}
        assertEquals(TokenParser.TokenType.BinaryOperator,
                parser.getNextToken(true, TokenParser.ParseContext.TackOn));
        assertEquals(TokenParser.BinaryOperatorType.Sub, parser.lastBinary);


        parser = new TokenParser(ops, 0, new HashMap<>());
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException expected) {}
        assertEquals(TokenParser.TokenType.UnaryOperator,
                parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals(TokenParser.UnaryOperatorType.Not, parser.lastUnary);
        assertEquals(TokenParser.TokenType.UnaryOperator,
                parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals(TokenParser.UnaryOperatorType.Neg, parser.lastUnary);
        parser = new TokenParser("()", 0, new HashMap<>());
        assertEquals(TokenParser.TokenType.GroupOperator,
                parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals(TokenParser.GroupOperatorType.GroupStart, parser.lastGroup);
        assertEquals(TokenParser.TokenType.GroupOperator,
                parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals(TokenParser.GroupOperatorType.GroupEnd, parser.lastGroup);
    }

    private void assertParse(int expect, String in) throws AssemblyException {
        assertEquals(expect, new TokenParser(in,0, new HashMap<>()).parseConstExpression());
    }

    private void failParse(String in) {
        try {
            new TokenParser(in,0, new HashMap<>()).parseConstExpression();
            fail();
        } catch (AssemblyException ignore) {}
    }

    @Test
    public void parseTest() throws AssemblyException {
        assertParse(10, "10");
        assertParse(16, "0x10");
        assertParse(8, "0o10");
        assertParse(2, "0b10");
        assertParse(2, "1 + (1)");
        assertParse(98, "(10) * 9 + 8");
        assertParse(82, "10 + (9) * 8");
        assertParse(2, "(1 + 1)");
        assertParse(152, "((10 + 9)) * 8");
        assertParse(170, "(10 * ((9 + 8)))");
        assertParse((char)-170, "(-10 * ((9 + 8)))");
        assertParse((char)-170, "(10 * -((9 + 8)))");
        assertParse(170, "(-10 * -((9 + 8)))");
        assertParse(2, "(-3 + 5)");
        assertParse(3, "10 - 4 - 3");

        failParse("1)");
        failParse("(1");
        failParse("(1 + 1");
        failParse("1 + 1)");
        failParse("((1 + 1)");
        failParse("(1 + 1))");
        failParse("+ 1");
        failParse("(+ 1)");
        failParse("(1 +)");
        failParse("1 + ()");
        failParse("() + 1");
    }
}
