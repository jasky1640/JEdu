package jedu.debugger.spec;

import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.EventRequestManager;

public final class WatchpointSpec extends EventSpec {
  private String fieldName;
  private boolean stopOnAccess = false;

  public WatchpointSpec(String className, String memberName) {
    this(className, memberName, false);
  }

  public WatchpointSpec(String className, String memberName, boolean stopOnAccess) {
    super(className);
    this.stopOnAccess = stopOnAccess;
    fieldName = memberName;
  }

  public boolean matches(ReferenceType rt) {
    return rt.name().equals(klass);
  }

  public void createRequest(ReferenceType rt) throws Exception {
    Field field = rt.fieldByName(fieldName);
    if (field == null) {
      throw new Exception("No such field " + fieldName + " in class " + klass);
    }
    EventRequestManager mgr = rt.virtualMachine().eventRequestManager();
    if (stopOnAccess) {
      request = mgr.createAccessWatchpointRequest(field);
    } else {
      request = mgr.createModificationWatchpointRequest(field);
    }
  }

  public final boolean equals(Object other) {
    if (other instanceof WatchpointSpec) {
      WatchpointSpec watch = (WatchpointSpec) other;
      return fieldName.equals(watch.fieldName) && (klass.equals(watch.klass)) && (stopOnAccess == watch.stopOnAccess);
    }
    return false;
  }

  public final String getFieldName() {
    return fieldName;
  }

  public String toString() {
    return "Watchpoint on " + fieldName + " of " + klass;
  }

}
