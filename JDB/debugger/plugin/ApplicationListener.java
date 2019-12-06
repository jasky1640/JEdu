package jedu.debugger.plugin;

import jedu.debugger.spec.EventSpec;

import java.util.EventListener;

/**
 * Breakpoints are common across multiple running debugger instances. This
 * listener is used to listen to global changes.
 */

public interface ApplicationListener extends EventListener {
  public void eventAdded(EventSpec event);

  public void eventRemoved(EventSpec event);

  public void eventModified(EventSpec event);
}