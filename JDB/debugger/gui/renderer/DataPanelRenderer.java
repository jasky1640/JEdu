package jedu.debugger.gui.renderer;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.Component;

import jedu.debugger.gui.tree.TreeNode;
import jedu.debugger.gui.GUIUtils;

public class DataPanelRenderer extends CellRenderer implements TreeCellRenderer
{

  protected static Icon objectIcon;
  protected static Icon primitiveIcon;
  
  EmptyBorder defaultBorder = new EmptyBorder(1, 1, 1, 1);
 
  static
  {
    objectIcon = GUIUtils.createIcon("object");
    primitiveIcon = GUIUtils.createIcon("primitive");
  }

  public void setIcon(Icon icon)
  {
    
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      TreeNode node = (TreeNode)value;
      setText(node.toString());

      isSelected = selected;
      if (selected)
      {
        setBackground(selectionColor);
      }

      if (hasFocus)
      {
        setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); 
      }
      else
      {
        setBorder(defaultBorder);
      }
      return this;
    }


}
