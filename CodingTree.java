import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * TCSS 342
 * Assignment 3 Compressed Literature
 */

/**
 * A CodingTree class that implements Huffman's algorithm
 * 
 * @author Arrunn Chhouy
 * @version 1.0
 */
public class CodingTree {
	public static final int RANGE = 8;
	
	/** 
	  * A data member that is a map of characters in the messages to binary codes 
	  * created by your tree 
	  */
	private Map<Character, String> codes;
	
	// A map of the character frequency
	private Map<Character, Integer> charFrequency;
	
	// A PriorityQueue  
	private PriorityQueue<TreeNode> queue;
	
	// Contains the main tree
	private TreeNode mainTree;
	
	// A string 
	private String text;
	
	/** A data member that is the message encoded using the Huffman codes */
	private List<Byte> bits;
	
	// A string of the entire encoding
	private StringBuilder coding;
	
	
	/** A constructor that takes the text of a message to be compressed. 
	  * The constructor is responsible for calling all private method that carry out the 
	  * Huffman's coding algorithm. 
	 * @throws IOException */
	public CodingTree(String message) throws IOException {
		codes = new HashMap<Character, String>();
		charFrequency = new HashMap<Character, Integer>();
		queue = new PriorityQueue<TreeNode>();
		bits = new ArrayList<Byte>();
		coding = new StringBuilder();
		
		// The string that is too be encoded
		text = message;
		
		// Counts the frequency of each character
		frequency();
		
		// Puts the TreeNodes in order of their frequency
		prioritize();
		
		// Creates the main tree with all the TreeNodes 
		buildTree();

		// Creates the bit encoding for each character
		bitMap(mainTree, "");
		
		// Encodes the string with the found bit mapping
		encode();
	}

	/**
	 * Counting the frequency of each character in the text file
	 */
	private void frequency() {
		for(int i = 0; i < text.length(); i++) {
			if(!charFrequency.containsKey(text.charAt(i))) {
				charFrequency.put(text.charAt(i), 1);
			} else {
				int count = charFrequency.get(text.charAt(i));
				count++;
				charFrequency.put(text.charAt(i), count);
			}
		}
	}
	
	/**
	 * Creates a TreeNode for each character in the map and prioritizes them based
	 * on their frequency.
	 */
	private void prioritize() {
		Iterator<Entry<Character, Integer>> entries = charFrequency.entrySet().iterator();

		while (entries.hasNext()) {
		    Map.Entry<Character, Integer> entry = entries.next();
		    queue.offer(new TreeNode(entry.getKey(), null, null, entry.getValue()));
		}
	}
	
	
	/**
	 * Creates a tree with all the character using the Huffman's Algorithm
	 */
	private void buildTree() {
		TreeNode firstMin;
		TreeNode secondMin;
		
		while(queue.size() > 1) {
			firstMin = queue.poll();
			secondMin = queue.poll();

			int combineWeight = firstMin.getWeight() + secondMin.getWeight();
			TreeNode root = new TreeNode(null, firstMin, secondMin, combineWeight);
			queue.offer(root);
		}
		mainTree = queue.poll();
	}
	
	/**
	 * Finds the bit coding for each character
	 */
	private void bitMap(TreeNode node, String code) {
		// If it is a leaf then store the code into the map
		if(node.isLeaf()) {
			codes.put(node.getData(), code);
		} else {
			// Traverse through the left side
			bitMap(node.getLeft(), code + 0);
			
			// Traverse through the right side
			bitMap(node.getRight(), code + 1);
		}
	}
	
	/**
	 * Checks the value of the map coding to ensure it is working properly
	 */
	public void checkMapCoding(PrintStream output) {
		Iterator<Entry<Character, String>> entries = codes.entrySet().iterator();

		while (entries.hasNext()) {
		    Map.Entry<Character, String> entry = entries.next();
		    output.println((entry.getKey() + " = " + entry.getValue()));
		}
	}
	
	/**
	 * Encodes the String with the Map coding
	 */
	private void encode() {
		for(int i = 0; i < text.length(); i++) {
			coding.append(codes.get(text.charAt(i)));
		}
	}
	
