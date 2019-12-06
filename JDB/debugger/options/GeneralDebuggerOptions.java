package jedu.debugger.options;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * A panel for the general UI options of the debugger.
 */

 // UI needs to be rebuilt
public class GeneralDebuggerOptions extends AbstractOptionPane implements DebuggerOptions {
  public static void main (String[] args) {
    //complier error not shown
    GeneralDebuggerOptions gdo = new GeneralDebuggerOptions();
  }

  public GeneralDebuggerOptions() {
    super("java.debugger.general");
  }

  static final String[] tabs = { "io", "threads", "breakpoints", "watches", "data", "classes", "events" };

  JCheckBox[] tabSelection;
  JCheckBox showStartup;
  JCheckBox toolTips;

  protected void _init() {
    boolean showDialog = jEdit.getBooleanProperty(SHOW_STARTUP, true);
    showStartup = new JCheckBox("Show parameter dialog at startup", showDialog);
    addComponent(showStartup);

    boolean showToolTips = jEdit.getBooleanProperty(SHOW_TOOLTIP, true);
    toolTips = new JCheckBox("Show tooltips while debugging", showToolTips);
    addComponent(toolTips);

    addComponent(new JLabel("Debugger tabs:"));
    tabSelection = new JCheckBox[tabs.length];
    for (int i = 0; i < tabs.length; i++) {
      String label = jEdit.getProperty(tabs[i] + ".name");
      boolean selected = jEdit.getBooleanProperty(SHOWTAB + tabs[i], true);
      tabSelection[i] = new JCheckBox(label, selected);
      addComponent(tabSelection[i]);
    }
  }

  protected void _save() {
    jEdit.setBooleanProperty(SHOW_STARTUP, showStartup.isSelected());
    jEdit.setBooleanProperty(SHOW_TOOLTIP, toolTips.isSelected());
    for (int i = 0; i < tabs.length; i++) {
      String property = SHOWTAB + tabs[i];
      jEdit.setBooleanProperty(property, tabSelection[i].isSelected());
    }
  }

}
