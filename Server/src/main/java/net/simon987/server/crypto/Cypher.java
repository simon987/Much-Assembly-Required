package net.simon987.server.crypto;

interface Cypher {

	public char[] encrypt(char[] plaintext, char[] key);

	public char[] decrypt(char[] cyphertext, char[] key);

	public String textCharset();
	
	public String keyCharset();

}