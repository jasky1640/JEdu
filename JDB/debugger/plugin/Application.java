/** 
 * This class Represents an Applcation to be Debugged .
 */

package jedu.debugger.plugin;

import jedu.debugger.options.DebuggerOptions;

import jedu.debugger.spec.EventSpec;
import jedu.debugger.spec.ExceptionBreakpointSpec;
import jedu.debugger.spec.MethodBreakpointSpec;
import jedu.debugger.spec.SourceBreakpointSpec;
import jedu.debugger.spec.WatchpointSpec;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javacore.JavaCorePlugin;

import javax.swing.event.EventListenerList;

import org.gjt.sp.jedit.jEdit;

/**
 * Represents an Application being Debugged. Stores the necessary information
 * about the application. like which Virtual Machine to use, Classpath etc.
 */

public final class Application implements DebuggerOptions {
  private List eventRequests;
  private EventListenerList listenerList;
  private static Application instance = new Application();

  private Application() {
    eventRequests = new ArrayList();
    listenerList = new EventListenerList();
    loadBreakpoints();
  }

  public static final Application getInstance() {
    return instance;
  }

  /**
   * Returns the classpath used by the application
   */
  public final String getClasspath() {
    return JavaCorePlugin.getClasspathSource().getClasspath();
  }

  /**
   * Returns the sourcepath used.
   */
  public final String getSourcepath() {
    return JavaCorePlugin.getClasspathSource().getSourcepath();
  }

  public final void addApplicationListener(ApplicationListener listener) {
    listenerList.add(ApplicationListener.class, listener);
  }

  public final void removeApplicationListener(ApplicationListener listener) {
    listenerList.remove(ApplicationListener.class, listener);
  }

  /** perisits the breakpoint information via jEdit properties */
  public final void saveBreakpoints() {
    int count = 0;
    for (int i = 0; i < eventRequests.size(); i++, count++) {
      EventSpec event = (EventSpec) eventRequests.get(i);
      String type = null;
      String value = null;

      if (event instanceof SourceBreakpointSpec) {
        type = SOURCE;
        SourceBreakpointSpec sbp = (SourceBreakpointSpec) event;
        value = sbp.filename() + ':' + sbp.lineNumber();
      } else if (event instanceof MethodBreakpointSpec) {
        MethodBreakpointSpec mbp = (MethodBreakpointSpec) event;
        type = METHOD;
        value = mbp.getClassName() + ':' + mbp.getMethodName();

      } else if (event instanceof ExceptionBreakpointSpec) {
        ExceptionBreakpointSpec ebp = (ExceptionBreakpointSpec) event;
        type = EXCEPTION;
        value = ebp.getClassName();
      } else if (event instanceof WatchpointSpec) {
        WatchpointSpec watch = (WatchpointSpec) event;
        type = WATCH;
        value = watch.getClassName() + ':' + watch.getFieldName();
      }

      if (type != null && value != null) {
        jEdit.setProperty(BREAKPOINT_TYPE + count, type);
        jEdit.setProperty(BREAKPOINT_VALUE + count, value);
        jEdit.setBooleanProperty(BREAKPOINT_ENABLED + count, event.isEnabled());
      }
    }
    jEdit.setIntegerProperty(BREAKPOINT_COUNT, count);
  }

  public final void loadBreakpoints() {
    int bpcount = jEdit.getIntegerProperty(BREAKPOINT_COUNT, 0);

    for (int i = 0; i < bpcount; i++) {
      String type = jEdit.getProperty(BREAKPOINT_TYPE + i);
      String value = jEdit.getProperty(BREAKPOINT_VALUE + i);
      boolean enabled = jEdit.getBooleanProperty(BREAKPOINT_ENABLED + i, true);

      if (type == null || value == null)
        continue;

      int index = value.lastIndexOf(':');
      EventSpec request = null;
      if (type.equals(SOURCE)) {
        String file = value.substring(0, index);
        String line = value.substring(index + 1);
        int lineno = new Integer(line).intValue();
        request = new SourceBreakpointSpec(file, lineno);
      } else if (type.equals(METHOD)) {
        String klass = value.substring(0, index);
        String method = value.substring(index + 1);
        request = new MethodBreakpointSpec(klass, method);
      } else if (type.equals(EXCEPTION)) {
        String klass = value;
        request = new ExceptionBreakpointSpec(klass, true, true);
      } else if (type.equals(WATCH)) {
        String klass = value.substring(0, index);
        String field = value.substring(index + 1);
        request = new WatchpointSpec(klass, field);
      }
      if (!enabled) {
        request.setEnabled(true);
      }
      eventRequests.add(request);
    }
  }

