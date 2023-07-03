package ad_hoc;


/*public class ClassA <T, V> {
	 
    public class classB<V> extends ClassA<String, V> {
    	
    }
}*/

public class ClassA <T extends java.lang.Object> {
 
	public boolean method(Class<T> fmxClass) {
		return fmxClass.isInstance(fmxClass);
	}
	public static void main() {
		ClassA<String> c = new ClassA<String>();
		c.method(c);
	}
}
