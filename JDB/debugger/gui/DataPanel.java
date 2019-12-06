package jedu.debugger.gui;

import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;

import jedu.debugger.gui.renderer.DataPanelRenderer;
import jedu.debugger.gui.tree.DebugStackFrameNode;
import jedu.debugger.gui.treetable.JTreeTable;

import jedu.debugger.plugin.DebuggerMessage;

import java.awt.Color;
import java.awt.event.MouseEvent;

import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

public class DataPanel extends TabPanel {
  public DataPanel() {
    root = new DebugStackFrameNode(null);
    model = new DataModel(root);
  }

  protected void createUI() {
    dataTree = new JTreeTable(model);

    JTree tree = dataTree.getTree();
    tree.putClientProperty("JTree.lineStyle", "Angled");
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);

    tree.setExpandsSelectedPaths(true);
    tree.setCellRenderer(new DataPanelRenderer());

    dataTree.addMouseListener(mouseHandler);

    JScrollPane scroll = new JScrollPane(dataTree);
    scroll.getViewport().setBackground(Color.white);
    panel.add("Center", scroll);

    popupMenu = GUIUtils.createPopupMenu("data.popup", actions);
  }

  /**
   * Clear the Thread tree after program termination.
   */
  protected void clear() {
    updateData(null);
  }

  void updateData(StackFrame frame) {

    rememberExpandedPaths();

    root.setFrame(frame);

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        model.update();
        recallExpandedPaths();
      }
    });
  }

  /** Remember the expanded tree paths. */
  private void rememberExpandedPaths() {
    expandedPaths = GUIUtils.getExpandedPaths(dataTree.getTree());
  }

  /** Expand all the previously expanded paths. */
  private void recallExpandedPaths() {
    if (expandedPaths == null) {
      dataTree.getTree().expandRow(0);
      return;
    }

    for (int i = 0; i < expandedPaths.size(); ++i) {
      TreePath oldPath = (TreePath) expandedPaths.get(i);
      TreePath newPath = GUIUtils.searchEqualPath(dataTree.getTree().getModel(), oldPath);
      if (newPath != null)
        dataTree.getTree().expandPath(newPath);
    }
    expandedPaths.clear();
  }

  public void locatableEvent(LocatableEvent evt) {
    ThreadReference currentThread = evt.thread();
    try {
      updateData(currentThread.frame(0));
    } catch (Exception ex) {

    }
  }

  public void vmDeathEvent(VMDeathEvent evt) {
    clear();
  }

  public void vmDisconnectEvent(VMDisconnectEvent evt) {
    clear();
  }

  public void handleDebuggerMessage(DebuggerMessage message) {
    if (message.getReason() == DebuggerMessage.STACK_FRAME_CHANGED) {
      StackFrame frame = (StackFrame) message.getInfo();
      updateData(frame);
    }
  }

  protected JPopupMenu getPopupMenu(MouseEvent evt) {
    return popupMenu;
  }

  DebugStackFrameNode root;
  JTreeTable dataTree;
  JPopupMenu popupMenu;
  DataModel model;
  List expandedPaths;

}
