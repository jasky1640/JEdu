package jedu.debugger.gui.renderer;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Graphics;

public class CellRenderer extends JLabel {

  protected boolean isSelected = false;
  protected Color selectionColor = UIManager.getColor("Tree.selectionBackground");

  /**
   * paint is subclassed to draw the background correctly. JLabel currently does
   * not allow backgrounds other than white, and it will also fill behind the
   * icon. Something that isn't desirable.
   */
  public void paint(Graphics g) {
    Color bColor = Color.white;
    if (isSelected) {
      bColor = selectionColor;
    } else if (getParent() != null) {
      bColor = getParent().getBackground();
    }

    g.setColor(bColor);

    Icon currentI = getIcon();
    if (currentI != null && getText() != null) {
      int offset = (currentI.getIconWidth() + getIconTextGap());
      g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
    } else {
      g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
    super.paint(g);
  }

}
