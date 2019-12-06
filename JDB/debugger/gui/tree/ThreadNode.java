package jedu.debugger.gui.tree;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.IncompatibleThreadStateException;

public class ThreadNode extends TreeNode {
  private static final long serialVersionUID = 1L;
  ThreadReference trf;

  public ThreadNode(ThreadReference tr) {
    super(tr);
    trf = tr;
    name = "Thread " + trf.name();
  }

  public void populateChildren() {
    try {
      addChildren(trf.frames());
    } catch (IncompatibleThreadStateException itse) {
      name += " <Unknown State> ";
    }
  }

}
