package net.simon987.server.crypto;

public class CryptoProvider{

	public static final int NO_CYPHER = 0x0000;
	public static final int CAESAR_CYPHER = 0x0001;
	public static final int VIGENERE_CYPHER = 0x0002;
	public static final int AUTOKEY_CYPHER = 0x0003;

	public static final int PASSWORD_LENGTH = 8; //Same as CubotComPort.MESSAGE_LENGTH

	private String charset;
	private RandomStringGenerator passwordGenerator;

	public CryptoProvider(String charset){
		this.charset = charset;
		this.passwordGenerator = new RandomStringGenerator(PASSWORD_LENGTH, charset);
	}

	public CryptoProvider(){
		this(RandomStringGenerator.ALPHANUMERIC_CHARSET);
	}

	public Cypher getCypher(int cypherId){
		switch (cypherId){
			case NO_CYPHER:
				return new NoCypher(charset);
			case CAESAR_CYPHER:
				return new CaesarCypher(charset);
			case VIGENERE_CYPHER:
				return new VigenereCypher(charset);
			case AUTOKEY_CYPHER:
				return new AutokeyCypher(charset);
			default:
				return null;
		}
	}

}