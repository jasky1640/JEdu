package jedu.debugger.plugin;

import jedu.debugger.core.Debugger;
import jedu.debugger.core.DebuggerException;
import jedu.debugger.core.DebuggerImpl;

import jedu.debugger.gui.StartupDialog;
import jedu.debugger.gui.ViewUI;

import jedu.debugger.options.DebuggerOptions;
import jedu.debugger.spec.EventSpec;
import jedu.debugger.spec.SourceBreakpointSpec;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

public final class DebuggerManager {

  private Debugger debugger;
  private View view;
  private ViewUI ui;

  public DebuggerManager(View view) {
    this.view = view;
    ui = new ViewUI(view);
  }

  public final void close() {
    if (debugger != null && debugger.isRunning()) {
      debugger.detach();
    }
    ui.close();
    view = null;
  }

  public final Debugger getDebugger() {
    if (debugger == null) {
      debugger = new DebuggerImpl();
      debugger.setSourceMapper(new PluginSourceMapper());
    }
    return debugger;
  }

  public void start() throws DebuggerException {
    if (getDebugger().isRunning()) {
      showError("Program already being Debugged");
      return;
    }

    if (jEdit.getBooleanProperty(DebuggerOptions.SHOW_STARTUP, true)) {
      StartupDialog dialog = new StartupDialog(view);
      if (!dialog.isAccepted())
        return;
    }

    EventDispatcher dispatcher = new EventDispatcher(this);
    Application application = Application.getInstance();

    sendMessage(DebuggerMessage.SESSION_STARTING);
    Debugger debugger = getDebugger();

    // Add the breakpoints
    List events = application.getEventRequests(EventSpec.class);
    for (int i = 0; i < events.size(); i++) {
      EventSpec spec = (EventSpec) events.get(i);
      debugger.addEventRequest(spec);
    }

    // start the application.
    Map map = application.getLaunchParams();
    debugger.launch(map);

  }

  private final void showError(String message) {
    JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public void stop() {
    if (!getDebugger().isRunning()) {
      showError("Nothing to Stop");
      return;
    }
    getDebugger().detach();
    sendMessage(DebuggerMessage.SESSION_TERMINATED);
  }

  public void suspend() {
    if (!getDebugger().isRunning()) {
      showError("Nothing to Suspend");
      return;
    }
    getDebugger().suspend();
    sendMessage(DebuggerMessage.SESSION_INTERRUPTED);
  }

  public void resume() {
    if (!getDebugger().isRunning()) {
      showError("Nothing to Resume");
      return;
    }
    getDebugger().resume();
    sendMessage(DebuggerMessage.SESSION_RESUMED);
  }

  public void step() {
    if (!getDebugger().isRunning()) {
      showError("Nothing to Suspend");
      return;
    }
    getDebugger().stepOver();
    sendMessage(DebuggerMessage.SESSION_RESUMED);
  }

  public void stepIn() {
    if (!getDebugger().isRunning()) {
      showError("Nothing to Suspend");
      return;
    }
    getDebugger().stepIn();
    sendMessage(DebuggerMessage.SESSION_RESUMED);
  }

  public void stepOut() {
    if (!getDebugger().isRunning()) {
      showError("Nothing to Suspend");
      return;
    }
    getDebugger().stepOut();
    sendMessage(DebuggerMessage.SESSION_RESUMED);
  }

  public final void runToCursor() {
    EventSpec event = createBreakpoint();
    event.setTransient(true);
    getDebugger().addEventRequest(event);
    getDebugger().resume();
    sendMessage(DebuggerMessage.SESSION_RESUMED);
  }

  private void updateUI(SourceBreakpointSpec spec, boolean add) {
    if (view.getBuffer().getPath().equals(spec.filename())) {
      int line = spec.lineNumber();
      if (add) {
        ui.getBreakpointHighlight().add(line);
      } else {
        ui.getBreakpointHighlight().remove(line);
      }
    }
  }

  public void addEventRequest(EventSpec spec) {
    Application.getInstance().addEventRequest(spec);
    getDebugger().addEventRequest(spec);
    if (spec instanceof SourceBreakpointSpec) {
      SourceBreakpointSpec sbp = (SourceBreakpointSpec) spec;
      updateUI(sbp, true);
    }
  }

  public void removeEventRequest(EventSpec spec) {
    Application.getInstance().removeEventRequest(spec);
    getDebugger().removeEventRequest(spec);
    if (spec instanceof SourceBreakpointSpec) {
      SourceBreakpointSpec sbp = (SourceBreakpointSpec) spec;
      updateUI(sbp, false);
    }
  }

  public void enableEventRequest(EventSpec spec) {
    Application.getInstance().enableEventRequest(spec);
    getDebugger().enableEventRequest(spec);
  }

  public void disableEventRequest(EventSpec spec) {
    Application.getInstance().disableEventRequest(spec);
    getDebugger().disableEventRequest(spec);
  }

  private final SourceBreakpointSpec createBreakpoint() {
    String file = view.getBuffer().getPath();
    int line = view.getTextArea().getCaretLine() + 1;
    return new SourceBreakpointSpec(file, line);
  }

  public final void setBreakpoint() {
    toggleBreakpoint();
  }

  public final void clearBreakpoint() {
    toggleBreakpoint();
  }

  public void toggleBreakpoint() {
    SourceBreakpointSpec spec = createBreakpoint();
    boolean added = Application.getInstance().toggleEventRequest(spec);
    if (added) {
      getDebugger().addEventRequest(spec);
      updateUI(spec, true);
    } else {
      getDebugger().removeEventRequest(spec);
      updateUI(spec, false);
    }
  }

  private final void sendMessage(Object messageType) {
    DebuggerMessage mesg = new DebuggerMessage(view, messageType, getDebugger());
    EditBus.send(mesg);
  }

  public final View getView() {
    return view;
  }
}
