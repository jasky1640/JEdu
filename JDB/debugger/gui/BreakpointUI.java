package jedu.debugger.gui;

import jedu.debugger.spec.EventSpec;
import jedu.debugger.spec.ExceptionBreakpointSpec;
import jedu.debugger.spec.MethodBreakpointSpec;
import jedu.debugger.spec.SourceBreakpointSpec;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

public class BreakpointUI extends JDialog implements ActionListener {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  static final String[] TYPES = { "Source Breakpoint", "Exception Breakpoint", "Method Breakpoint" };

  static final String OK_COMMAND = "ok";
  static final String CANCEL_COMMAND = "cancel";
  static final String BROWSE_COMMAND = "browse";

  JPanel contentPanel;
  JComboBox combobox;
  JCheckBox caughtExceptions;

  GridBagLayout layout;
  GridBagConstraints constraints;

  JTextField field1;
  JTextField field2;

  public BreakpointUI() {
    super(jEdit.getActiveView(), jEdit.getProperty("newbp.title"), true);

    JPanel panel = new JPanel(new BorderLayout());

    JLabel label = new JLabel(jEdit.getProperty("newbp.type"));
    combobox = new JComboBox(TYPES);
    combobox.addActionListener(this);

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    top.add(label);
    top.add(combobox);
    panel.add(BorderLayout.NORTH, top);

    layout = new GridBagLayout();
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.fill = GridBagConstraints.HORIZONTAL;

    contentPanel = new JPanel(layout);
    createSourceUI();
    panel.add(BorderLayout.CENTER, contentPanel);

    JButton ok = new JButton(jEdit.getProperty("ok.label"));
    ok.setActionCommand(OK_COMMAND);
    ok.addActionListener(this);

    JButton cancel = new JButton(jEdit.getProperty("cancel.label"));
    cancel.setActionCommand(CANCEL_COMMAND);
    cancel.addActionListener(this);

    JPanel bottom = new JPanel();
    bottom.add(ok);
    bottom.add(cancel);
    panel.add(BorderLayout.SOUTH, bottom);

    getContentPane().add(panel);
    pack();
    GUIUtilities.centerOnScreen(this);
  }

  private final void createSourceUI() {
    JLabel label = new JLabel("File: ");
    field1 = new JTextField(20);
    JButton button = new JButton(jEdit.getProperty("browse.label"));
    button.setActionCommand(BROWSE_COMMAND);
    button.addActionListener(this);

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    layout.setConstraints(label, constraints);
    contentPanel.add(label);

    constraints.gridx = 1;
    constraints.weightx = 3;
    constraints.gridwidth = GridBagConstraints.RELATIVE;
    layout.setConstraints(field1, constraints);
    contentPanel.add(field1);

    constraints.gridx = 2;
    constraints.weightx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(button, constraints);
    contentPanel.add(button);

    label = new JLabel("Line: ");
    field2 = new JTextField(4);

    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    layout.setConstraints(label, constraints);
    contentPanel.add(label);

    constraints.gridx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.fill = GridBagConstraints.NONE;
    layout.setConstraints(field2, constraints);
    contentPanel.add(field2);

    label = new JLabel("Example: File.java:1234");
    label.setEnabled(false);
    constraints.gridy = 2;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    layout.setConstraints(label, constraints);
    contentPanel.add(label);

  }

  private final void createMethodUI() {
    contentPanel.removeAll();

    JLabel label = new JLabel("Class: ");
    field1 = new JTextField(20);

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    layout.setConstraints(label, constraints);
    contentPanel.add(label);

    constraints.gridx = 1;
    constraints.weightx = 3;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(field1, constraints);
    contentPanel.add(field1);

    label = new JLabel("Method: ");
    field2 = new JTextField(20);

    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    constraints.weightx = 1;
    layout.setConstraints(label, constraints);
    contentPanel.add(label);

    constraints.gridx = 1;
    constraints.weightx = 3;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(field2, constraints);
    contentPanel.add(field2);

    label = new JLabel("Example: java.io.File.<init>");
    label.setEnabled(false);
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(label, constraints);
    contentPanel.add(label);

  }

  void createExceptionUI() {
    JLabel label = new JLabel("Class: ");
    field1 = new JTextField(20);

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 1;
    layout.setConstraints(label, constraints);
    contentPanel.add(label);

    constraints.gridx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.weightx = 3;
    layout.setConstraints(field1, constraints);
    contentPanel.add(field1);

    caughtExceptions = new JCheckBox("Break on caught exceptions");
    constraints.gridy = 1;
    constraints.gridx = 0;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(caughtExceptions, constraints);
    contentPanel.add(caughtExceptions);

    label = new JLabel("Example: java.lang.NullPointerException");
    label.setEnabled(false);
    constraints.gridy = 2;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(label, constraints);
    contentPanel.add(label);
  }

  /** Handles changes to combo box selection */

  void handleSelection() {
    contentPanel.removeAll();

    int index = combobox.getSelectedIndex();
    switch (index) {
    case 0:
      createSourceUI();
      break;
    case 1:
      createExceptionUI();
      break;
    case 2:
      createMethodUI();
      break;
    }
    contentPanel.revalidate();
    contentPanel.repaint();
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == combobox) {
      handleSelection();
      return;
    }
    String command = evt.getActionCommand();
    if (command.equals(BROWSE_COMMAND)) {
      String[] files = GUIUtilities.showVFSFileDialog(jEdit.getActiveView(), null, 0, false);
      if (files != null) {
        field1.setText(files[0]);
      }
    } else {
      if (command.equals(OK_COMMAND)) {
        createEventSpec();
      }
      dispose();
    }
  }

  void showError() {
    GUIUtilities.error(this, "newbp.invalidbp", null);
  }

  private final void createSourceBreakpoint() {
    String file = field1.getText();
    String line = field2.getText();
    if (file.length() == 0 || line.length() == 0) {
      showError();
      return;
    }
    int lineNo = -1;
    try {
      lineNo = Integer.parseInt(line);
    } catch (NumberFormatException ne) {
      showError();
      return;
    }
    eventSpec = new SourceBreakpointSpec(file, lineNo);
  }

  private final void createExceptionBreakpoint() {
    String className = field1.getText();
    boolean stopOnAll = caughtExceptions.isSelected();
    eventSpec = new ExceptionBreakpointSpec(className, stopOnAll, true);
  }

  private final void createMethodBreakpoint() {
    String className = field1.getText();
    String methodName = field2.getText();
    eventSpec = new MethodBreakpointSpec(className, methodName);
  }

  private void createEventSpec() {
    int index = combobox.getSelectedIndex();
    switch (index) {
    case 0:
      createSourceBreakpoint();
      break;
    case 1:
      createExceptionBreakpoint();
      break;
    case 2:
      createMethodBreakpoint();
      break;
    }

  }

  private EventSpec eventSpec;

  public EventSpec getEventSpec() {
    return eventSpec;
  }

}
