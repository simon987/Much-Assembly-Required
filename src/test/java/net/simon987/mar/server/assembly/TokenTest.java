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
                " 27 0x27 0o27 0b11011 alpha beta gamma delta epsilon 0 1a 0xG 0o8 0b2",
                0, labels);

        assertEquals(TokenParser.TokenType.Space, parser.getNextToken(false, TokenParser.ParseContext.Value));
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(false, TokenParser.ParseContext.Value));
        assertEquals((char)27, parser.lastInt);
        assertEquals(TokenParser.TokenType.Space, parser.getNextToken(false, TokenParser.ParseContext.TackOn));
        assertEquals(TokenParser.TokenType.Constant, parser.getNextToken(true, TokenParser.ParseContext.Value));
        assertEquals((char)39, parser.lastInt);
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
        } catch (AssemblyException ignore) {}
        try {
            parser.getNextToken(true, TokenParser.ParseContext.TackOn);
            fail();
        } catch (AssemblyException ignore) {}
        try {
            parser.getNextToken(true, TokenParser.ParseContext.TackOn);
            fail();
        } catch (AssemblyException ignore) {}
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException ignore) {}
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException ignore) {}
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException ignore) {}
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException ignore) {}
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
        } catch (AssemblyException ignore) {}
        assertEquals(TokenParser.TokenType.BinaryOperator,
                parser.getNextToken(true, TokenParser.ParseContext.TackOn));
        assertEquals(TokenParser.BinaryOperatorType.Sub, parser.lastBinary);


        parser = new TokenParser(ops, 0, new HashMap<>());
        try {
            parser.getNextToken(true, TokenParser.ParseContext.Value);
            fail();
        } catch (AssemblyException ignore) {}

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

    @Test
    public void parseTest() throws AssemblyException {
        assertEquals(1, Operand.parseConstExpression(
                "1",
                0, new HashMap<>())
        );
        assertEquals(2, Operand.parseConstExpression(
                "1 + 1",
                0, new HashMap<>())
        );
        assertEquals(82, Operand.parseConstExpression(
                "10 + 9 * 8",
                0, new HashMap<>())
        );
        assertEquals(98, Operand.parseConstExpression(
                "10 * 9 + 8",
                0, new HashMap<>())
        );
        assertEquals(2, Operand.parseConstExpression(
                "(1 + 1)",
                0, new HashMap<>())
        );
        assertEquals(152, Operand.parseConstExpression(
                "((10 + 9)) * 8",
                0, new HashMap<>())
        );
        assertEquals(170, Operand.parseConstExpression(
                "(10 * ((9 + 8)))",
                0, new HashMap<>())
        );
        assertEquals((char)-170, Operand.parseConstExpression(
                "(-10 * ((9 + 8)))",
                0, new HashMap<>())
        );
        assertEquals(2, Operand.parseConstExpression(
                "(-3 + 5)",
                0, new HashMap<>())
        );
        try {
            Operand.parseConstExpression(
                    "(1",
                    0, new HashMap<>()
            );
            fail();
        } catch (AssemblyException ignore) {}
        try {
            Operand.parseConstExpression(
                    "1)",
                    0, new HashMap<>()
            );
            fail();
        } catch (AssemblyException ignore) {}
        try {
            Operand.parseConstExpression(
                    "(1+1",
                    0, new HashMap<>()
            );
            fail();
        } catch (AssemblyException ignore) {}
        try {
            Operand.parseConstExpression(
                    "1+1)",
                    0, new HashMap<>()
            );
            fail();
        } catch (AssemblyException ignore) {}
        try {
            Operand.parseConstExpression(
                    "((1+1)",
                    0, new HashMap<>()
            );
            fail();
        } catch (AssemblyException ignore) {}
        try {
            Operand.parseConstExpression(
                    "(1+1))",
                    0, new HashMap<>()
            );
            fail();
        } catch (AssemblyException ignore) {}
    }
}
