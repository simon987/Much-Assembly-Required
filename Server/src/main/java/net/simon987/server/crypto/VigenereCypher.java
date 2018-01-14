package net.simon987.server.crypto;

public class VigenereCypher extends ShiftSubstitutionCypher {

	public VigenereCypher(String charset){
		super(charset);
	}

	public VigenereCypher(){
		super();
	}

    @Override
    protected char encryptionShiftAt(int position, char[] plaintext, char[] key, char[] partialCypherText) {
//        int j = i % key.length;
//        return key[j];

        return 0;
    }

    @Override
    protected char decryptionShiftAt(int position, char[] cypherText, char[] key, char[] partialPlainText) {
//        int j = i % key.length;
//        return key[j];

        return 0;
    }
    
}