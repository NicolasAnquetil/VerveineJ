package comments;


public class CommentClass{

    CommentClass(){
        // simple constructor
    }

    // constructor with comment
    CommentClass(int i){
        // simple constructor
    }

    /**
     * constructor with javadoc
     */
    CommentClass(int i, int j){
        // simple constructor
    }

    int method(){
        //method with nothing
    }

    // comment
    int method(int i){
        // method witout modifier and with comment
    }

    /**
     * method witout modifier and with javadoc
     */
    int method(int i, int j){
        //method with nothing
    }

    public int method(){
        //method with one modifier
    }

    // a method with modifier and normal comment
    public int method(double a){
        //method with one modifier
    }

    /**
     * a method with modifier and javadoc comment
     */
    public int method(double a, double b){
        //method with one modifier
    }

    /**
     * a method with modifier and javadoc comment
     */
    public int methodWithoutBody(double a, double b, double c);

}