package net.simon987.server.crypto;

public class NoCypher implements Cypher {

	private String charset;

	public NoCypher(String charset){
		this.charset = charset;
	}

    public NoCypher() {
        this(RandomStringGenerator.ALPHANUMERIC_CHARSET);
    }

    public char[] encrypt(char[] plainText, char[] key) throws InvalidCharsetException {

        char[] cypherText = new char[plainText.length];
        for (int i = 0; i < plainText.length; i++) {
            char p = plainText[i];
            int p_ind = charset.indexOf(p);
            if (p_ind == -1) {
                throw new InvalidCharsetException("Plaintext contains invalid character: " + p);
            }
            cypherText[i] = p;
        }
        return cypherText;
    }


    public char[] decrypt(char[] cypherText, char[] key) throws InvalidCharsetException {

        char[] plaintext = new char[cypherText.length];

        for (int i = 0; i < cypherText.length; i++) {

            char c = cypherText[i];
            int c_ind = charset.indexOf(c);
            if (c_ind == -1){
                throw new InvalidCharsetException("Cyphertext contains invalid character: " + c);
            }
            plaintext[i] = c;
        }
        return plaintext;
	}

	public String textCharset(){
		return charset;
	}
	
	public String keyCharset(){
		return charset;
	}
}