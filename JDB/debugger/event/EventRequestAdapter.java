package jedu.debugger.event;

import jedu.debugger.spec.EventSpec;

public abstract class EventRequestAdapter implements EventRequestListener {
  public void eventRequestAdded(EventSpec spec) {
  }

  public void eventRequestRemoved(EventSpec spec) {
  }

  public void eventRequestDisabled(EventSpec spec) {
  }

  public void eventRequestEnabled(EventSpec spec) {
  }

  public void eventRequestHit(EventSpec spec) {
  }

}