package jedu.debugger.gui;

import jedu.debugger.JavaDebuggerPlugin;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.EnhancedButton;
import org.gjt.sp.jedit.jEdit;

public class GUIUtils {
  public static Icon createIcon(String name) {
    String image = jEdit.getProperty(name + ".icon");
    if (image != null) {
      java.net.URL url = JavaDebuggerPlugin.class.getResource("icons/" + image);
      if (url != null) {
        return new ImageIcon(url);
      }
    } else {
      System.err.println("Error Loading " + name);
    }
    return null;
  }

  public static JMenu createSubMenu(String name, ActionSet actions) {
    String mname = jEdit.getProperty(name + ".label", "");
    String items = jEdit.getProperty(name, "");

    JMenu menu = new JMenu(mname);
    StringTokenizer tokenizer = new StringTokenizer(items);
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (token.equals("-")) {
        menu.addSeparator();
      } else if (token.charAt(0) == '+') {
        menu.add(createSubMenu(token.substring(1), actions));
      } else {
        menu.add(createMenuItem(token, actions));
      }
    }

    return menu;
  }

  public static JMenuItem createMenuItem(String name, ActionSet actions) {
    // JMenuItem mi = GUIUtilities.loadMenuItem(name);
    JMenuItem mi = new JMenuItem(jEdit.getProperty(name + ".label", name));
    if (actions.getAction(name) != null) {
      mi.addActionListener(new EditActionWrapper(actions.getAction(name)));
      mi.setEnabled(true);
    }
    return mi;
  }

  public static JToolBar createToolBar(String name) {

    // JToolBar toolbar = GUIUtilities.loadToolBar(name);
    // Work around for null pointer excpeitons.
    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);

    String entries = jEdit.getProperty(name, "");
    StringTokenizer tokenizer = new StringTokenizer(entries);
    for (int i = 0; tokenizer.hasMoreTokens(); i++) {
      String token = tokenizer.nextToken();
      if (token.equals("-")) {
        toolbar.addSeparator();
        continue;
      }

      Icon icon = createIcon(token);
      String tooltip = jEdit.getProperty(token + ".label");

      JButton button = new EnhancedButton(icon, tooltip, token, jEdit.getActionContext());
      toolbar.add(button);
    }
    return toolbar;
  }

  public static JComponent[] createMenuItems(String name) {
    ArrayList popupItems = new ArrayList(3);
    String entries = jEdit.getProperty(name, "");
    StringTokenizer tokenizer = new StringTokenizer(entries);
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (token.equals("-")) {
        popupItems.add(new javax.swing.JPopupMenu.Separator());
        continue;
      }
      JMenuItem mi = GUIUtilities.loadMenuItem(token);
      popupItems.add(mi);

    }
    JComponent[] retVal = new JComponent[popupItems.size()];
    popupItems.toArray(retVal);
    return retVal;
  }

  public static JPopupMenu createPopupMenu(String name, ActionSet actions) {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenu menu = createSubMenu(name, actions);
    Component[] items = menu.getMenuComponents();
    for (int i = 0; i < items.length; i++) {
      popupMenu.add(items[i]);
    }
    return popupMenu;
  }

  /**
   * Expands all nodes of the specified tree.
   */
  public static void expandAll(JTree tree) {
    for (int i = 0; i < tree.getRowCount(); i++) {
      tree.expandRow(i);
    }
  }

  /**
   * Search for a path in the specified tree model, whose nodes have the same name
   * (compared using <code>equals()</code>) as the ones specified in the old path.
   *
   * @return a new path for the specified model, or null if no such path could be
   *         found.
   */
  public static TreePath searchEqualPath(TreeModel model, TreePath oldPath) {
    Object treenode = model.getRoot();
    Object[] oldPathNodes = oldPath.getPath();
    TreePath newPath = new TreePath(treenode);

    for (int i = 0; i < oldPathNodes.length; ++i) {
      Object oldPathNode = oldPathNodes[i];

      if (treenode.equals(oldPathNode)) {
        if (i == oldPathNodes.length - 1)
          return newPath;
        else {
          if (model.isLeaf(treenode))
            return null; // not found
          else {
            int count = model.getChildCount(treenode);
            boolean foundChild = false;

            for (int j = 0; j < count; ++j) {
              Object child = model.getChild(treenode, j);
              if (child.equals(oldPathNodes[i + 1])) {
                newPath = newPath.pathByAddingChild(child);
                treenode = child;
                foundChild = true;
                break;
              }
            }

            if (!foundChild)
              return null; // couldn't find child with same name
          }
        }
      }
    }

    return null;
  }

  public static List getExpandedPaths(JTree tree) {
    TreePath rootPath = new TreePath(tree.getModel().getRoot());
    Enumeration descendants = tree.getExpandedDescendants(rootPath);

    // (Copy these to be safe in case the enumeration chokes when
    // the backing data source changes.)
    if (descendants != null) {
      List expandedPaths = new ArrayList();
      while (descendants.hasMoreElements())
        expandedPaths.add(descendants.nextElement());
      return expandedPaths;
    }
    return null;
  }

  static class EditActionWrapper implements ActionListener {
    EditActionWrapper(EditAction action) {
      this.action = action;
    }

    public void actionPerformed(ActionEvent event) {
      View view = GUIUtilities.getView((Component) event.getSource());
      action.invoke(view);
    }

    private EditAction action;
  }

  static final void makeCellVisible(JTable table, int row, int col) {
    if (table.getParent() instanceof JViewport) {
      JViewport viewport = (JViewport) table.getParent();
      Rectangle rect = table.getCellRect(row, col, true);

      // The location of the viewport relative to the table
      Point pt = viewport.getViewPosition();

      // Translate the cell location so that it is relative
      // to the view, assuming the northwest corner of the
      // view is (0,0)
      rect.setLocation(rect.x - pt.x, rect.y - pt.y);

      // Scroll the area into view
      viewport.scrollRectToVisible(rect);
    }
  }
}
