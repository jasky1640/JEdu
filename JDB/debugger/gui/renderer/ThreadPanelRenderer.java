package jedu.debugger.gui.renderer;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.StackFrame;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;

import java.awt.Component;

import jedu.debugger.gui.GUIUtils;

public class ThreadPanelRenderer extends CellRenderer implements TreeCellRenderer
{

  protected static Icon threadIcon;
  protected static Icon threadGroupIcon;
  protected static Icon stackIcon;
  
 
  public ThreadPanelRenderer()
  {
    selectionColor = UIManager.getColor("Tree.selectionBackground");
  }
  
  static
  {
    threadIcon = GUIUtils.createIcon("thread");
    threadGroupIcon = GUIUtils.createIcon("threadgroup");
    stackIcon = GUIUtils.createIcon("stack");
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
      Object object = node.getUserObject();

      setText(node.toString());

      if (object instanceof  ThreadReference)
      {
        setIcon(threadIcon);
      }
      else if (object instanceof ThreadGroupReference)
      {
        setIcon(threadGroupIcon);
      }
      else if (object instanceof StackFrame)
      {
        setIcon(stackIcon);
      }

      isSelected = selected;
      if (selected)
      {
        setBackground(selectionColor);
      }

      return this;
    }


}
