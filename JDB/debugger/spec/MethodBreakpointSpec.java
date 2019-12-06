package jedu.debugger.spec;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.Method;
import com.sun.jdi.Location;
import com.sun.jdi.request.EventRequestManager;

import java.util.List;

public class MethodBreakpointSpec extends BreakpointSpec {

  private String methodName;

  public MethodBreakpointSpec(String klass, String method) {
    super(klass);
    methodName = method;
  }

  public final String getMethodName() {
    return methodName;
  }

  public boolean matches(ReferenceType rt) {
    return rt.name().equals(klass);
  }

  public void createRequest(ReferenceType rt) throws Exception {
    EventRequestManager evmgr = rt.virtualMachine().eventRequestManager();
    List methods = rt.methodsByName(methodName);
    if (methods.size() == 0) {
      // No such method. disable this for the current session.
      throw new Exception("No such method " + methodName + " in class " + klass);
    }
    Method mthd = (Method) methods.get(0);
    Location loc = (Location) mthd.allLineLocations().get(0);
    request = evmgr.createBreakpointRequest(loc);
  }

  public boolean equals(Object obj) {
    if (obj instanceof MethodBreakpointSpec) {
      MethodBreakpointSpec other = (MethodBreakpointSpec) obj;
      return (klass.equals(other.klass) && methodName.equals(other.methodName));
    }
    return false;
  }

  public String toString() {
    return "Breakpoint in method " + methodName + " of " + klass;
  }
}
