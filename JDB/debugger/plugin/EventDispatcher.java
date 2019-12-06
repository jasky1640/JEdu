/** 
 * Converts Debugger messages to Edit Bus Messasges which can be handled by UI
 */

package jedu.debugger.plugin;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;

import jedu.debugger.JavaDebuggerPlugin;

import jedu.debugger.core.Debugger;

import jedu.debugger.event.EventAdapter;

import jedu.debugger.plugin.DebuggerMessage;
import jedu.debugger.plugin.SourceLocation;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

public final class EventDispatcher extends EventAdapter {

  public EventDispatcher(DebuggerManager manager) {
    debugger = manager.getDebugger();
    debugger.addEventListener(this);
    view = manager.getView();
    plugin = JavaDebuggerPlugin.getPlugin();
  }

  public static SourceLocation getDebuggerLocation(Debugger debugger, Location location) {
    JavaDebuggerPlugin plugin = JavaDebuggerPlugin.getPlugin();
    Log.log(Log.DEBUG, plugin, "Looking up path for location:" + location);

    String className = location.declaringType().name();
    String classPath = className.replace('.', '/');

    // For inner classes get the outer class name.
    int index = classPath.indexOf('$');
    if (index != -1) {
      classPath = classPath.substring(0, index);
    }

    int lineNo = location.lineNumber();
    String fileName = null;

    // Get the File Name.
    try {
      fileName = location.sourceName();
      index = classPath.lastIndexOf('/');
      if (index != -1) {
        fileName = classPath.substring(0, index + 1) + fileName;
      }
    } catch (AbsentInformationException ex) {
      Log.log(Log.WARNING, plugin, "No debugging information present... stopped at location:" + location);
      fileName = classPath + ".java";
    }

    // If no Debug Info is available or Method is native exit.
    if (fileName == null || lineNo == -1) {
      lineNo = 1; // line info missing.. go to first line of the file.
      Log.log(Log.WARNING, plugin, "No debugging info... stopped in class " + className);
    }

    Log.log(Log.DEBUG, plugin, "Looking up actual path of file: " + fileName);
    fileName = debugger.getSourceMapper().getSourceFile(fileName);
    if (fileName == null) {
      Log.log(Log.WARNING, plugin, "file not found: " + fileName);
    }

    return new SourceLocation(fileName, lineNo);
  }

  public void locatableEvent(LocatableEvent evt) {
    Object reason = DebuggerMessage.EVENT_HIT;
    message = new DebuggerMessage(view, reason, evt);
    EditBus.send(message);
  }

  public void vmStartEvent(VMStartEvent evt) {
    message = new DebuggerMessage(view, DebuggerMessage.SESSION_STARTED, debugger);
    EditBus.send(message);
  }

  public void vmDeathEvent(VMDeathEvent evt) {
    message = new DebuggerMessage(view, DebuggerMessage.SESSION_TERMINATED, debugger);
    EditBus.send(message);
  }

  public void vmDisconnectEvent(VMDisconnectEvent evt) {
    message = new DebuggerMessage(view, DebuggerMessage.SESSION_TERMINATED, debugger);
    EditBus.send(message);
  }

  public void vmSuspended() {
    message = new DebuggerMessage(view, DebuggerMessage.SESSION_INTERRUPTED, debugger);
    EditBus.send(message);
  }

  private final Debugger debugger;
  private final View view;
  private final JavaDebuggerPlugin plugin;

  private DebuggerMessage message;

}
