package jedu.debugger.core;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.EventRequest;

import jedu.debugger.event.EventAdapter;
import jedu.debugger.event.EventRequestListener;

import jedu.debugger.spec.EventSpec;

import java.util.ArrayList;
import java.util.Iterator;

// event handling ok

final class EventRequestHandler extends EventAdapter {
  public static void main(String[] args) {
    EventRequestHandler erh = new EventRequestHandler();
    System.out.println("JERE");
  }

  final void reset() {
    virtualMachine = null;
    eventRequests.clear();
  }

  final void addEventRequest(EventSpec event) {
    eventRequests.add(event);
    // If program is already running resolve it.
    if (virtualMachine != null) {
      event.set(virtualMachine);
    }

    if (!event.isTransient()) {
      fireAddEvent(event);
    }
  }

  final void removeEventRequest(EventSpec event) {
    EventSpec match = eventRequests.get(event);
    if (match != null) {
      match.setEnabled(false);
      eventRequests.remove(match);
      fireDeleteEvent(event);
    }
  }

  public void enableEventRequest(EventSpec event) {
    EventSpec match = eventRequests.get(event);
    if (match != null) {
      match.setEnabled(true);
      fireEnableEvent(match);
    }
  }

  public void disableEventRequest(EventSpec event) {
    EventSpec match = eventRequests.get(event);
    if (match != null) {
      match.setEnabled(false);
      fireDisableEvent(match);
    }
  }

  public void vmDeathEvent(VMDeathEvent evt) {
    eventRequests.clear();
    virtualMachine = null;
  }

  public void vmDisconnectEvent(VMDisconnectEvent evt) {
    eventRequests.clear();
    virtualMachine = null;
  }

  public void vmStartEvent(VMStartEvent evt) {
    virtualMachine = evt.virtualMachine();

    for (int i = 0; i < eventRequests.size(); i++) {
      EventSpec event = (EventSpec) eventRequests.get(i);
      event.set(virtualMachine);
    }
  }

  public void classPrepareEvent(ClassPrepareEvent evt) {
    ReferenceType rt = evt.referenceType();
    Iterator itr = eventRequests.iterator();
    while (itr.hasNext()) {
      EventSpec event = (EventSpec) itr.next();
      if (!event.isResolved()) {
        try {
          event.resolve(rt);
        } catch (Exception ex) {
          ex.printStackTrace();
          event.setEnabled(false);
        }
      }

    }
  }

  private EventSpec findMatchingRequest(EventRequest request) {
    Iterator itr = eventRequests.iterator();
    while (itr.hasNext()) {
      EventSpec spec = (EventSpec) itr.next();
      EventRequest origin = spec.getRequest();
      if (origin != null && origin == request) {
        return spec;
      }
    }
    return null;
  }

  public void locatableEvent(LocatableEvent evt) {
    EventSpec spec = (EventSpec) evt.request().getProperty(EventSpec.EVENT_SPEC);
    if (spec != null) {
      fireHitEvent(spec);
      if (spec.isTransient()) {
        eventRequests.remove(spec);
      }
    }
  }

  static class EventList extends ArrayList {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public EventSpec eventAt(int index) {
      return (EventSpec) get(index);
    }

    public void add(EventSpec spec) {
      super.add(spec.clone());
    }

    public EventSpec get(EventSpec match) {
      Iterator itr = iterator();
      while (itr.hasNext()) {
        EventSpec spec = (EventSpec) itr.next();
        if (spec.equals(match))
          return spec;
      }
      return null;
    }
  }

  public void addEventRequestListener(EventRequestListener listener) {
    listeners.add(listener);
  }

  public void removeEventRequestListener(EventRequestListener listener) {
    listeners.remove(listener);
  }

  private void fireAddEvent(EventSpec spec) {
    Iterator itr = listeners.iterator();
    while (itr.hasNext()) {
      EventRequestListener listener = (EventRequestListener) itr.next();
      listener.eventRequestAdded(spec);
    }
  }

  private final void fireDeleteEvent(EventSpec spec) {
    Iterator itr = listeners.iterator();
    while (itr.hasNext()) {
      EventRequestListener listener = (EventRequestListener) itr.next();
      listener.eventRequestRemoved(spec);
    }
  }

  private final void fireEnableEvent(EventSpec spec) {
    Iterator itr = listeners.iterator();
    while (itr.hasNext()) {
      EventRequestListener listener = (EventRequestListener) itr.next();
      listener.eventRequestEnabled(spec);
    }
  }

  private final void fireDisableEvent(EventSpec spec) {
    Iterator itr = listeners.iterator();
    while (itr.hasNext()) {
      EventRequestListener listener = (EventRequestListener) itr.next();
      listener.eventRequestDisabled(spec);
    }
  }

  private final void fireHitEvent(EventSpec spec) {
    Iterator itr = listeners.iterator();
    while (itr.hasNext()) {
      EventRequestListener listener = (EventRequestListener) itr.next();
      listener.eventRequestHit(spec);
    }
  }

  EventList eventRequests = new EventList();
  VirtualMachine virtualMachine = null;
  private ArrayList listeners = new ArrayList();

}
