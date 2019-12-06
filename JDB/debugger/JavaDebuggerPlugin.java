package jedu.debugger;

import jedu.debugger.plugin.Application;
import jedu.debugger.plugin.DebuggerManager;

import java.util.Hashtable;

import java.util.Iterator;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.ViewUpdate;

public class JavaDebuggerPlugin extends EBPlugin {

  private Hashtable views;

  public static void main (String[] args) {
    JavaDebuggerPlugin jdp = new JavaDebuggerPlugin();
  }

  public JavaDebuggerPlugin() {
    // Initialize the static variable which is handle to this plugin;
    plugin = this;
  }

  public void handleMessage(EBMessage message) {
    if (message instanceof ViewUpdate) {
      ViewUpdate vu = (ViewUpdate) message;
      View view = vu.getView();
      Object what = vu.getWhat();

      if (what == ViewUpdate.CREATED) {
        addView(view);
      } else if (what == ViewUpdate.CLOSED) {
        removeView(view);
      }

    }
  }

  public void start() {
    if (!MiscUtilities.isToolsJarAvailable()) {
      throw new RuntimeException("Debugger Plugin Requires JDK (not JRE) 1.3 or above");
    }
    // Initialize required data structures
    views = new Hashtable();

    // For all the already open views create a debugger manager
    View[] openViews = jEdit.getViews();
    for (int i = 0; i < openViews.length; i++) {
      if (views.get(openViews[i]) == null) {
        addView(openViews[i]);
      }
    }
  }

  public void stop() {
    Iterator iterator = views.keySet().iterator();
    while (iterator.hasNext()) {
      View view = (View) iterator.next();
      DebuggerManager manager = (DebuggerManager) views.get(view);
      manager.close();
    }

    views.clear();
    Application.getInstance().close();

    views = null;
  }

  private final void addView(View view) {
    if (views.get(view) == null) {
      DebuggerManager manager = new DebuggerManager(view);
      views.put(view, manager);
    }
  }

  private final void removeView(View view) {
    DebuggerManager manager = (DebuggerManager) views.remove(view);
    if (manager != null) {
      manager.close();
    }
  }

  public final DebuggerManager getDebuggerManager(View view) {
    return (DebuggerManager) views.get(view);
  }

  /**
   * Returns the instance of JavaDebuggerPlugin running. Since the plugin is a
   * singleton this method helps to get the handle to the singleton
   */
  private static JavaDebuggerPlugin plugin;

  public static JavaDebuggerPlugin getPlugin() {
    return plugin;
  }

}
