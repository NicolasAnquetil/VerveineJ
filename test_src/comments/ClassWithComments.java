package comments;


public class ClassWithComments {

	/**
	 * an Attribute with a JavaDoc
	 */
	private int x;
	
	/*
	 * Another attribute with a block comment
	 */
	private int y;

	// simple constructor
    CommentClass(){
    }

    // constructor with comment
    CommentClass(int i){
    }

    /**
     * constructor with javadoc
     */
    CommentClass(int i, int j){
        /* block comment in the constructor
         */
    	this(j);
    }

    /* method with nothing */
    int method(){
    }

    // method witout modifier and with comment
    int method(int i){
        /* another block comment in method body
         */
    	ClassWithComments obj;
    }

    /**
     * method witout modifier and with javadoc
     */
    int method(int i, int j){
    }

    public int method(){
    }

    // a method with modifier and normal comment
    public int method(double a){
    }

    /**
     * a method with modifier and javadoc comment
     */
    public int method(double a, double b){
    }

    /**
     * a method with modifier and javadoc comment
     */
    public int methodWithoutBody(double a, double b, double c);

}