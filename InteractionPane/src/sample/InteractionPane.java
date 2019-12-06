package sample;

import org.gjt.sp.jedit.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;

/**
 * The actual built jEdit plugin.
 * Notice that unlike the Main class, this Interaction Pane plugin is based on JavaSwing due certain
 * requirements of jEdit library interface. This View will be passed into the jEdit dockview in order
 * to render the plugin.
 * @author Yue Shu, Yihe Guo, Shiqing Gao
 */

public class InteractionPane extends JPanel implements EBComponent{

    private final JTextArea cmdTextArea;
    private final JTextArea outputTextArea;

    private View view;

    private int loc;
    private List<String> prevInput;

    public InteractionPane(View view) {
        super(new BorderLayout());
        this.view = view;

        prevInput = new ArrayList<>();

        cmdTextArea = new JTextArea(5, 60);
        outputTextArea = new JTextArea(30, 60);
        outputTextArea.setEditable(false);

        ExampleJShell es = new ExampleJShell();

        String[] openedClassfiles = getActionNames();

        for (String classPaths : openedClassfiles) {
            es.evaluate(path);
        }

        cmdTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP && loc >= 0) {
                    cmdTextArea.setText(prevInput.get(loc));
                    loc--;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN && loc < prevInput.size() - 1) {
                    cmdTextArea.setText(prevInput.get(loc + 1));
                    loc++;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = cmdTextArea.getText();
                    // truncate the leading line
                    while (input.length() > 0 && input.charAt(0) == '\n') {
                        input = input.substring(1);
                    }

                    // truncate the tailing line
                    while (input.length() > 0 && input.charAt(input.length() - 1) == '\n') {
                        input = input.substring(0, input.length() - 1);
                    }
                    System.out.println("the input is: " + input);

                    if (input.length() < 1) {
                        cmdTextArea.setText("");
                        return;
                    }

                    outputTextArea.append("> " + input + "\n");
                    String output = es.useJshell(input + ";");
                    if (output.length() > 0) {
                        outputTextArea.append(output + "\n");
                    }
                    prevInput.add(input);
                    cmdTextArea.setText("");
                    loc = prevInput.size() - 1;
                    System.out.println("loc is " + loc);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        JScrollPane pane = new JScrollPane(outputTextArea);

        add(BorderLayout.CENTER, pane);
        add(BorderLayout.NORTH, cmdTextArea);

    }

    public void handleMessage(EBMessage message) {

    }

    public void addNotify() {
        super.addNotify();
        EditBus.addToBus(this);
    }

    public void removeNotify() {
        super.removeNotify();
        EditBus.removeFromBus(this);
    }
}
