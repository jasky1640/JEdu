package jedu.debugger.core;

// Exceptions ok

public class DebuggerException extends Exception {
  public static void main(String[] args) {
    DebuggerException de = new DebuggerException("test");
    System.out.println("here");
  }

  private static final long serialVersionUID = 1L;

  public DebuggerException(String message) {
    super(message);
  }

  public DebuggerException(Throwable throwable) {
    super(throwable);
  }

  public DebuggerException(String message, Throwable throwable) {
    super(message, throwable);
  }
}