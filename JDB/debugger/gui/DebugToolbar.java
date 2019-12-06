package jedu.debugger.gui;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.View;

import javax.swing.SwingUtilities;
import javax.swing.JToolBar;
import javax.swing.JComponent;

import jedu.debugger.plugin.DebuggerMessage;

public class DebugToolbar implements Runnable, EBComponent {

  public static final int STOPPED = 1;
  public static final int RUNNING = 2;
  public static final int INTERRUPTED = 3;

  public static final int START_BUTTON = 0;
  public static final int CONTINUE_BUTTON = 1;
  public static final int INTERRUPT_BUTTON = 2;
  public static final int STOP_BUTTON = 3;
  public static final int STEP_BUTTON = 5;
  public static final int STEPIN_BUTTON = 6;
  public static final int STEPOUT_BUTTON = 7;
  public static final int RUNTO_BUTTON = 8;

  public static final int BUTTON_COUNT = 9;

  private View view;
  private JComponent[] buttons = new JComponent[BUTTON_COUNT];
  private JToolBar toolbar;

  static final int[] STOPPED_STATE = { START_BUTTON };

  static final int[] RUNNING_STATE = { INTERRUPT_BUTTON, STOP_BUTTON };

  public final int[] INTERRUPTED_STATE = { CONTINUE_BUTTON, STOP_BUTTON, STEP_BUTTON, STEPIN_BUTTON, STEPOUT_BUTTON,
      RUNTO_BUTTON };

  private int state = STOPPED;

  public DebugToolbar(View view) {
    this.view = view;
    createUI();
    update(STOPPED);
  }

  public void createUI() {
    toolbar = GUIUtils.createToolBar("jdebugger-toolbar");
    for (int i = 0; i < BUTTON_COUNT; i++) {
      buttons[i] = (JComponent) toolbar.getComponentAtIndex(i);
    }
  }

  public JToolBar getToolBar() {
    return toolbar;
  }

  public void update(int newState) {
    state = newState;
    SwingUtilities.invokeLater(this);
  }

  private void enableAll(boolean value) {
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].setEnabled(value);
    }
  }

  private void enableButtons(int[] indexes, boolean value) {
    for (int i = 0; i < indexes.length; i++) {
      buttons[indexes[i]].setEnabled(value);
    }
  }

  public void run() {
    enableAll(false);
    switch (state) {
    case STOPPED:
      enableButtons(STOPPED_STATE, true);
      break;
    case RUNNING:
      enableButtons(RUNNING_STATE, true);
      break;
    case INTERRUPTED:
      enableButtons(INTERRUPTED_STATE, true);
      break;
    }
  }

  public void handleMessage(EBMessage message) {
    if (message instanceof DebuggerMessage && message.getSource() == view) {
      Object reason = ((DebuggerMessage) message).getReason();
      if (reason == DebuggerMessage.SESSION_STARTED) {
        update(RUNNING);
      } else if (reason == DebuggerMessage.SESSION_RESUMED) {
        update(RUNNING);
      } else if (reason == DebuggerMessage.SESSION_INTERRUPTED) {
        update(INTERRUPTED);
      } else if (reason == DebuggerMessage.SESSION_TERMINATED) {
        update(STOPPED);
      }
    }
  }
}
