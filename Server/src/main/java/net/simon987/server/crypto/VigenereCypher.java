package net.simon987.server.crypto;

public class VigenereCypher extends ShiftSubstitutionCypher {

	public VigenereCypher(String charset){
		super(charset);
	}

	public VigenereCypher(){
		super();
	}

    @override
    protected char encryptionShiftAt(int position, char[] plaintext, char[] key, char[] partial_cyphertext){
        int j = i % key.length;
        return key[j];
    }

    @override
    protected char decryptionShiftAt(int position, char[] cyphertext, char[] key, char[] partial_plaintext){
        int j = i % key.length;
        return key[j];
    }
    
}