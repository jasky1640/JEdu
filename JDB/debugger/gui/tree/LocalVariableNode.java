package jedu.debugger.gui.tree;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public class LocalVariableNode extends TreeNode implements TreeTableNode {
  private static final long serialVersionUID = 1L;

  public LocalVariableNode(LocalVariable variable) {
    super(variable);
    name = variable.name();
  }

  public final String getName() {
    return name;
  }

  public final String getType() {
    return getLocalVariable().typeName();
  }

  public final String getValue() {
    return getVariableValue() == null ? "null" : value.toString();
  }

  public final void setValue(String value) {
    LocalVariable variable = getLocalVariable();
    try {
      Type type = variable.type();
      Value mirrorValue = ValueHelper.createValue(type, value);
      StackFrameNode parent = (StackFrameNode) getParent();
      StackFrame stackFrame = parent.getStackFrame();
      stackFrame.setValue(variable, mirrorValue);
    } catch (Exception ex) {
    }
  }

  public final LocalVariable getLocalVariable() {
    return (LocalVariable) getUserObject();
  }

  private final Value getVariableValue() {
    if (value == null) {
      StackFrameNode parent = (StackFrameNode) getParent();
      StackFrame stackFrame = parent.getStackFrame();
      value = stackFrame.getValue(getLocalVariable());
    }
    return value;
  }

  private Value value = null;
}