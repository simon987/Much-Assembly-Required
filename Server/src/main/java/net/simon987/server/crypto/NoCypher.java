package net.simon987.server.crypto;

public abstract class NoCypher implements Cypher {

	private String charset;

	public NoCypher(String charset){
		this.charset = charset;
	}

	public NoCypher(){
		this(RandomStringGenerator.alphanum);
	}

	public char[] encrypt(char[] plaintext, char[] key){
        char[] cyphertext = new char[plaintext.length];
        for (int i = 0; i< plaintext.length; i++){
        	char p = plaintext[i];
            int p_ind = charset.indexOf(p);
            if (p_ind == -1){
                throw InvalidCharsetException("Plaintext contains invalid character: "+p);
            }
            cyphertext[i] = p;
        }
        return cyphertext;
	}



	public char[] decrypt(char[] cyphertext, char[] key){
        char[] plaintext = new char[cyphertext.length];
        for (int i = 0; i< cyphertext.length; i++){
        	char c = cyphertext[i];
            int c_ind = charset.indexOf(c);
            if (c_ind == -1){
                throw InvalidCharsetException("Cyphertext contains invalid character: "+c);
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