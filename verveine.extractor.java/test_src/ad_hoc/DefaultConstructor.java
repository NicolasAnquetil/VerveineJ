package ad_hoc;

public class DefaultConstructor {

	public static final int FIELD_WITH_CLASS_SCOPE = 0;
	
	protected int fieldWithInstanceScope = FIELD_WITH_CLASS_SCOPE;

	public DefaultConstructor() {
		this("For testing");
	}

	public DefaultConstructor(String why) {
		super(why);
	}

	public void methodWithInstanceScope() {}
		
	public static void methodWithClassScope() {
		DefaultConstructor x = new DefaultConstructor();
		x.methodWithInstanceScope();
	}
}
