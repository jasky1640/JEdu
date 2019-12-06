package jedu.debugger.core;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

import jedu.debugger.event.EventAdapter;

import java.util.List;

public class StepHandler extends EventAdapter {
  ThreadReference curThread;

  public static void main (String[] args) {
    StepHandler sh = new StepHandler();    
    System.out.println("here");
  }

  public StepHandler() {
  }

  protected void step(int stepType) {
    if (curThread == null)
      return;
    clearPreviousStepRequest(curThread);
    int depth = StepRequest.STEP_LINE;
    VirtualMachine vm = curThread.virtualMachine();
    EventRequestManager evmgr = vm.eventRequestManager();
    StepRequest request = evmgr.createStepRequest(curThread, depth, stepType);
    request.addCountFilter(1);
    request.enable();
    vm.resume();
  }

  public final void stepOver() {
    step(StepRequest.STEP_OVER);
  }

  public final void stepInto() {
    step(StepRequest.STEP_INTO);
  }

  public final void stepOut() {
    step(StepRequest.STEP_OUT);
  }

  public void clearPreviousStepRequest(ThreadReference trf) {
    EventRequestManager evmgr = trf.virtualMachine().eventRequestManager();
    List requests = evmgr.stepRequests();
    int size = requests.size();
    for (int i = 0; i < size; i++) {
      StepRequest request = (StepRequest) requests.get(i);
      if (request.thread().equals(trf)) {
        evmgr.deleteEventRequest(request);
      }
    }
  }

  public void event(Event evt) {
    if (evt instanceof LocatableEvent) {
      LocatableEvent levt = (LocatableEvent) evt;
      curThread = levt.thread();
    } else {
      curThread = null;
    }
  }

}
