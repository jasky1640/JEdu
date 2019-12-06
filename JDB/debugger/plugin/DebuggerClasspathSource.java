package jedu.debugger.plugin;

import jedu.debugger.JavaDebuggerPlugin;
import jedu.debugger.options.DebuggerOptions;

import javacore.AbstractClasspathSource;

import org.gjt.sp.jedit.jEdit;

public class DebuggerClasspathSource extends AbstractClasspathSource {
  public DebuggerClasspathSource() {
    super(JavaDebuggerPlugin.class);
  }

  public String getClasspath() {
    return jEdit.getProperty(DebuggerOptions.CLASSPATH, "");
  }

  public String getSourcepath() {
    return jEdit.getProperty(DebuggerOptions.SOURCEPATH, "");
  }
}