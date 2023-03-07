package comments;


public class CommentClass{

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
        /* simple constructor
         */
    	this(j);
    }

    /* method with nothing */
    int method(){
    }

    // method witout modifier and with comment
    int method(int i){
        /* comment in method body
         */
    	method(i,0);
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