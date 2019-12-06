package jedu.debugger.gui;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

public abstract class CustomHighlight extends TextAreaExtension implements Runnable {

  protected JEditTextArea textarea = null;

  public CustomHighlight(JEditTextArea ta) {
    textarea = ta;
  }

  /** Helper routine to force redraw of a line */
  public final void redraw(int lineno) {
    textarea.invalidateLine(lineno);
    try {
      SwingUtilities.invokeLater(this);
    } catch (Exception ex) {
    }
  }

  public void run() {
    textarea.repaint();
  }

}
