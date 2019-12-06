package jedu.debugger.core;

public class InvalidCommandException extends java.lang.Exception {
  private static final long serialVersionUID = 1L;
  String token;

  public InvalidCommandException(String token) {
  }

  public InvalidCommandException(String token, String reason) {
    super(reason);
  }

  public String toString() {
    return "Invalid token " + token;
  }

}
