package jedu.debugger.gui.tree;

import com.sun.jdi.Location;
import com.sun.jdi.StackFrame;

public class StackFrameNode extends TreeNode {

  private static final long serialVersionUID = 1L;

  public StackFrameNode(StackFrame frame) {
    super(frame);
    if (frame != null)
      updateValues();
  }

  public boolean isLeaf() {
    return true;
  }

  public final StackFrame getStackFrame() {
    if (getUserObject() instanceof StackFrame) {
      return (StackFrame) getUserObject();
    }
    return null;
  }

  public boolean equals(Object other) {
    if (other instanceof StackFrameNode) {
      return getUserObject() == ((StackFrameNode) other).getUserObject();
    }
    return false;
  }

  private final void updateValues() {
    Location stackLoc = getStackFrame().location();
    className = stackLoc.declaringType().name();
    methodName = stackLoc.method().name();
    try {
      location = stackLoc.sourceName() + ':' + stackLoc.lineNumber();
    } catch (Exception e) {
      location = stackLoc.toString();
    }
  }

  public final String getClassName() {
    return className;
  }

  public final String getMethod() {
    return methodName;
  }

  public final String getLocation() {
    return location;
  }

  public String toString() {
    return location;
  }

  private String className = null;
  private String methodName = null;
  private String location = null;
}
