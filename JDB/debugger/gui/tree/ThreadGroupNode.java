package jedu.debugger.gui.tree;

import com.sun.jdi.ThreadGroupReference;

public class ThreadGroupNode extends TreeNode {

  private static final long serialVersionUID = 1L;
  ThreadGroupReference tgr;

  public ThreadGroupNode(ThreadGroupReference ref) {
    super(ref);
    tgr = ref;
    name = "ThreadGroup " + tgr.name();
  }

  public void populateChildren() {
    addChildren(tgr.threadGroups());
    addChildren(tgr.threads());
  }

  public boolean isLeaf() {
    return false;
  }

}
