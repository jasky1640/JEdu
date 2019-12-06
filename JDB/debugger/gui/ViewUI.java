
/**
 * This class  stores the Data specific to each View
 */

package jedu.debugger.gui;

import com.sun.jdi.StackFrame;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.WatchpointRequest;
import jedu.debugger.core.ContextEvaluator;
import jedu.debugger.options.DebuggerOptions;
import jedu.debugger.plugin.Application;
import jedu.debugger.plugin.DebuggerMessage;
import jedu.debugger.plugin.EventDispatcher;
import jedu.debugger.plugin.SourceLocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

public final class ViewUI implements EBComponent {

  // A map of all the textareas for the current view.
  // since when view is split we can have more than one text area.
  private HashMap textAreaMap = new HashMap();
  private View view;
  private DebugToolbar toolbar;

  public ViewUI(View view) {
    this.view = view;

    toolbar = new DebugToolbar(view);
    view.addToolBar(toolbar.getToolBar());

    EditBus.addToBus(this);
    EditBus.addToBus(toolbar);

    updateTextArea(view.getEditPane().getTextArea());
  }

  static final class TextAreaUI {
    TextAreaUI(JEditTextArea textArea) {
      this.textArea = textArea;

      bph = new BreakpointHighlight(textArea);
      textArea.getGutter().addExtension(bph);

      sh = new StepHighlight(textArea);
      textArea.getPainter().addExtension(sh);
    }

    public final void close() {
      textArea.getPainter().removeExtension(sh);
      textArea.getGutter().removeExtension(bph);
    }

    // We defer creating poup menus unless it is a java file.

    JComponent[] getPopupItems() {
      JPopupMenu pm = textArea.getRightClickPopup();
      if (pm == null) {
        return null;
      }

      if (popupItems == null) {
        popupItems = GUIUtils.createMenuItems("jdebugger-popup");
        for (int i = 0; i < popupItems.length; i++) {
          pm.insert(popupItems[i], i);
        }
      }
      return popupItems;
    }

    JEditTextArea textArea;
    BreakpointHighlight bph;
    StepHighlight sh;
    JComponent[] popupItems;
  }

  /**
   * Handles EditBus Messages.
   */
  public void handleMessage(EBMessage message) {
    if (message instanceof EditPaneUpdate) {
      handleEditPaneUpdate((EditPaneUpdate) message);
    } else if (message instanceof BufferUpdate) {
      handleBufferUpdate((BufferUpdate) message);
    } else if (message instanceof DebuggerMessage && message.getSource() == view) {
      handleDebuggerMessage((DebuggerMessage) message);
    }
  }

  private void handleEditPaneUpdate(EditPaneUpdate eup) {
    EditPane pane = eup.getEditPane();
    // If this message is not for the current view return
    if (view != pane.getView()) {
      return;
    }

    JEditTextArea ta = pane.getTextArea();

    Object what = eup.getWhat();
    if (what == EditPaneUpdate.BUFFER_CHANGED) {
      updateView();
    } else if (what == EditPaneUpdate.CREATED) {
      updateTextArea(ta);
    } else if (what == EditPaneUpdate.DESTROYED) {
      textAreaMap.remove(ta);
    }
  }

  private static boolean isJavaFile(Buffer buffer) {
    return buffer.getName().endsWith(".java")
        || (buffer.getMode() != null && buffer.getMode().getName().equals("java"));
  }

  private void handleBufferUpdate(BufferUpdate bu) {
    View bufferView = bu.getView();

    // When the editor is opened we get a buffer update with out an associated view.
    // If the buffer update is not for this view ignore it.
    if (bufferView != null && view != bu.getView()) {
      return;
    }

    Object what = bu.getWhat();
    if (what == BufferUpdate.PROPERTIES_CHANGED) {
      if (isJavaFile(bu.getBuffer())) {
        updateView();
      }
    }
  }

  void updateTextArea(JEditTextArea textArea) {
    if (textAreaMap.get(textArea) == null) {
      TextAreaUI tui = new TextAreaUI(textArea);
      textAreaMap.put(textArea, tui);
    }
    updateView();
  }

  protected void updateView() {
    JEditTextArea ta = view.getTextArea();
    TextAreaUI ui = (TextAreaUI) textAreaMap.get(ta);
    if (ui == null)
      return;

    // show popup entries only for java files.
    boolean isJavaFile = false;
    isJavaFile = isJavaFile(view.getBuffer());

    JComponent[] popupItems = ui.getPopupItems();
    if (popupItems != null) {
      for (int i = 0; i < popupItems.length; i++) {
        popupItems[i].setVisible(isJavaFile);
      }
    }

    if (isJavaFile) {
      BreakpointHighlight bph = ui.bph;
      String file = view.getBuffer().getPath();
      List List = Application.getInstance().getBreakpointsForFile(file);
      bph.update(List);
    }
  }