	/**
	 * 
	 * 
	 * @param out the printstream
	 * @throws IOException
	 */
	public void outPut(PrintStream out) throws IOException {
		String partial;
		int part;
		int rounds = coding.length() / RANGE;
		for(int i = 0; i < rounds * RANGE;  i += RANGE) {
			partial = coding.substring(i, i + RANGE);
			part = Integer.parseInt(partial, 2);
			Byte b = (byte)part;
			out.write(b);
			bits.add(b);
		}
		partial = coding.substring(rounds * RANGE, coding.length());
		part = Integer.parseInt(partial, 2);
		Byte b = (byte)part;
		bits.add(b);
	}
	
	public Map<Character, String> getCodeMap() {
		return codes;
	} 
	
	
	/**
	 * Decodes the encoded String with the Map coding
	 * 
	 * @param bits a String
	 * @param codes a Map
	 * @return a String
	 * @throws FileNotFoundException 
	 */
//	public String decode(String bit, Map<Character, String> codes) throws FileNotFoundException {
//		// Gathers the bit data of the string
//		StringBuilder sb = new StringBuilder();
//		
//		// Holds all the character that each bit represents
//		StringBuilder original = new StringBuilder();
//		
//		File newFile = new File("original.txt");
//		PrintStream output = new PrintStream(newFile);
//		byte[] b = bit.getBytes();
//		for(int i = 0; i < b.length; i++) {
//			String bs = Integer.toBinaryString(b[i]);
//			sb.append(bs);
//		}
//		output.println(text);
//		output.close();
//		return original.toString();
//	}
	
	public String decode(String bits, Map<Character, String> codes) {
		StringBuilder decodedMessage = new StringBuilder();
		Map<String, Character> codesReversed = new HashMap<String, Character>();
		
		//reverse map so we can parse the bits with codes as our keys
		for (Character c : codes.keySet()) {
			String code = codes.get(c);
			codesReversed.put(code, c);
		}
		
		StringBuilder subEncoded = new StringBuilder();
		Character charTemp;
		
		for (int i = 0; i < bits.length(); i++) {
			subEncoded.append(bits.charAt(i));
			charTemp = codesReversed.get(subEncoded.toString());
			if (charTemp != null) {
				decodedMessage.append(charTemp);
				subEncoded.setLength(0);	//clears the bits
			}
		}
		return decodedMessage.toString();
	}
	
	/**
	 * A TreeNode class.
	 * 
	 * @author Arrunn
	 * @version 1.0
	 */
	public class TreeNode implements Comparable<TreeNode> {
		// Holds the data in the left of the tree
		private TreeNode myLeft;
		
		// Holds the data in the right of the tree
		private TreeNode myRight;
		
		// The character in the node
		private Character myData;
		
		// The weight of the letter.
		private int myWeight;
		
		/**
		 * A constructor of the TreeNode that initializes the fields
		 * 
		 * @param data of the Character
		 * @param left is TreeNode
		 * @param right is TreeNode
		 * @param weight is the frequency
		 */
		public TreeNode(Character data, TreeNode left, TreeNode right, int weight) {
			myData = data;
			myLeft = left;
			myRight = right;
			myWeight = weight;
		}
		
		/**
		 * Checks to see if the TreeNode is a leaf
		 * 
		 * @return a boolean
		 */
		public boolean isLeaf() {
			return (myLeft == null && myRight == null);
		}
		
		/**
		 * Returns the character data
		 * 
		 * @return a Character
		 */
		public Character getData() {
			return myData;
		}
		
		/**
		 * Gets the frequency of the character
		 * 
		 * @return an int.
		 */
		public int getWeight() {
			return myWeight;
		}
		
		/**
		 * Gets the left node of this TreeNode
		 * 
		 * @return a TreeNode
		 */
		public TreeNode getLeft() {
			return myLeft;
		}
		
		/**
		 * Gets the right node of this TreeNode
		 * 
		 * @return a TreeNode
		 */
		public TreeNode getRight() {
			return myRight;
		}
		
		/**
		 * Compares with another TreeNode to see which TreeNode 
		 * is larger
		 * 
		 * @return an int
		 */
		@Override
		public int compareTo(TreeNode other) {
			TreeNode node = other;
			int compare = 0;
			if(myWeight > node.getWeight()) {
				compare = 1;
			} else if(myWeight < node.getWeight()) {
				compare = -1;
			}
			return compare;
		}
		
		/**
		 * A string representation of the TreeNode
		 * 
		 * @return a String
		 */
		public String toString() {
			return "Character: " + myData +" Weight: "+ myWeight;
			
		}
	}
}
