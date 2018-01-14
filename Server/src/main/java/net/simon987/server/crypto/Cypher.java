package net.simon987.server.crypto;

interface Cypher {

	public char[] encrypt(char[] plainText, char[] key) throws CryptoException;

	public char[] decrypt(char[] cypherText, char[] key) throws CryptoException;

	public String textCharset();
	
	public String keyCharset();

}