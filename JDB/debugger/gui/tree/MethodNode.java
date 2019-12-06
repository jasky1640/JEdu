package jedu.debugger.gui.tree;

import com.sun.jdi.Method;
import java.util.Iterator;
import java.util.List;

public class MethodNode extends TreeNode {

  private static final long serialVersionUID = 1L;

  public MethodNode(Method method) {
    super(method);
    StringBuffer buffer = new StringBuffer();
    buffer.append(method.returnTypeName()).append(' ');
    buffer.append(method.name()).append('(');
    List args = method.argumentTypeNames();
    Iterator itr = args.iterator();
    if (itr.hasNext()) {
      buffer.append(itr.next());
    }
    while (itr.hasNext()) {
      buffer.append(',').append(itr.next());
    }
    buffer.append(')');
    name = buffer.toString();
  }

  public final boolean isLeaf() {
    return true;
  }
}
