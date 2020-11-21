package ad_hoc;

public class Modifiers {
	private final String privateFinalAttribute;

	public final static transient volatile String attribute;

	public final static transient synchronized volatile void methodModifiers(){
		System.out.println("Hello World");
	}

}