  public HashMap getLaunchParams() {
    HashMap table = new HashMap();

    String javavm = jEdit.getProperty(VM_NAME);

    String programType = jEdit.getProperty(PROGRAM_TYPE, "application");
    String mainClass = null;
    if (programType.equals("applet")) {
      mainClass = jEdit.getProperty(APPLET_CLASS);
    } else {
      mainClass = jEdit.getProperty(CLASS_NAME);
    }

    // Set class and its arguments
    StringBuffer buffer = new StringBuffer(mainClass);

    String arguments = jEdit.getProperty(CLASS_ARGS, "");
    buffer.append(' ').append(arguments);

    table.put("main", buffer.toString());

    // Set VM Arguments
    buffer.setLength(0);
    // if custom classpath path is set use it else use default.
    String classpath = jEdit.getProperty(CUSTOM_CLASSPATH);
    if (classpath == null || classpath.length() == 0) {
      classpath = JavaCorePlugin.getClasspathSource().getClasspath();
    }
    buffer.append("-classpath \"").append(classpath).append("\" ");

    String vmargs = jEdit.getProperty(VM_ARGS, "");
    buffer.append(vmargs);

    if (buffer.length() > 0) {
      table.put("options", buffer.toString());
    }

    // ?? needed for proper operation of the debugger.
    table.put("suspend", "true");

    return table;
  }

  public List getEventRequests(Class type) {
    List retList = new ArrayList();
    Iterator itr = eventRequests.iterator();
    while (itr.hasNext()) {
      Object value = itr.next();
      if (type.isAssignableFrom(value.getClass())) {
        retList.add(value);
      }
    }
    return retList;
  }

  public synchronized final void addEventRequest(EventSpec event) {
    eventRequests.add(event);
    ApplicationListener[] listeners = (ApplicationListener[]) listenerList.getListeners(ApplicationListener.class);
    for (int i = 0; i < listeners.length; i++)
      listeners[i].eventAdded(event);
  }

  public synchronized final void removeEventRequest(EventSpec event) {
    eventRequests.remove(event);
    ApplicationListener[] listeners = (ApplicationListener[]) listenerList.getListeners(ApplicationListener.class);
    for (int i = 0; i < listeners.length; i++)
      listeners[i].eventRemoved(event);
  }

  public synchronized boolean toggleEventRequest(EventSpec event) {
    if (eventRequests.contains(event)) {
      removeEventRequest(event);
      return false;
    } else {
      addEventRequest(event);
      return true;
    }
  }

  private final void fireEventChanged(EventSpec event) {
    ApplicationListener[] listeners = (ApplicationListener[]) listenerList.getListeners(ApplicationListener.class);
    for (int i = 0; i < listeners.length; i++)
      listeners[i].eventModified(event);
  }

  public final void enableEventRequest(EventSpec event) {
    int index = eventRequests.indexOf(event);
    if (index != -1) {
      EventSpec request = (EventSpec) eventRequests.get(index);
      request.setEnabled(true);
      fireEventChanged(event);
    }
  }

  public final void disableEventRequest(EventSpec event) {
    int index = eventRequests.indexOf(event);
    if (index != -1) {
      EventSpec request = (EventSpec) eventRequests.get(index);
      request.setEnabled(false);
      fireEventChanged(event);
    }
  }

  public List getBreakpointsForFile(String filename) {
    List retList = new ArrayList();
    Iterator itr = eventRequests.iterator();
    while (itr.hasNext()) {
      EventSpec event = (EventSpec) itr.next();
      if (event instanceof SourceBreakpointSpec) {
        SourceBreakpointSpec sbp = (SourceBreakpointSpec) event;
        File firstFile = new File(sbp.filename());
        File secondFile = new File(filename);

        if (firstFile.equals(secondFile)) {
          retList.add(sbp);
        }
      }
    }
    return retList;
  }

  public void close() {
    saveBreakpoints();
    eventRequests.clear();
  }

  static final String BREAKPOINT_TYPE = "jdebugger.breakpoint.type.";
  static final String BREAKPOINT_ENABLED = "jdebugger.breakpoint.enabled.";
  static final String BREAKPOINT_VALUE = "jdebugger.breakpoint.value.";
  static final String BREAKPOINT_COUNT = "jdebugger.breakpoints.count";
  static final String SOURCE = "source";
  static final String METHOD = "method";
  static final String EXCEPTION = "exception";
  static final String WATCH = "watch";

}
