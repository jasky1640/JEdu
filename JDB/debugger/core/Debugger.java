package jedu.debugger.core;

import jedu.debugger.event.EventListener;
import jedu.debugger.event.EventRequestListener;
import jedu.debugger.spec.EventSpec;

import java.util.List;
import java.util.Map;

public interface Debugger {
  /**
   * Launch a program for debugging.
   * 
   * @param launchParams contains the paramters for launching.
   * @throws VMLaunchException when the debugger fails to launch the program.
   * @see com.sun.jdi.connect.LaunchingConnector
   */

   //Major component
   
  public void launch(Map launchParams) throws DebuggerException;

  /**
   * Attaches the debugger to a remote process.
   * 
   * @param attachParams
   * @throws debugger.core.DebuggerException
   */
  public void attach(Map attachParams) throws DebuggerException;

  /**
   * Stop debugging and detach from the debugee process. If the process was
   * lauched by the debugger then kill the process.
   */
  public void detach();

  /**
   * Indicates wether the debugger is running or not currnetly
   */
  public boolean isRunning();

  public void suspend();

  public void resume();

  public void addEventListener(EventListener listener);

  public void removeEventListener(EventListener listener);

  public void addEventRequest(EventSpec event);

  public void removeEventRequest(EventSpec event);

  public void addEventRequestListener(EventRequestListener listener);

  public void removeEventRequestListener(EventRequestListener listener);

  public void enableEventRequest(EventSpec event);

  public void disableEventRequest(EventSpec event);

  public void stepIn();

  public void stepOut();

  public void stepOver();

  // IO Handling
  public void addEchoListener(OutputListener listener);

  public void removeEchoListener(OutputListener listener);

  public void addOutputListener(OutputListener listener);

  public void removeOutputListener(OutputListener listener);

  public void addErrorListener(OutputListener listener);

  public void removeErrorListener(OutputListener listener);

  public void outputLine(String line);

  // SourceMapping.
  public void setSourceMapper(SourceMapper mapper);

  public SourceMapper getSourceMapper();

  public List getTopLevelThreadGroups();

  public List getAllClasses();
}