  public final BreakpointHighlight getBreakpointHighlight() {
    TextAreaUI tp = (TextAreaUI) textAreaMap.get(view.getTextArea());
    return tp.bph;
  }

  public final StepHighlight getStepHighlight() {
    TextAreaUI tp = (TextAreaUI) textAreaMap.get(view.getTextArea());
    return tp.sh;
  }

  protected void handleDebuggerMessage(DebuggerMessage message) {
    Object reason = message.getReason();
    if (reason == DebuggerMessage.SESSION_TERMINATED) {
      getStepHighlight().clear();
      setStatus("Program terminated");
    } else if (reason == DebuggerMessage.SESSION_STARTED) {
      setStatus("Program started");
    } else if (reason == DebuggerMessage.SHOW_SOURCE) {
      SourceLocation location = (SourceLocation) message.getInfo();
      openLocation(location);
    } else if (reason == DebuggerMessage.SESSION_RESUMED) {
      TextAreaUI tui = (TextAreaUI) textAreaMap.get(message.getView().getTextArea());
      tui.sh.setContext(null);
    } else if (reason == DebuggerMessage.STACK_FRAME_CHANGED) {
      StackFrame stack = (StackFrame) message.getInfo();
      if (jEdit.getBooleanProperty(DebuggerOptions.SHOW_TOOLTIP, true)) {
        TextAreaUI tui = (TextAreaUI) textAreaMap.get(message.getView().getTextArea());
        tui.sh.setContext(new ContextEvaluator(stack));
      }
    } else if (reason == DebuggerMessage.EVENT_HIT) {
      handleBreakpoint(message);
    }
  }

  private final void setStatus(String message) {
    view.getStatus().setMessageAndClear(message);
  }

  /** go to the given source location */
  private final void openLocation(SourceLocation location) {
    String file = location.getFilename();

    // A null file indicates we do not have source file or do not know where it is
    // so ignore it.
    if (file == null)
      return;

    final int line = location.getLineNumber();
    final Buffer buffer = jEdit.openFile(view, file);

    if (buffer == null) {
      setStatus("Failed to open : " + file + " ...");
      return;
    }

    VFSManager.runInAWTThread(new Runnable() {
      public void run() {
        if (buffer.getLineCount() >= line) {
          JEditTextArea ta = view.getTextArea();
          TextAreaUI tui = (TextAreaUI) textAreaMap.get(ta);
          if (tui != null) {
            tui.sh.update(line);
          }
          int start = buffer.getLineStartOffset(line - 1);
          int end = buffer.getLineEndOffset(line - 1);
          ta.setSelection(new Selection.Range(start, end));
          ta.moveCaretPosition(start);
        }
      }
    });
  }

  private void handleBreakpoint(DebuggerMessage message) {
    LocatableEvent event = (LocatableEvent) message.getInfo();
    if (event instanceof WatchpointRequest)
      setStatus(WATCHPOINT_MESSAGE);
    else
      setStatus(BREAKPOINT_MESSAGE);

    if (jEdit.getBooleanProperty(DebuggerOptions.SHOW_TOOLTIP, true)) {
      try {
        TextAreaUI tui = (TextAreaUI) textAreaMap.get(message.getView().getTextArea());
        tui.sh.setContext(new ContextEvaluator(event));
      } catch (Exception ex) {
      }
    }
    SourceLocation location = EventDispatcher.getDebuggerLocation(message.getSession(), event.location());
    openLocation(location);
  }

  /**
   * Do the clean up of view private stuff.
   */
  public void close() {
    Iterator iterator = textAreaMap.keySet().iterator();
    while (iterator.hasNext()) {
      TextAreaUI tui = (TextAreaUI) textAreaMap.get(iterator.next());
      tui.close();
    }
    textAreaMap.clear();
    if (!view.isClosed()) {
      view.removeToolBar(toolbar.getToolBar());
    }

    EditBus.removeFromBus(toolbar);
    EditBus.removeFromBus(this);
  }

  private static final String BREAKPOINT_MESSAGE = "Breakpoint reached ...";
  private static final String WATCHPOINT_MESSAGE = "Watchpoint reached ...";
}
