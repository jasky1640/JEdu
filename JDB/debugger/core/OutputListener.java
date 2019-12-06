package jedu.debugger.core;

import java.util.EventListener;

public interface OutputListener extends EventListener {
  public interface STDINListener extends OutputListener {
  }

  public interface STDOUTListener extends OutputListener {
  }

  public interface STDERRListener extends OutputListener {
  }

  public void outputLine(String line);
}
