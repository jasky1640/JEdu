package jedu.debugger.gui.renderer;

import jedu.debugger.spec.ExceptionBreakpointSpec;
import jedu.debugger.spec.MethodBreakpointSpec;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.Icon;
import java.awt.Component;

import jedu.debugger.spec.EventSpec;
import jedu.debugger.spec.SourceBreakpointSpec;

import jedu.debugger.gui.GUIUtils;

public class BreakpointRenderer extends CellRenderer implements ListCellRenderer {

  static Icon bpIcon;
  static Icon activeIcon;
  static Icon disabledIcon;
  int activeIndex = -1;

  static {
    bpIcon = GUIUtils.createIcon("break");
    activeIcon = GUIUtils.createIcon("activebreak");
    disabledIcon = GUIUtils.createIcon("disabledbreak");
  }

  public final void setActiveIndex(int index) {
    activeIndex = index;
  }

  public final int getActiveIndex() {
    return activeIndex;
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected,
      boolean hasFocus) {
    EventSpec request = (EventSpec) value;
    Icon icon = bpIcon;
    if (!request.isEnabled()) {
      icon = disabledIcon;
    } else if (activeIndex != -1 && activeIndex == index) {
      icon = activeIcon;
    }
    setIcon(icon);

    isSelected = selected;
    String labelText = null;

    if (request instanceof SourceBreakpointSpec) {
      SourceBreakpointSpec sbp = (SourceBreakpointSpec) request;
      String fileName = sbp.filename();
      int lineNo = sbp.lineNumber();
      labelText = fileName + ':' + lineNo;
    } else if (request instanceof ExceptionBreakpointSpec) {
      ExceptionBreakpointSpec ebp = (ExceptionBreakpointSpec) request;
      labelText = "Exception: " + ebp.getClassName();
    } else if (request instanceof MethodBreakpointSpec) {
      MethodBreakpointSpec mbp = (MethodBreakpointSpec) request;
      labelText = mbp.getClassName() + ':' + mbp.getMethodName();
    }
    setText(labelText);

    return this;
  }

}