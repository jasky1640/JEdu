package jedu.debugger.gui;

import jedu.debugger.event.EventRequestListener;

import jedu.debugger.gui.renderer.BreakpointRenderer;

import jedu.debugger.plugin.Application;
import jedu.debugger.plugin.ApplicationListener;
import jedu.debugger.plugin.DebuggerMessage;
import jedu.debugger.plugin.SourceLocation;

import jedu.debugger.spec.BreakpointSpec;
import jedu.debugger.spec.EventSpec;
import jedu.debugger.spec.SourceBreakpointSpec;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class BreakpointPanel extends TabPanel implements ApplicationListener {

  private BreakpointModel breakpointModel;
  private JPopupMenu popupMenu;
  private BreakpointRenderer renderer;
  private JList jlist;

  public BreakpointPanel() {
    breakpointModel = new BreakpointModel();
    Application.getInstance().addApplicationListener(this);
  }

  protected void createUI() {
    jlist = new JList(breakpointModel);
    renderer = new BreakpointRenderer();
    jlist.setCellRenderer(renderer);
    jlist.addMouseListener(mouseHandler);
    JScrollPane scroll = new JScrollPane(jlist);
    panel.add(BorderLayout.CENTER, scroll);

    popupMenu = GUIUtils.createPopupMenu("breakpoints.popup", actions);
  }

  protected final void debuggerSet() {
    debugger.addEventRequestListener(breakpointModel);
  }

  protected final void debuggerCleared() {
    debugger.removeEventRequestListener(breakpointModel);
    Application.getInstance().removeApplicationListener(this);
  }

  private final class BreakpointModel extends AbstractListModel implements EventRequestListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    List list;

    BreakpointModel() {
      list = Application.getInstance().getEventRequests(BreakpointSpec.class);
    }

    public final int getSize() {
      return list.size();
    }

    public Object getElementAt(int index) {
      return list.get(index);
    }

    final void update() {
      fireContentsChanged(this, 0, list.size());
    }

    final void update(int index) {
      fireContentsChanged(this, index, index);
      jlist.setSelectedIndex(index);
    }

    private final boolean update(EventSpec spec) {
      boolean retValue = false;
      int index = list.indexOf(spec);
      if (index != -1) {
        list.set(index, spec);
        fireContentsChanged(this, index, index);
        retValue = true;
      }
      return retValue;
    }

    public void eventRequestAdded(EventSpec spec) {
      if (spec instanceof BreakpointSpec) {
        if (!update(spec)) {
          list.add(spec);
          int index = list.size() - 1;
          fireIntervalAdded(this, index, index);
        }
      }
    }

    public void eventRequestRemoved(EventSpec spec) {
      int index = list.indexOf(spec);
      if (index != -1) {
        list.remove(index);
        fireIntervalRemoved(this, index, index);
      }
    }

    public void eventRequestEnabled(EventSpec spec) {
      update(spec);
    }

    public void eventRequestDisabled(EventSpec spec) {
      update(spec);
    }

    public final void eventRequestHit(EventSpec spec) {
      int index = list.indexOf(spec);
      if (index != -1) {
        renderer.setActiveIndex(index);
        update(index);
      }
    }

  }

  public void eventAdded(EventSpec event) {
    breakpointModel.eventRequestAdded(event);
  }

  public void eventRemoved(EventSpec event) {
    breakpointModel.eventRequestRemoved(event);
  }

  public void eventModified(EventSpec event) {
    breakpointModel.update(event);
  }

  public void handleDebuggerMessage(DebuggerMessage message) {
    if (message.getReason() == DebuggerMessage.SESSION_RESUMED) {
      int index = renderer.getActiveIndex();
      renderer.setActiveIndex(-1);
      breakpointModel.update();
    }
  }

  protected JPopupMenu getPopupMenu(MouseEvent evt) {
    EventSpec spec = (EventSpec) jlist.getSelectedValue();
    if (spec != null) {
      JMenuItem mi = (JMenuItem) popupMenu.getComponent(TOGGLE_INDEX);
      if (spec.isEnabled()) {
        mi.setText(DISABLE_TEXT);
      } else {
        mi.setText(ENABLE_TEXT);
      }
      if (spec instanceof SourceBreakpointSpec) {
        popupMenu.getComponent(SOURCE_INDEX).setEnabled(true);
      } else {
        popupMenu.getComponent(SOURCE_INDEX).setEnabled(true);
      }
    }
    return popupMenu;
  }

  public void createActions() {
    actions.addAction(new EventHandler(NEW_BREAKPOINT));
    actions.addAction(new EventHandler(TOGGLE_BREAKPOINT));
    actions.addAction(new EventHandler(REMOVE_BREAKPOINT));
    actions.addAction(new EventHandler(SHOW_SOURCE));
    actions.addAction(new EventHandler(ENABLE_ALL));
    actions.addAction(new EventHandler(DISABLE_ALL));
  }

  public class EventHandler extends AbstractAction {
    public EventHandler(String name) {
      super(name);
    }

    public void invoke(View view) {
      String eventName = getName();
      if (eventName.equals(NEW_BREAKPOINT)) {
        BreakpointUI ui = new BreakpointUI();
        EventSpec spec = ui.getEventSpec();
        if (spec != null) {
          getManager(view).addEventRequest(spec);
        }
      } else if (eventName.equals(TOGGLE_BREAKPOINT)) {
        EventSpec spec = (EventSpec) jlist.getSelectedValue();
        if (spec != null) {
          if (spec.isEnabled()) {
            getManager(view).disableEventRequest(spec);
          } else {
            getManager(view).enableEventRequest(spec);
          }
        }
      } else if (eventName.equals(REMOVE_BREAKPOINT)) {
        EventSpec spec = (EventSpec) jlist.getSelectedValue();
        getManager(view).removeEventRequest(spec);
      } else if (eventName.equals(SHOW_SOURCE)) {
        EventSpec spec = (EventSpec) jlist.getSelectedValue();
        if (spec instanceof SourceBreakpointSpec) {
          SourceBreakpointSpec sbp = (SourceBreakpointSpec) spec;
          SourceLocation srcLocation = new SourceLocation(sbp.filename(), sbp.lineNumber());
          EditBus.send(new DebuggerMessage(view, DebuggerMessage.SHOW_SOURCE, srcLocation));
        }
      }
    }
  }

  static final String NEW_BREAKPOINT = "newbp";
  static final String SHOW_SOURCE = "source";
  static final String TOGGLE_BREAKPOINT = "enablebp";
  static final String ENABLE_ALL = "enableall";
  static final String DISABLE_ALL = "disableall";
  static final String REMOVE_BREAKPOINT = "removebp";

  static final String ENABLE_TEXT = jEdit.getProperty("enablebp.label", "Enable");
  static final String DISABLE_TEXT = jEdit.getProperty("disablebp.label", "Disable");

  static final int TOGGLE_INDEX = 1;
  static final int SOURCE_INDEX = 3;
}
