package jedu.debugger.gui.tree;

public interface TreeTableNode {
  public String getName();

  public String getType();

  public String getValue();

  public void setValue(String value);
}
