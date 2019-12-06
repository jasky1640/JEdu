package jedu.debugger.gui.tree;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Field;
import com.sun.jdi.Method;
import com.sun.jdi.Value;

public abstract class TreeNodeFactory {
  public static TreeNode createNode(Object obj) {
    if (obj instanceof ThreadGroupReference) {
      return new ThreadGroupNode((ThreadGroupReference) obj);
    } else if (obj instanceof ThreadReference) {
      return new ThreadNode((ThreadReference) obj);
    } else if (obj instanceof StackFrame) {
      return new StackFrameNode((StackFrame) obj);
    } else if (obj instanceof Field) {
      return new FieldNode((Field) obj);
    } else if (obj instanceof Method) {
      return new MethodNode((Method) obj);
    } else if (obj instanceof ReferenceType) {
      return new ReferenceNode((ReferenceType) obj);
    } else if (obj instanceof LocalVariable) {
      return new LocalVariableNode((LocalVariable) obj);
    }
    return new TreeNode(obj);
  }

  public static TreeNode createNode(String name, Object obj) {
    if (obj instanceof Value) {
      return new ValueNode(name, (Value) obj, null);
    }
    return new TreeNode(obj);
  }

}
