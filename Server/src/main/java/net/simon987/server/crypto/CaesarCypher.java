package net.simon987.server.crypto;

public class CaesarCypher extends ShiftSubstitutionCypher {

	public CaesarCypher(String charset){
		super(charset);
	}

	public CaesarCypher(){
        super();
	}

    /**
     * Uses the first character of the key as the shift, and ignores the rest.
     */
    @Override
    protected char encryptionShiftAt(int position, char[] plaintext, char[] key, char[] partialCypherText) {
        return key[0];
    }

    /**
     * Uses the first character of the key as the shift, and ignores the rest.
     */
    @Override
    protected char decryptionShiftAt(int position, char[] cypherText, char[] key, char[] partialPlainText) {
        return key[0];
    }

}