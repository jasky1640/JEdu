package jedu.debugger.gui;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EditAction;

import jedu.debugger.JavaDebuggerPlugin;
import jedu.debugger.plugin.DebuggerManager;

public abstract class AbstractAction extends EditAction {
  public AbstractAction(String name) {
    super(name);
  }

  public String getCode() {
    return "";
  }

  public static final DebuggerManager getManager(View view) {
    return JavaDebuggerPlugin.getPlugin().getDebuggerManager(view);
  }
}
