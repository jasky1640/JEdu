package jedu.debugger.gui.renderer;

import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.Value;
import javax.swing.table.TableCellRenderer;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;

import java.awt.Component;

import jedu.debugger.gui.GUIUtils;
import jedu.debugger.gui.tree.ValueNode;


public class TreeTableRenderer extends CellRenderer implements TableCellRenderer
{

  protected static Icon threadIcon;
  protected static Icon threadGroupIcon;
  protected static Icon stackIcon;
  protected static Icon objectIcon;
  protected static Icon primitiveIcon;
  
 
  public TreeTableRenderer()
  {
    selectionColor = UIManager.getColor("Tree.selectionBackground");
  }
  
  static
  {
    objectIcon = GUIUtils.createIcon("object");
    primitiveIcon = GUIUtils.createIcon("primitive");
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
    boolean selected, boolean hasFocus, int row, int col)
    {
      if (col == 0)
      {
        if (value instanceof ValueNode)
        {
          ValueNode node = (ValueNode) value;
          setText(node.getName());
          Value nodeValue = ((ValueNode)value).getNodeValue();
          if (value instanceof PrimitiveValue)
          {
            setIcon(primitiveIcon);            
          }
          else
          {
            setIcon(objectIcon);
          }
        }
      }
      else
      {
        setText(value.toString());
      }

      isSelected = selected;
      if (selected)
      {
        setBackground(selectionColor);
      }

      return this;
    }

}
