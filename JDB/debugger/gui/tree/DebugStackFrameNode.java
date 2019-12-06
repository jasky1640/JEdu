package jedu.debugger.gui.tree;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.AbsentInformationException;

import com.sun.jdi.Value;
import java.util.List;

public class DebugStackFrameNode extends StackFrameNode {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DebugStackFrameNode(StackFrame frame) {
    super(frame);
    setFrame(frame);
  }

  public void setFrame(StackFrame frame) {
    // If the node was loaded, remove all the children.
    setLoaded(false);

    if (frame != null) {
      setUserObject(frame);
    } else {
      setUserObject(DUMMY_NODE);
    }
  }

  public void populateChildren() {
    if (getStackFrame() != null) {
      addChildren();
    }

  }

  private void addChildren() {
    int next = 0;
    StackFrame frame = getStackFrame();
    ObjectReference thisObj = frame.thisObject();
    if (thisObj != null) {
      next = 1;
      insert(new ValueNode("this", thisObj, null), 0);
    }

    try {
      List list = frame.visibleVariables();
      addChildren(list, next);
    } catch (AbsentInformationException ex) {
    }
  }

  public void addChild(Object obj, int index) {
    LocalVariable variable = (LocalVariable) obj;
    Value value = getStackFrame().getValue(variable);
    String name = variable.name();
    insert(new ValueNode(name, value, variable), index);
  }

  public boolean isLeaf() {
    return false;
  }

  public boolean equals(Object other) {
    if (other instanceof StackFrameNode) {
      StackFrameNode node = (StackFrameNode) other;
      return getStackFrame() == node.getStackFrame();
    }
    return false;
  }

}
