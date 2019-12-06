package jedu.debugger.gui.tree;

import com.sun.jdi.CharType;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleType;
import com.sun.jdi.FloatType;
import com.sun.jdi.IntegerType;
import com.sun.jdi.LongType;
import com.sun.jdi.ShortType;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public final class ValueHelper {

  public static Value createValue(Type type, String value) {
    Value retValue = null;
    if (type instanceof IntegerType) {
      int intValue = Integer.valueOf(value).intValue();
      retValue = type.virtualMachine().mirrorOf(intValue);
    } else if (type instanceof FloatType) {
      float floatValue = Float.valueOf(value).floatValue();
      retValue = type.virtualMachine().mirrorOf(floatValue);
    } else if (type instanceof DoubleType) {
      float doubleValue = Double.valueOf(value).floatValue();
      retValue = type.virtualMachine().mirrorOf(doubleValue);
    } else if (type instanceof LongType) {
      float longValue = Long.valueOf(value).longValue();
      retValue = type.virtualMachine().mirrorOf(longValue);
    } else if (type instanceof ShortType) {
      short shortValue = Short.valueOf(value).shortValue();
      retValue = type.virtualMachine().mirrorOf(shortValue);
    } else if (type instanceof CharType) {
      char charValue = value.charAt(0);
      retValue = type.virtualMachine().mirrorOf(charValue);
    } else if (type instanceof ClassType) {
      if (type.name().equals("java.lang.String")) {
        retValue = type.virtualMachine().mirrorOf(value);
      }
    }
    return retValue;
  }
}