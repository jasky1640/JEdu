package jedu.debugger.gui.renderer;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.Icon;
import javax.swing.JTree;
import java.awt.Component;

import jedu.debugger.gui.GUIUtils;
import jedu.debugger.gui.tree.TreeNode;
import jedu.debugger.gui.tree.MethodNode;
import jedu.debugger.gui.tree.FieldNode;
import jedu.debugger.gui.tree.ReferenceNode;

public class ClassPanelRenderer extends CellRenderer implements TreeCellRenderer {

  protected static Icon packageIcon;
  protected static Icon classIcon;
  protected static Icon interfaceIcon;
  protected static Icon methodIcon;
  protected static Icon fieldIcon;
  protected static Icon constructorIcon;

  static {
    // Load the icons required.
    packageIcon = GUIUtils.createIcon("package");
    classIcon = GUIUtils.createIcon("class");
    methodIcon = GUIUtils.createIcon("method");
    constructorIcon = GUIUtils.createIcon("constructor");
    fieldIcon = GUIUtils.createIcon("field");
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {
    TreeNode node = (TreeNode) value;

    Icon icon = null;
    String text = node.toString();

    if (node instanceof ReferenceNode) {
      icon = classIcon;
    } else if (node instanceof MethodNode) {
      icon = methodIcon;
    } else if (node instanceof FieldNode) {
      icon = fieldIcon;
    } else {
      icon = packageIcon;
    }

    setText(text);
    setIcon(icon);
    isSelected = selected;

    return this;
  }

}
