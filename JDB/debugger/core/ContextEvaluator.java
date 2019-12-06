package jedu.debugger.core;

import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.event.LocatableEvent;

import java.util.StringTokenizer;

/**
 * Evlautes a given expression in the context of a stack frame.
 */
public final class ContextEvaluator {

  public ContextEvaluator(StackFrame stack) {
    stackframe = stack;
  }

  public ContextEvaluator(LocatableEvent event) throws IncompatibleThreadStateException {
    this(event.thread().frame(0));
  }

  public String evaluate(String expression) throws Exception {
    StringTokenizer tokenizer = new StringTokenizer(expression, ".");

    Value currentValue = null;

    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (token.equals("this")) {
        currentValue = stackframe.thisObject();
      } else {
        if (currentValue == null) {
          LocalVariable variable = stackframe.visibleVariableByName(token);
          currentValue = stackframe.getValue(variable);
        } else {
          Type type = currentValue.type();
          if (type instanceof ReferenceType) {
            Field field = ((ReferenceType) type).fieldByName(token);
            currentValue = ((ObjectReference) currentValue).getValue(field);
          }
        }
      }
    }

    StringBuffer buffer = new StringBuffer(expression);
    buffer.append('=');
    buffer.append(currentValue);
    return buffer.toString();
  }

  private StackFrame stackframe;
}
