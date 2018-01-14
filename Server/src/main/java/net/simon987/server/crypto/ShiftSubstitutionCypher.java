package net.simon987.server.crypto;

public abstract class ShiftSubstitutionCypher implements Cypher {

	private String charset;

	public ShiftSubstitutionCypher(String charset){
		this.charset = charset;
	}

	public ShiftSubstitutionCypher(){
        this(RandomStringGenerator.ALPHANUMERIC_CHARSET);
    }

    protected abstract char encryptionShiftAt(int position, char[] plaintext, char[] key, char[] partialCypherText);

    protected abstract char decryptionShiftAt(int position, char[] cypherText, char[] key, char[] partialPlainText);

    public char[] encrypt(char[] plainText, char[] key) throws CryptoException {

        if (key.length == 0) {
            throw new InvalidKeyException("Key is empty");
        }

        char[] cypherText = new char[plainText.length];

        for (int i = 0; i < plainText.length; i++) {

            char p = plainText[i];
            char k = encryptionShiftAt(i, plainText, key, cypherText);
            int p_ind = charset.indexOf(p);

            if (p_ind == -1) {
                throw new InvalidCharsetException("Plaintext contains invalid character: " + p);
            }

            int k_ind = charset.indexOf(k);

            if (k_ind == -1) {
                throw new InvalidCharsetException("Key contains invalid character: " + k);
            }

            int c_int = (p_ind + k_ind) % charset.length();
            char c = charset.charAt(c_int);
            cypherText[i] = c;
        }
        return cypherText;
    }

    public char[] decrypt(char[] cypherText, char[] key) throws CryptoException {

        if (key.length == 0) {
            throw new InvalidKeyException("Key is empty");
        }

        char[] plaintext = new char[cypherText.length];

        for (int i = 0; i < cypherText.length; i++) {

            char c = cypherText[i];
            char k = decryptionShiftAt(i, cypherText, key, plaintext);
            int cInd = charset.indexOf(c);

            if (cInd == -1) {
                throw new InvalidCharsetException("CypherText contains invalid character: " + c);
            }

            int kInd = charset.indexOf(k);

            if (kInd == -1) {
                throw new InvalidCharsetException("Password contains invalid character: " + k);
            }

            int pInt = (cInd - kInd) % charset.length();
            char p = charset.charAt(pInt);
            plaintext[i] = p;
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