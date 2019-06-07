
package exceptions;

public interface GenerticThrower<T extends Throwable> {
    public void doThrow() throws T;
}
