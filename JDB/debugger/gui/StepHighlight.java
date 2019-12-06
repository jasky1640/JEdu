package jedu.debugger.gui;

import jedu.debugger.JavaDebuggerPlugin;

import jedu.debugger.core.ContextEvaluator;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

public class StepHighlight extends CustomHighlight {
  private int curline = -1;
  private static Color highlightColor = Color.cyan;
  private ContextEvaluator evaluator = null;

  public StepHighlight(JEditTextArea textarea) {
    super(textarea);
  }

  public final void setContext(ContextEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  public final void update(int lineNo) {
    curline = lineNo;
    redraw(lineNo);
  }

  public final void clear() {
    int temp = curline;
    curline = -1;
    if (temp > 0)
      redraw(temp);
  }

  public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine, int start, int end, int y) {
    TextAreaPainter painter = textarea.getPainter();
    FontMetrics fm = painter.getFontMetrics();

    // If the current line is due to a step event. Paint the bakground special
    if (curline == physicalLine + 1) {
      gfx.setColor(highlightColor);
      gfx.fillRect(0, y, painter.getWidth(), fm.getHeight());
    }

  }

  private final boolean isDebuggerRunning() {
    return JavaDebuggerPlugin.getPlugin().getDebuggerManager(textarea.getView()).getDebugger().isRunning();
  }

  private String getIdentifierAtCursor(int x, int y) {
    int offset = textarea.xyToOffset(x, y, true);
    int line = textarea.getLineOfOffset(offset);
    int startOffset = textarea.getLineStartOffset(line);
    Segment segment = new Segment();
    textarea.getLineText(line, segment);
    segment.setIndex(offset);
    char c = segment.current();
    if (Character.isUnicodeIdentifierPart(c) || Character.isUnicodeIdentifierStart(c)) {
      while (Character.isUnicodeIdentifierPart(c) || Character.isUnicodeIdentifierStart(c))
        c = segment.previous();
      c = segment.next();

      StringBuffer buffer = new StringBuffer();
      while (Character.isUnicodeIdentifierPart(c) || Character.isUnicodeIdentifierStart(c) || c == '.') {
        buffer.append(c);
        c = segment.next();
      }
      return buffer.toString();
    }
    return null;
  }

  public final String getToolTipText(int x, int y) {
    if (evaluator != null && isDebuggerRunning()) {
      try {
        String identifier = getIdentifierAtCursor(x, y);
        return evaluator.evaluate(identifier);
      } catch (Exception ex) {
        // ex.printStackTrace();
        // ignore exceptions here.
      }
    }
    return null;
  }

}
