package jedu.debugger.spec;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.EventRequest;
import java.util.List;

public abstract class EventSpec implements Cloneable {

  public static final String EVENT_SPEC = "spec";

  /** The class on which the event is created */
  protected String klass;

  /** Indicates wether this request is yet resolved or not */
  protected boolean resolved = false;

  /** Indicates if the request has been disabled */
  protected boolean enabled = true;

  protected EventRequest request;

  protected boolean isTransient = false;

  protected EventSpec(String className) {
    klass = className;
  }

  public abstract void createRequest(ReferenceType rt) throws Exception;

  public abstract boolean matches(ReferenceType rt);

  public final boolean isResolved() {
    return resolved;
  }

  public final EventRequest getRequest() {
    return request;
  }

  public final String getClassName() {
    return klass;
  }

  public final void setClassName(String name) {
    klass = name;
  }

  /** Resolves the request */
  public void resolve(ReferenceType rt) throws Exception {
    if (enabled && matches(rt)) {
      createRequest(rt);
      if (request != null) {
        if (isTransient()) {
          request.addCountFilter(1);
        }
        request.putProperty(EVENT_SPEC, this);
        request.enable();
        resolved = true;
      }
    }
  }

  private final void createClassRequest(VirtualMachine virtualMachine) {
    EventRequestManager evirtualMachinegr = virtualMachine.eventRequestManager();
    ClassPrepareRequest cpr = evirtualMachinegr.createClassPrepareRequest();
    System.err.println("creating request for " + klass);
    cpr.addClassFilter(klass + "*");
    // cpr.addCountFilter(1);
    cpr.enable();
  }

  public boolean set(VirtualMachine virtualMachine) {
    if (klass == null) {
      // This is for SourceBreakpoints
      return false;
    }
    List list = virtualMachine.classesByName(klass);
    if (list.size() == 0) {
      System.out.println("Deferring EventRequest: Class not loaded: " + klass);
      createClassRequest(virtualMachine);
      return false;
    }
    ReferenceType rt = (ReferenceType) list.get(0);
    try {
      resolve(rt);
    } catch (Exception ex) {
      setEnabled(false);
      return false;
    }
    return true;
  }

  public final boolean isEnabled() {
    if (resolved)
      return request.isEnabled();
    return enabled;
  }

  /**
   * Enables/Disables the Event Request.
   */

  public void setEnabled(boolean enable) {
    if (resolved) {
      request.setEnabled(enable);
    }
    enabled = enable;
  }

  public boolean equals(Object obj) {
    if (obj instanceof EventSpec) {
      EventSpec espec = (EventSpec) obj;
      return espec.klass.equals(klass);
    }
    return false;
  }

  public final boolean isTransient() {
    return isTransient;
  }

  public final void setTransient(boolean value) {
    isTransient = value;
  }

  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException ex) {
      return null;
    }
  }

}
