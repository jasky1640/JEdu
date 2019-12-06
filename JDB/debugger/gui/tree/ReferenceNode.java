package jedu.debugger.gui.tree;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ArrayType;

public class ReferenceNode extends TreeNode {
  private static final long serialVersionUID = 1L;
  private boolean isArray = false;

  public ReferenceNode(ReferenceType ref) {
    super(ref);
    name = ref.name();
    int index = name.lastIndexOf('.');
    if (index != -1) {
      name = name.substring(index + 1);
    }
    if (ref instanceof ArrayType)
      isArray = true;
  }

  public void populateChildren() {
    ReferenceType rt = (ReferenceType) getUserObject();
    try {
      addChildren(rt.allFields());
      addChildren(rt.allMethods());
    } catch (Exception ex) {
      name += "<Class not yet Loaded !!>";
      return;
    }
  }

  public boolean isLeaf() {
    return isArray;
  }

}
