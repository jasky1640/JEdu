package jedu.debugger.gui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import java.util.List;
import java.util.Iterator;

/**
 * A Simple TreeNode class with support for lazy Loading of children.
 *
 */
public class TreeNode extends DefaultMutableTreeNode {
  private static final long serialVersionUID = 1L;
  public static final Object DUMMY_NODE = "Unknown Object";
  boolean loaded = false;
  protected String name = null;

  public TreeNode(Object obj) {
    if (obj != null) {
      setUserObject(obj);
    } else {
      setUserObject(DUMMY_NODE);
      loaded = true;
    }
  }

  public final void addChildren(List list) {
    addChildren(list, 0);
  }

  public final void addChildren(List list, int startIndex) {
    Iterator itr = list.iterator();
    while (itr.hasNext()) {
      addChild(itr.next(), startIndex++);
    }
    loaded = true;
  }

  public void addChild(Object obj, int index) {
    insert(TreeNodeFactory.createNode(obj), index);
  }

  public int getChildCount() {
    if (!loaded) {
      loaded = true;
      populateChildren();
    }
    int count = super.getChildCount();
    return count;
  }

  /**
   * Add the children to this node. Subclasses need to overrride this method.
   */

  protected void populateChildren() {
  };

  public String toString() {
    if (name == null) {
      name = getUserObject().toString();
    }
    return name;
  }

  public void updateInfo(List list) {
    removeAllChildren();
    addChildren(list);
  }

  /**
   * Tells whether the given node is leaf or not.
   *
   */

  public boolean isLeaf() {
    return false;
  }

  public final void reaload() {
    setLoaded(false);
  }

  protected void setLoaded(boolean value) {
    // If children were already loaded unload them.
    if (loaded) {
      removeAllChildren();
    }
    // set loaded to false so the are reloaded again.
    loaded = value;
  }

  public boolean equals(Object obj) {
    if (obj instanceof TreeNode) {
      TreeNode node = (TreeNode) obj;
      return getUserObject().equals(node.getUserObject());
    }
    return false;
  }

  public TreeNode getChild(Object obj) {
    int count = getChildCount();
    for (int i = 0; i < count; i++) {
      TreeNode node = (TreeNode) getChildAt(i);
      if (node.getUserObject().equals(obj)) {
        return node;
      }
    }
    return null;
  }

  /**
   * Get the Child which contains a given Object; Create one if not found.
   */

  public TreeNode getCreateChild(Object obj) {
    int count = getChildCount();
    TreeNode node;
    for (int i = 0; i < count; i++) {
      node = (TreeNode) getChildAt(i);
      if (node.getUserObject().equals(obj)) {
        return node;
      }
    }
    node = TreeNodeFactory.createNode(obj);
    add(node);
    return node;
  }

  public TreeNode searchNode(Object obj) {
    java.util.Enumeration children = depthFirstEnumeration();
    while (children.hasMoreElements()) {
      TreeNode node = (TreeNode) children.nextElement();
      if (node.getUserObject().equals(obj)) {
        return node;
      }
    }
    return null;
  }

  public final Object getParentObject() {
    TreeNode parent = (TreeNode) getParent();
    return parent == null ? null : parent.getUserObject();
  }

}
