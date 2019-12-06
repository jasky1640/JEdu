package jedu.debugger.plugin;

public final class SourceLocation {
  private final String file;
  private final int line;

  public SourceLocation(String filename, int lineNo) {
    file = filename;
    line = lineNo;
  }

  public final String getFilename() {
    return file;
  }

  public final int getLineNumber() {
    return line;
  }
}