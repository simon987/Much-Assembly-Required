package net.simon987.server.crypto;

public abstract class ShiftSubstitutionCypher implements Cypher {

	private String charset;

	public ShiftSubstitutionCypher(String charset){
		this.charset = charset;
	}

	public ShiftSubstitutionCypher(){
		this(RandomStringGenerator.alphanum);
	}

    protected abstract char encryptionShiftAt(int position, char[] plaintext, char[] key, char[] partial_cyphertext);
    protected abstract char decryptionShiftAt(int position, char[] cyphertext, char[] key, char[] partial_plaintext);

	public char[] encrypt(char[] plaintext, char[] key){
        if (key.length==0){
            throw InvalidKeyException("Key is empty");
        }
        int charset_length = charset.length();
        char[] cyphertext = new char[plaintext.length];
        for (int i = 0; i< plaintext.length; i++){
            char p = plaintext[i];
            char k = encryptionShiftAt(i,plaintext,key,cyphertext);
            int p_ind = charset.indexOf(p);
            if (p_ind == -1){
                throw InvalidCharsetException("Plaintext contains invalid character: "+p);
            }
            int k_ind = charset.indexOf(k);
            if (k_ind == -1){
                throw InvalidCharsetException("Key contains invalid character: "+k); 
            }
            int c_int = (p_ind+k_ind)%charset_length;
            char c = charset.charAt(c_int);
            cyphertext[i] = c;
        }
        return cyphertext;
	}

	public char[] decrypt(char[] cyphertext, char[] key){
        if (key.length==0){
            throw InvalidKeyException("Key is empty");
        }
        int charset_length = charset.length();
        char[] plaintext = new char[cyphertext.length];
        for (int i = 0; i< cyphertext.length; i++){
            char c = cyphertext[i];
            char k = decryptionShiftAt(i,cyphertext,key,plaintext);
            int c_ind = charset.indexOf(c);
            if (c_ind == -1){
                throw InvalidCharsetException("Cyphertext contains invalid character: "+c);
            }
            int k_ind = charset.indexOf(k);
            if (k_ind == -1){
                throw InvalidCharsetException("Password contains invalid character: "+k); 
            }
            int p_int = (c_ind-k_ind)%charset_length;
            char p = charset.charAt(p_int);
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