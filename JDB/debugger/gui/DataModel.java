package jedu.debugger.gui;

import jedu.debugger.gui.tree.TreeNode;
import jedu.debugger.gui.tree.TreeTableNode;
import jedu.debugger.gui.treetable.AbstractTreeTableModel;
import jedu.debugger.gui.treetable.TreeTableModel;

import org.gjt.sp.jedit.jEdit;

public class DataModel extends AbstractTreeTableModel {

  private static final int COLUMN_COUNT = 3;

  static final String[] COLUMN_NAMES = { "data.column1", "data.column2", "data.column3" };

  static final Class[] COLUMN_CLASS = { TreeTableModel.class, String.class, String.class };

  static String[] columnNames = new String[COLUMN_COUNT];

  static {
    for (int i = 0; i < COLUMN_COUNT; i++) {
      columnNames[i] = jEdit.getProperty(COLUMN_NAMES[i], COLUMN_NAMES[i]);
    }
  }

  public DataModel(Object root) {
    super(root);
  }

  public int getColumnCount() {
    return COLUMN_COUNT;
  }

  public Class getColumnClass(int index) {
    return COLUMN_CLASS[index];
  }

  public String getColumnName(int index) {
    return columnNames[index];
  }

  public Object getValueAt(Object parent, int index) {
    if (parent instanceof TreeTableNode) {
      TreeTableNode node = (TreeTableNode) parent;
      switch (index) {
      case 0:
        return node.getName();
      case 1:
        return node.getType();
      case 2:
        return node.getValue();
      }
    }
    return null;
  }

  public Object getChild(Object parent, int index) {
    TreeNode node = (TreeNode) parent;
    return node.getChildAt(index);
  }

  public int getChildCount(Object parent) {
    TreeNode node = (TreeNode) parent;
    return node.getChildCount();
  }

  public boolean isCellEditable(Object value, int col) {
    if (col == 2)
      return true;
    return super.isCellEditable(value, col);
  }

  public void update() {
    fireTreeStructureChanged(this, new Object[] { root }, null, null);
  }

  public void setValueAt(Object value, Object node, int column) {
    if (node instanceof TreeTableNode) {
      TreeTableNode treeNode = (TreeTableNode) node;
      treeNode.setValue(value.toString());
    }
  }

}