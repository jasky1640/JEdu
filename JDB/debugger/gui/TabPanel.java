package jedu.debugger.gui;

import jedu.debugger.core.Debugger;

import jedu.debugger.event.EventAdapter;

import jedu.debugger.plugin.DebuggerMessage;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.ActionSet;

/**
 * A Helper class that encapsulates the common helper routines for the other GUI
 * panels in the debugger dockable.
 */
abstract class TabPanel extends EventAdapter {
  public TabPanel() {
    createActions();
  }

  void setDebugger(Debugger debugger) {
    if (debugger != null && this.debugger == null) {
      this.debugger = debugger;
      debugger.addEventListener(this);
      debuggerSet();
    }
  }

  protected void debuggerSet() {
  }

  protected void debuggerCleared() {
  }

  void clearDebugger() {
    if (debugger != null) {
      debuggerCleared();
      debugger.removeEventListener(this);
      debugger = null;
    }
  }

  /** Helper class the provides support for Popup Menus. */
  private final class MouseHandler extends MouseAdapter {
    private final void showPopup(MouseEvent evt) {
      JPopupMenu menu = getPopupMenu(evt);
      if (menu != null) {
        menu.show(evt.getComponent(), evt.getX(), evt.getY());
      }
    }

    public void mousePressed(MouseEvent evt) {
      if (evt.isPopupTrigger()) {
        showPopup(evt);
      }
    }

    public void mouseReleased(MouseEvent evt) {
      if (evt.isPopupTrigger()) {
        showPopup(evt);
      }
    }
  }

  final JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel(new BorderLayout());
      createUI();
    }
    return panel;
  }

  /**
   * Returns the popup menu for the given mouse event
   * 
   * @param event the event for which the popup menu is to be returned.
   * @return popmenu.
   * @return null if no popup is applicable.
   */
  protected JPopupMenu getPopupMenu(MouseEvent event) {
    return null;
  }

  void handleMessage(DebuggerMessage message) {
    if (message.getReason() == DebuggerMessage.SESSION_STARTING) {
      setDebugger(message.getSession());
    }
    if (message.getSession() == debugger) {
      handleDebuggerMessage(message);
    }
  }

  protected void handleDebuggerMessage(DebuggerMessage message) {
  }

  /** Derived classes can overrride this to create the requried popup actions */
  protected void createActions() {
  }

  protected abstract void createUI();

  // Holds the UI. Sub classes will add elements to this.
  protected JPanel panel;
  protected ActionSet actions = new ActionSet();
  protected MouseHandler mouseHandler = new MouseHandler();
  protected Debugger debugger;
}
