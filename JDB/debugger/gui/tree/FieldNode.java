package jedu.debugger.gui.tree;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.Value;

public class FieldNode extends TreeNode implements TreeTableNode {

  private static final long serialVersionUID = 1L;

  public FieldNode(Field fld) {
    super(fld);
    field = fld;
    name = fld.typeName() + ' ' + fld.name();
  }

  public boolean isLeaf() {
    Value value = getFieldValue();
    return value == null ? true : value.type() instanceof PrimitiveType;
  }

  public String getName() {
    return field.name();
  }

  public String getType() {
    return field.typeName();
  }

  public String getValue() {
    Value value = getFieldValue();
    return value == null ? "null" : value.toString();
  }

  public final Value getFieldValue() {
    if (fieldValue != null) {
      Object parent = getParentObject();
      if (parent != null && parent instanceof ObjectReference) {
        ObjectReference reference = (ObjectReference) parent;
        fieldValue = reference.getValue(field);
      }
    }
    return fieldValue;
  }

  public final void setValue(String value) {
    Value mirrorValue = ValueHelper.createValue(fieldValue.type(), value);
    Object parent = getParentObject();
    if (parent != null && parent instanceof ObjectReference) {
      ObjectReference reference = (ObjectReference) parent;
      try {
        reference.setValue(field, mirrorValue);
      } catch (Exception ex) {
      }
    }
  }

  private Value fieldValue;
  private Field field;
}
