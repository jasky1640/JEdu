package jedu.debugger.spec;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.EventRequestManager;

public class ExceptionBreakpointSpec extends BreakpointSpec {

  boolean caught = true;
  boolean uncaught = true;

  public ExceptionBreakpointSpec(String name, boolean caught, boolean uncaught) {
    super(name);
    this.caught = caught;
    this.uncaught = uncaught;
  }

  public boolean matches(ReferenceType rt) {
    return rt.name().equals(klass);
  }

  public void createRequest(ReferenceType rt) throws Exception {
    EventRequestManager evmgr = rt.virtualMachine().eventRequestManager();
    request = evmgr.createExceptionRequest(rt, caught, uncaught);
  }

  public boolean equals(Object obj) {
    if (obj instanceof ExceptionBreakpointSpec) {
      ExceptionBreakpointSpec ebp = (ExceptionBreakpointSpec) obj;
      return (klass.equals(ebp.klass));
    }
    return false;
  }

  public String toString() {
    return "Breakpoint in exception " + klass;
  }
}
