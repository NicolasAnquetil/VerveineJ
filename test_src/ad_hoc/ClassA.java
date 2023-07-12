package ad_hoc;

import java.util.ArrayList ;
public class ClassA {
	
	public <T> void m(ArrayList<T> list ) {
	}
	
	public static void main() {
		ClassA t = new ClassA();
		ArrayList<Integer> myList = new ArrayList<Integer>();
		t.m(myList);
	}
}