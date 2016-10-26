import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TCSS 342
 * Assignment 3 Compressed Literature
 */

/**
 * A priority queue that 
 * 
 * @author Arrunn Chhouy
 * @version 1.0
 */

public class MyPriorityQueue<Type extends Comparable<Type>> {
	private List<Type> storage;
	
	private int length;
	
	public MyPriorityQueue() {
		length = 0;
		storage = new ArrayList<Type>();
	}
	
	public void offer(Type item) {
		length++;
		storage.add(item);
		Collections.sort(storage);
	}
	
	
	public Type poll() {
		Type hold = storage.get(0);
		storage.remove(0);
		return hold;
	}
	
	public int size() {
		return length;
	}
}
