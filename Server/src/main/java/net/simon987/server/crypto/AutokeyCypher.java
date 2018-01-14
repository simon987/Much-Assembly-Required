package net.simon987.server.crypto;

public class AutokeyCypher extends ShiftSubstitutionCypher {

    public AutokeyCypher(String charset){
        super(charset);
    }

    public AutokeyCypher(){
        super();
    }

    @Override
    protected char encryptionShiftAt(int position, char[] plaintext, char[] key, char[] partialCypherText) {
//        if (i < key.length){
//            return key[i];
//        } else {
//            return plaintext[i - key.length];
//        }
        return 0;
    }

    @Override
    protected char decryptionShiftAt(int position, char[] cypherText, char[] key, char[] partialPlainText) {
//        if (i < key.length){
//            return key[i];
//        } else {
//            return partialPlainText[i - key.length];
//        }
        return 0;
    }
	
}