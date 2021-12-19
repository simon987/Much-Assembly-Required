package net.simon987.mar.server.assembly;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TokenTest {
    @Test
    public void numberTokenParsing() throws Exception {
        TokenParser parser = new TokenParser("27 0x27 0o27 0b11011", 0, new HashMap<>());
        assertEquals(TokenParser.TokenType.Constant, parser.GetNextToken(true, TokenParser.ParseContext.Value));
        assertEquals(27, parser.lastInt);

        assertEquals(TokenParser.TokenType.Constant, parser.GetNextToken(true, TokenParser.ParseContext.Value));
        assertEquals(39, parser.lastInt);

        assertEquals(TokenParser.TokenType.Constant, parser.GetNextToken(true, TokenParser.ParseContext.Value));
        assertEquals(23, parser.lastInt);

        assertEquals(TokenParser.TokenType.Constant, parser.GetNextToken(true, TokenParser.ParseContext.Value));
        assertEquals(27, parser.lastInt);
    }
}
