package jedu.debugger.spec;

public abstract class BreakpointSpec extends EventSpec {
  public BreakpointSpec(String className) {
    super(className);
  }

  public String toString() {
    return "Breakpoint in class " + klass;
  }
}
