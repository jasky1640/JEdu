package jedu.debugger.plugin;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EBMessage;

import jedu.debugger.core.Debugger;
import jedu.debugger.JavaDebuggerPlugin;

public class DebuggerMessage extends EBMessage {

  public static final Object SESSION_STARTING = "SESSION STARTING";
  public static final Object SESSION_STARTED = "SESSION STARTED";
  public static final Object SESSION_TERMINATED = "SESSION STOPPED";
  public static final Object SESSION_INTERRUPTED = "SESSION INTERRUPTED";
  public static final Object SESSION_RESUMED = "SESSION RESUMED";
  public static final Object SHOW_SOURCE = "SHOW SOURCE";
  public static final Object EVENT_HIT = "EVENT_HIT";
  public static final Object STACK_FRAME_CHANGED = "STACK FRAME CHANGED";

  private final Object what;
  private final Object info;

  public DebuggerMessage(View source, Object message, Object additionalInfo) {
    super(source);
    what = message;
    info = additionalInfo;
  }

  public final View getView() {
    return (View) getSource();
  }

  public final Debugger getSession() {
    return JavaDebuggerPlugin.getPlugin().getDebuggerManager(getView()).getDebugger();
  }

  public final Object getReason() {
    return what;
  }

  public final Object getInfo() {
    return info;
  }

  public final String toString() {
    return what.toString();
  }
}
