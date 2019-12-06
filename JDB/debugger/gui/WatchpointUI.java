package jedu.debugger.gui;

import jedu.debugger.spec.WatchpointSpec;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

public class WatchpointUI extends JDialog implements ActionListener {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public WatchpointUI() {
    super(jEdit.getActiveView(), jEdit.getProperty("watchui.title", "New Watch"), true);
    createUI();
  }

  private final void createUI() {
    GridBagLayout layout = new GridBagLayout();
    JPanel panel = new JPanel(layout);

    JLabel klass = new JLabel("Class:");
    JLabel field = new JLabel("Field:");

    className = new JTextField(20);
    fieldName = new JTextField(20);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.weightx = 1;

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    layout.setConstraints(klass, constraints);
    panel.add(klass);

    constraints.gridy = 1;
    layout.setConstraints(field, constraints);
    panel.add(field);

    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.weightx = 3;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(className, constraints);
    panel.add(className);

    constraints.gridy = 1;
    panel.add(fieldName);
    layout.setConstraints(fieldName, constraints);

    checkBox = new JCheckBox("Stop only on the read access of the field");
    constraints.gridy = 2;
    constraints.gridx = 0;
    layout.setConstraints(checkBox, constraints);
    panel.add(checkBox);

    JButton ok = new JButton(jEdit.getProperty("ok.label"));
    ok.addActionListener(this);
    constraints.gridx = 1;
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    constraints.weightx = 1;
    layout.setConstraints(ok, constraints);
    panel.add(ok);

    JButton cancel = new JButton(jEdit.getProperty("cancel.label"));
    cancel.addActionListener(this);
    constraints.gridx = 2;
    layout.setConstraints(cancel, constraints);
    panel.add(cancel);

    constraints.gridx = 3;
    Component dummy = Box.createGlue();
    layout.setConstraints(dummy, constraints);
    panel.add(dummy);

    getContentPane().add(panel);
    pack();
    GUIUtilities.centerOnScreen(this);
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals("OK")) {
      // add watch.
      String klass = className.getText();
      String field = fieldName.getText();
      boolean checkAccess = checkBox.isSelected();
      if (klass.length() > 0 && field.length() > 0) {
        watch = new WatchpointSpec(klass, field, checkAccess);
      }
    }
    dispose();
  }

  WatchpointSpec getWatch() {
    return watch;
  }

  private WatchpointSpec watch;
  private JTextField className;
  private JTextField fieldName;
  private JCheckBox checkBox;
}