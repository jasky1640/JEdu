package jedu.debugger.event;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;
import com.sun.jdi.request.EventRequest;

import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.Vector;

// The events are usable and complete

public final class EventHandler implements Runnable {

  public static void main(String[] args) {
    EventHandler eh = new EventHandler();
    System.out.println("here");
  }

  private Thread thread;
  private volatile boolean connected = false;
  private boolean suspendVM = false;

  private Vector listeners = new Vector();
  private VirtualMachine vmachine;

  private static final String[] methodNames = { "event", "locatableEvent", "vmStartEvent", "vmDeathEvent",
      "vmDisconnectEvent", "threadStartEvent", "threadDeathEvent", "classPrepareEvent", "classUnloadEvent",
      "breakpointEvent", "watchpointEvent", "exceptionEvent", "stepEvent", "methodEntryEvent", "methodExitEvent", };

  static final Class[] eventTypes = { Event.class, LocatableEvent.class, VMStartEvent.class, VMDeathEvent.class,
      VMDisconnectEvent.class, ThreadStartEvent.class, ThreadDeathEvent.class, ClassPrepareEvent.class,
      ClassUnloadEvent.class, BreakpointEvent.class, WatchpointEvent.class, ExceptionEvent.class, StepEvent.class,
      MethodEntryEvent.class, MethodExitEvent.class, };

  static final boolean[] suspendStatus = { false, true, false, false, false, false, true, false, false, true, true,
      true, true, true, true };

  static final Method[] eventMethods;

  static {
    int length = methodNames.length;
    eventMethods = new Method[length];

    Class listenerClass = EventListener.class;
    Method[] methods = listenerClass.getDeclaredMethods();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      String methodName = method.getName();
      for (int j = 0; j < length; j++) {
        if (methodNames[j].equals(methodName)) {
          eventMethods[j] = method;
        }
      }
    }
  }

  public boolean isConnected() {
    return connected;
  }

  public void addEventListener(EventListener lsnr) {
    listeners.add(lsnr);
  }

  public void removeEventListener(EventListener lsnr) {
    listeners.remove(lsnr);
  }

  public void startHandler(VirtualMachine vm, boolean suspend) {
    vmachine = vm;
    suspendVM = suspend;
    connected = true;
    thread = new Thread(this);
    thread.start();
  }

  public synchronized void stopHandler() {
    connected = false;
    thread.interrupt();
    thread = null;
  }

  public void run() {
    EventQueue queue = vmachine.eventQueue();
    while (connected) {
      try {
        EventSet set = queue.remove();
        EventIterator iterator = set.eventIterator();
        boolean resumeVM = false;

        while (iterator.hasNext()) {
          resumeVM |= !processEvent(iterator.nextEvent());
        }

        if (resumeVM) {
          System.out.println("Automatically resuming VM");
          set.resume();
        } else if (set.suspendPolicy() == EventRequest.SUSPEND_ALL) {
          System.out.println("VM needs to be resumed");
          vmSuspended();
        }

      } catch (InterruptedException iex) {
      } catch (VMDisconnectedException vex) {
        handleDisconnection();
      }

    }
  }

  public boolean processEvent(Event evt) {

    // Take special care for Exit events
    if (evt instanceof VMDeathEvent) {

    } else if (evt instanceof VMDisconnectEvent) {
      connected = false;
    }

    return dispatchEvent(evt);
  }

  public void handleDisconnection() {
    EventQueue queue = vmachine.eventQueue();
    while (connected) {
      try {
        EventSet set = queue.remove();
        EventIterator iterator = set.eventIterator();

        while (iterator.hasNext()) {
          Event evt = iterator.nextEvent();
          if (evt instanceof VMDeathEvent || evt instanceof VMDisconnectEvent) {
            connected = false;
            processEvent(evt);
          } else {
            // System.err.println("Invalid event " + evt.toString());
          }
        }
      } catch (InterruptedException ex) {
      }
    }
  }

  /**
   * Dispatches the vent to the appropriate listeners.
   */
  private boolean dispatchEvent(Event event) {
    boolean retValue = false;
    Class type = event.getClass();
    for (int i = 0; i < eventTypes.length; i++) {
      Method method = eventMethods[i];
      if (eventTypes[i].isAssignableFrom(type) && method != null) {
        invoke(method, event);
        retValue = suspendStatus[i];
      }
    }
    return retValue;
  }

  private final void vmInterrupted() {
    Iterator itr = listeners.iterator();
    while (itr.hasNext()) {
      EventListener listener = (EventListener) itr.next();
      listener.vmInterrupted();
    }
  }

  private final void vmSuspended() {
    Iterator itr = listeners.iterator();
    while (itr.hasNext()) {
      EventListener listener = (EventListener) itr.next();
      listener.vmSuspended();
    }
  }

  private void invoke(Method method, Event event) {
    Object[] args = { event };
    Iterator itr = listeners.iterator();
    while (itr.hasNext()) {
      try {
        method.invoke(itr.next(), args);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

}
