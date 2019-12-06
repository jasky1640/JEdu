package jedu.debugger.gui.tree;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;

public class ValueNode extends TreeNode implements TreeTableNode {
  
  private static final long serialVersionUID = 1L;

  public ValueNode(String name, Value value, Object info) {
    super(value);
    this.name = name;
    this.value = value;
    this.info = info;
    setType();

    if (value instanceof PrimitiveValue) {
      isLeaf = true;
    }

    if (info != null && info instanceof Integer) {
      int index = ((Integer) info).intValue();
      this.name += "[" + index + "]";
    }
  }

  private final void setType() {
    if (value != null) {
      type = value.type().name();
      if (value instanceof ArrayReference) {
        ArrayReference array = (ArrayReference) value;
        type = ((ArrayType) array.type()).componentTypeName();
        int length = ((ArrayReference) value).length();
        type += "[" + length + "]";
      }
    } else if (info != null && info instanceof Field) {
      type = ((Field) info).typeName();
    } else if (info != null && info instanceof LocalVariable) {
      type = ((LocalVariable) info).typeName();
    }
  }

  public final String getName() {
    return name;
  }

  public final String getType() {
    return type;
  }

  public final String getValue() {
    return value == null ? "null" : value.toString();
  }

  public boolean isLeaf() {
    return isLeaf;
  }

  public void populateChildren() {
    if (value instanceof ArrayReference) {
      ArrayReference ar = (ArrayReference) value;
      addChildren(ar.getValues());
    } else if (value instanceof ObjectReference) {
      ReferenceType type = ((ObjectReference) value).referenceType();
      addChildren(type.allFields());
    }
  }

  public void addChild(Object obj, int index) {
    String name = null;
    Object info = null;
    Value childValue = null;
    if (obj instanceof Field) {
      Field field = (Field) obj;
      name = field.name();
      ObjectReference object = (ObjectReference) value;
      childValue = object.getValue(field);
      info = field;
    } else if (obj instanceof Value) {
      name = this.name;
      childValue = (Value) obj;
      info = new Integer(index);
    }
    ValueNode child = new ValueNode(name, childValue, info);
    insert(child, index);
  }

  public void setValue(String newValue) {
    Value mirrorValue = ValueHelper.createValue(value.type(), newValue);
    Object parent = getParentObject();
    try {
      if (parent instanceof ArrayReference) {
        ArrayReference array = (ArrayReference) parent;
        int index = ((Integer) info).intValue();
        array.setValue(index, mirrorValue);
      } else if (parent instanceof ObjectReference) {
        ObjectReference object = (ObjectReference) parent;
        object.setValue((Field) info, mirrorValue);
      } else if (parent instanceof StackFrame) {
        StackFrame stack = (StackFrame) parent;
        stack.setValue((LocalVariable) info, mirrorValue);
      }
      value = mirrorValue;
    } catch (Exception ex) {
    }
  }

  public final Value getNodeValue() {
    return value;
  }

  private Value value;
  private boolean isLeaf = false;
  private Object info;
  private String type;
}