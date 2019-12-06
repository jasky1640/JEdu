package jedu.debugger.gui;

import jedu.debugger.options.DebuggerOptions;

import jedu.debugger.plugin.Application;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class StartupDialog extends JDialog implements ActionListener
{
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public StartupDialog(View owner)
  {
    super(owner, "Launch parameters ...", true);
    createUI();
    GUIUtilities.centerOnScreen(this);
  }

  private final void createUI()
  {
    GridBagLayout layout = new GridBagLayout();
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    
    JPanel panel = new JPanel(layout);

    constraints.gridwidth = 1;
    JLabel label =new JLabel("Main Class: ");    
    layout.setConstraints(label, constraints);
    panel.add(label);

    constraints.gridx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    className = new JTextField(jEdit.getProperty(DebuggerOptions.CLASS_NAME));
    layout.setConstraints(className, constraints);
    panel.add(className);
  
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    label = new JLabel("Arguments: ");
    layout.setConstraints(label, constraints);
    panel.add(label);

    constraints.gridx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    classArgs = new JTextField(jEdit.getProperty(DebuggerOptions.CLASS_ARGS));
    layout.setConstraints(classArgs, constraints);
    panel.add(classArgs);
    
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    label = new JLabel("Classpath: ");
    layout.setConstraints(label, constraints);
    panel.add(label);

    constraints.gridx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    classPath = new JTextField(Application.getInstance().getClasspath());
    layout.setConstraints(classPath, constraints);
    panel.add(classPath);
    
    JButton ok = new JButton("OK");
    ok.addActionListener(this);
    JPanel bottom = new JPanel();
    bottom.add(ok);

    constraints.gridx = 0;
    constraints.gridy = 3;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    JLabel notice = new JLabel("Note: Classpath change applicable to this run only");
    notice.setEnabled(false);
    layout.setConstraints(notice, constraints);
    panel.add(notice);
    
    constraints.gridx = 0;
    constraints.gridy = 4;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(bottom, constraints);
    panel.add(bottom);

    getContentPane().add(panel);
    pack();
  }
  
  public void actionPerformed(ActionEvent event)
  {
    accepted = true;
    jEdit.setProperty(DebuggerOptions.CLASS_NAME, className.getText());
    jEdit.setProperty(DebuggerOptions.CLASS_ARGS, classArgs.getText());
    jEdit.setTemporaryProperty(DebuggerOptions.CUSTOM_CLASSPATH, classPath.getText());
    dispose();
  }
  
  public final boolean isAccepted()
  {
    return accepted;
  }

  private JTextField className;
  private JTextField classArgs;
  private JTextField classPath;
  private boolean accepted = false;
}