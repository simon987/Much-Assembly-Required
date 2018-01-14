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
    @override
    protected char encryptionShiftAt(int position, char[] plaintext, char[] key, char[] partial_cyphertext){
        return key[0];
    }

    /**
     * Uses the first character of the key as the shift, and ignores the rest.
     */
    @override
    protected char decryptionShiftAt(int position, char[] cyphertext, char[] key, char[] partial_plaintext){
        return key[0];
    }

}