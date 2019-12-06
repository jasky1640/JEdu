package jedu.debugger.options;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

import javacore.gui.PathBuilder;

public class JavaDebuggerOptions extends AbstractOptionPane implements DebuggerOptions {
  JTabbedPane tab;
  PathBuilder classPath;
  PathBuilder sourcePath;

  JTextField vmName;
  JButton vmSetButton;

  JTextField vmArgs;

  JLabel programTypeLabel;
  JRadioButton application;
  JRadioButton applet;

  JTextField className;
  JTextField classArgs;

  public JavaDebuggerOptions() {
    super("java.debugger.runtime");
  }

  private static final Border createBorder(String title) {
    TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        jEdit.getProperty(title));
    return border;
  }

  protected void _init() {
    vmName = new JTextField();

    vmSetButton = new RolloverButton(GUIUtilities.loadIcon("Open.png"));
    vmSetButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        String[] entries = GUIUtilities.showVFSFileDialog(jEdit.getActiveView(), null, VFSBrowser.OPEN_DIALOG, false);
        if (entries != null && entries.length > 0) {
          vmName.setText(entries[0]);
        }
      }
    });

    JPanel vmPathPanel = new JPanel(new BorderLayout(0, 0));
    vmPathPanel.add(vmName, BorderLayout.CENTER);
    vmPathPanel.add(vmSetButton, BorderLayout.EAST);

    vmArgs = new JTextField();

    JPanel vmPanel = new JPanel(new GridLayout(0, 1));
    vmPanel.setBorder(createBorder("options.jdebugger.vmProperties"));

    vmPanel.add(new JLabel(jEdit.getProperty("options.jdebugger.vmLabel")));
    vmPanel.add(vmPathPanel);
    vmPanel.add(new JLabel(jEdit.getProperty("options.jdebugger.vmArgsLabel")));
    vmPanel.add(vmArgs);

    programTypeLabel = new JLabel(jEdit.getProperty("options.jdebugger.programTypeLabel"));

    application = new JRadioButton();
    application.setText("Application");

    applet = new JRadioButton();
    applet.setText("Applet");

    ButtonGroup group = new ButtonGroup();
    group.add(application);
    group.add(applet);

    JPanel radioPanel = new JPanel(new GridLayout(1, 2));
    radioPanel.add(application);
    radioPanel.add(applet);

    className = new JTextField();
    classArgs = new JTextField();

    JPanel programPanel = new JPanel(new GridLayout(0, 1));
    programPanel.setBorder(createBorder("options.jdebugger.programProperties"));
    programPanel.add(programTypeLabel);
    programPanel.add(radioPanel);
    programPanel.add(new JLabel(jEdit.getProperty("options.jdebugger.classLabel")));
    programPanel.add(className);
    programPanel.add(new JLabel(jEdit.getProperty("options.jdebugger.classArgsLabel")));
    programPanel.add(classArgs);

    FileFilter filter = new Filter();

    classPath = new PathBuilder();
    classPath.setFileFilter(filter);
    classPath.setEnabled(true);

    sourcePath = new PathBuilder();
    sourcePath.setFileFilter(filter);
    sourcePath.setEnabled(true);

    tab = new JTabbedPane();
    tab.addTab(jEdit.getProperty("options.jdebugger.cpLabel"), classPath);
    tab.addTab(jEdit.getProperty("options.jdebugger.spLabel"), sourcePath);

    JPanel classpathPanel = new JPanel(new BorderLayout());
    classpathPanel.setBorder(createBorder("options.jdebugger.pathsLabel"));
    classpathPanel.add(tab, BorderLayout.CENTER);

    BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
    setLayout(layout);

    add(vmPanel);
    add(programPanel);
    add(classpathPanel);

    // populate default values of the UI
    setDefaults();

  }

  public void setDefaults() {
    vmName.setText(jEdit.getProperty(VM_NAME));
    vmArgs.setText(jEdit.getProperty(VM_ARGS));

    if (jEdit.getProperty(PROGRAM_TYPE).equals("applet"))
      applet.setSelected(true);
    else
      application.setSelected(true);

    className.setText(jEdit.getProperty(CLASS_NAME));
    classArgs.setText(jEdit.getProperty(CLASS_ARGS));

    String path = jEdit.getProperty(CLASSPATH);
    if (path != null)
      classPath.setPath(path);

    path = jEdit.getProperty(SOURCEPATH);
    if (path != null)
      sourcePath.setPath(path);
  }

  protected void _save() {
    jEdit.setProperty(VM_NAME, vmName.getText());
    jEdit.setProperty(VM_ARGS, vmArgs.getText());

    String programType = "application";
    if (applet.isSelected()) {
      programType = "applet";
    }
    jEdit.setProperty(PROGRAM_TYPE, programType);

    jEdit.setProperty(CLASS_NAME, className.getText());
    jEdit.setProperty(CLASS_ARGS, classArgs.getText());
    jEdit.setProperty(CLASSPATH, classPath.getPath());
    jEdit.setProperty(SOURCEPATH, sourcePath.getPath());
  }

  private static class Filter extends FileFilter {
    public String getDescription() {
      return "Directories and Archives";
    }

    public boolean accept(File file) {
      boolean ret = false;
      if (file.isDirectory()) {
        ret = true;
      } else {
        String name = file.getName();
        if (name.endsWith(".zip") || name.endsWith(".jar")) {
          ret = true;
        }
      }

      return ret;
    }

  }

}
