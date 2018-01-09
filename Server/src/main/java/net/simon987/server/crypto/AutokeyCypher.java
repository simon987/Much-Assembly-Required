package net.simon987.server.crypto;

public class AutokeyCypher extends ShiftSubstitutionCypher {

    public AutokeyCypher(String charset){
        super(charset);
    }

    public AutokeyCypher(){
        super();
    }

    @override
    protected char encryptionShiftAt(int position, char[] plaintext, char[] key, char[] partial_cyphertext){
        if (i<key.length){
            return key[i];
        } else {
            return plaintext[i-key.length];
        }
    }

    @override
    protected char decryptionShiftAt(int position, char[] cyphertext, char[] key, char[] partial_plaintext){
        if (i<key.length){
            return key[i];
        } else {
            return partial_plaintext[i-key.length];
        }
    }
	
}