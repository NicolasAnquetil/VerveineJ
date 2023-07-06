package ad_hoc;

import java.util.ArrayList ;

/*public class ClassA <T, V> {
	 
    public class classB<V> extends ClassA<String, V> {
    	
    }
}

public class ClassC <T extends java.lang.Object> {
 
}*/

/*public class ClassA{
    public <T> T method(T t){
        return t ;
    }

    public Integer method2(Integer n){
        return (this.method(n) + 3);
    }}*/




public class ClassA {
	
	public <T> void m(ArrayList<T> list ) {
	}
	
	public static void main() {
		ClassA t = new ClassA();
		ArrayList<Integer> myList = new ArrayList<Integer>();
		t.m(myList);
	}
}