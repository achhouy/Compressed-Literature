import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * TCSS 342
 * Assignment 3 Compressed Literature
 */

/**
 * The main driver program that takes in a file and compresses it
 * 
 * @author Arrunn Chhouy
 * @version 1.0
 */

public class Main {
	public static void main(String[] args) throws IOException {
		String textFile = "WarAndPeace.txt";
		String compressedFile = "compressed.txt";
		
		File text = new File(textFile);
		
		// Creates a new file for the compressed file
		File result = new File(compressedFile);
		
		// Creates a new file for the coding map
		File codes = new File("codes.txt");
		
		PrintStream output = new PrintStream(codes);
		
		PrintStream outputTwo = new PrintStream(result);
		
		// String version of the file
		String content = readFile(textFile);
		
		long startTime = System.currentTimeMillis();
		// Creates a new tree of the content
		
		
		CodingTree tree = new CodingTree(content);
		
		// Prints the code to the codes.txt
		tree.checkMapCoding(output);
		
		// Outputs the compressed message to a binary file
		tree.outPut(outputTwo);
		
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		long sizeOne = text.length();
		long sizeTwo = result.length();
		
		System.out.println(textFile + " file size: " + sizeOne + " bytes");
		System.out.println(compressedFile + " file size: " + sizeTwo + " bytes");
		System.out.println("Running Time: " + elapsedTime + " milliseconds");
		
		//String compressContent = readFile(compressedFile);
		
		String encodedMessage = null;
		
		try {
			encodedMessage = decompress(compressedFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File original = new File("original.txt");
		PrintStream outPut = new PrintStream(original);
		outPut.println(tree.decode(encodedMessage, tree.getCodeMap()));
		outPut.close();
	}
	
	//Decompress binary file into String of bits, to be decoded
	private static String decompress(String file) throws IOException {
		StringBuilder bytes = new StringBuilder();
		File binaryFile = new File(file);
		FileInputStream inFile = null;
		
		try {
			inFile = new FileInputStream(binaryFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] byteData = new String[inFile.available()];
		
		for (int i = 0; i < byteData.length; i++) {
			int b = inFile.read();
			byteData[i] = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
			//http://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
			bytes.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
		}

		return bytes.toString();
	}
	
	/**
	 * Process the text file into a string so that it can be passed
	 * 
	 * Found the code to change a file into a string:
	 * http://stackoverflow.com/questions/326390/
	 * how-do-i-create-a-java-string-from-the-contents-of-a-file
	 * 
	 * @param file passes in a String of the file name.
	 * @return a String of the text file
	 * @throws IOException if there is anything wrong reading the file
	 */
	private static String readFile(String file) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");

	    try {
	        while((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}
}
