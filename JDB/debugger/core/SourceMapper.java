package jedu.debugger.core;

/**
 * An interface to look up the source code of classes for debugging.
 */

public interface SourceMapper {
  public String getSourceFile(String suffix);

  /**
   * Given a java file name returns the java class name for that file. eg: give
   * src/java/lang/Class.java returns java.lang.Class
   */
  public String getClassNameForFile(String filename);
}
