package jedu.debugger.gui;

import com.sun.jdi.event.Event;

import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;

import java.awt.event.MouseEvent;
import org.gjt.sp.jedit.View;

public class EventPanel extends TabPanel {

  JTextArea output;
  JPopupMenu popup;

  public EventPanel() {
  }

  protected void createUI() {
    output = new JTextArea();
    output.setEditable(false);

    JScrollPane scroll = new JScrollPane(output);
    panel.add(scroll, BorderLayout.CENTER);

    popup = GUIUtils.createPopupMenu("events.popup", actions);
    output.addMouseListener(mouseHandler);
  }

  public void event(Event evt) {
    output.append(evt.toString() + '\n');
  }

  protected void createActions() {
    actions.addAction(new AbstractAction("events.clear") {
      public void invoke(View view) {
        output.setText("");
      }
    });
  }

  protected JPopupMenu getPopupMenu(MouseEvent evt) {
    return popup;
  }

}
