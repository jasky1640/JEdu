/*
* TestRunner.java
* Copyright (c) 2001 - 2003 Andre Kaplan
* Copyright (c) 2006 Denis Koryavov
* Copyright (c) 2011 Eric Le Lay
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package junit.jeditui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Image;

import java.net.URL;

import java.util.Enumeration;

import javax.swing.*;

import junit.JEditReloadingTestSuiteLoader;
import junit.JUnitPlugin;
import junit.PluginTestCollector;

import org.junit.runner.*;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.StoppedByUserException;

import org.gjt.sp.jedit.gui.BeanShellErrorDialog;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

/**
* A test runner for jEdit.
*/
public class TestRunner extends RunListener implements TestRunContext{
        private View fView;
        private DefaultListModel failures;
        private Thread runnerThread;
        private RunNotifier fRunNotifier;
        private DetailedResult fResult;
        private String classPath;
        private JUnitDockable dockable;
        
        //{{{ TestRunner
        public TestRunner(View view) {
                fView = view;
                failures = new DefaultListModel();
        } //}}}
        
        //{{{ _createUI method.
        public JPanel _createUI(String position, boolean selected) {
                return dockable = new JUnitDockable(this, position, selected);
        } //}}}
        
        //{{{ getClassPath method.
        public String getClassPath() {
                if (classPath == null) {
                        classPath = JUnitPlugin.getClassPath();
                }
                return classPath;
        } 
        //}}}
        
        //{{{ setClassPath method.
        public void setClassPath(String aClassPath) {
                classPath = aClassPath;
        } 
        //}}}
        
        //{{{ createTestCollector method.
        public PluginTestCollector createTestCollector() {
        	String cp = getClassPath();
                if (cp.length() == 0) {
                	return new PluginTestCollector();
                }else{
                	return new PluginTestCollector(cp);
                }
        } //}}}
        
        //{{{ getView method.
        public View getView() {
                return fView;
        } //}}}
        
        //{{{ runSuite method.
        synchronized public void runSuite() {
                if (runnerThread != null) {
                        fRunNotifier.pleaseStop();
                } else {
                        reset();
                        dockable.showInfo("Loading Test Case...");
                        
                        final String suiteName = dockable.getCurrentTest();
                        try{
                        	final Runner testSuite = Request.aClass(getLoader().load(suiteName)).getRunner();
				doRunTest(testSuite);
                        }catch(ClassNotFoundException e){
                        	throw new NoClassDefFoundError(suiteName);
                        }
                }
        } 
        //}}}
        
        // {{{ TestRunContext Methods
        /**
        * Run the current test.
        */
        public void runSelectedTest(Description test) {
                rerunTest(test);
        }
        
        public void handleTestSelected(Description test) {
                dockable.showFailureDetail(test);
        }
        
        public ListModel getFailures() {
                return failures;
        }
        // }}}
        
        // {{{ RunListener Methods
        @Override
        public void testAssumptionFailure(final Failure failure) {
                
        	SwingUtilities.invokeLater(
                        new Runnable() {
                                public void run() {
					dockable.setAssumptionCount(fResult.getAssumptionCount());
					appendFailure("Assumption", failure);
                                }
                        });
        }

        @Override
        public void testFailure(final Failure failure) {
        	final boolean isFailure = JUnitPlugin.isFailure(failure);
                SwingUtilities.invokeLater(
                        new Runnable() {
                                public void run() {
                                	if(isFailure){
                                		dockable.setFailureCount(fResult.getFailureCount());
                                		appendFailure("Failure", failure);
                                	}else{
                                		dockable.setErrorCount(fResult.getErrorCount());
                                		appendFailure("Error", failure);
                                	}
                                }
                        });
        }
        
        @Override
        public void testStarted(Description description) {
                dockable.showInfo("Running: " + description.getDisplayName());
        }
        
        @Override
        public void testFinished(Description description) {
                synchUI();
                SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                        if (fResult != null) {
                                                 dockable.setRunCount(fResult.getRunCount());
                                                 dockable.updateProgress(fResult.wasSuccessful());
                                        }
                                }
                });
        }
        
        @Override
        public void testIgnored(Description d) {
        	// let's say ignored tests don't change the status OK/KO of the tests
        	dockable.updateProgress(true);
        }

        @Override
        public void testRunFinished(Result result) {
        	System.err.println("tests finished");
        }
        // }}}
        
        public JEditReloadingTestSuiteLoader getLoader() {
                if (getClassPath().length() == 0) {
                        return new JEditReloadingTestSuiteLoader();
                } else {
                        return new JEditReloadingTestSuiteLoader(getClassPath());
                }
        }
        
        //{{{ getIconResource method.
        static Icon getIconResource(Class c, String name) {
                URL url = c.getResource(name);
                if (url == null) {
                        Log.log(Log.ERROR, TestRunner.class, "Warning: could not load \"" + name
                                + "\" icon");
                        return null;
                }
                return new ImageIcon(url);
        } 
        //}}}
        
        //{{{ createTestResult method.
        protected RunNotifier createTestResult() {
                return new RunNotifier();
        } //}}}
        
        //{{{ rerunTest method.
        /**
         * rerun selected test case (can be 1 test of a class).
         * code is very similar to doRunTest(), buy
         * - the TestRunner is not registered as listener, so
         *   run/error/failure counts are not updated
         */
        private void rerunTest(final Description test) {
        	Log.log(Log.DEBUG,TestRunner.class,"Reruning test "+test);
        	String suiteName = test.getClassName();
        	Request rerunTest = null;
        	try{
        		rerunTest = Request.aClass(getLoader().load(suiteName));
        	}catch(ClassNotFoundException e){
        		Log.log(Log.WARNING,TestRunner.class, "test class not found "+suiteName);
        		return;
        	}
        	if(!rerunTest.getRunner().getDescription().equals(test)){
        		rerunTest = rerunTest.filterWith(test);
        	}
        	final Runner testSuite = rerunTest.getRunner();
                fRunNotifier = createTestResult();
                fResult = new DetailedResult();
                fRunNotifier.addListener(fResult);
                
                runnerThread = new Thread("TestRunner-Thread") {
                        public void run() {
                                dockable.startTesting(testSuite.testCount());
                                try{
					fRunNotifier.fireTestRunStarted(testSuite.getDescription());
					testSuite.run(fRunNotifier);
					fRunNotifier.fireTestRunFinished(new Result());
					String message = test.toString();
					if (fResult.wasSuccessful()) {
						dockable.showInfo(message + " was successful");
						removeFailure(testSuite.getDescription());
					} else if (fResult.getErrorCount() == 1) {
						Failure tf = fResult.getFailures().get(0);
						appendFailure("Error", tf);
						dockable.showStatus(message + " had an error");
					} else if(fResult.getFailureCount() == 1){
						Failure tf = fResult.getFailures().get(0);
						appendFailure("Failure", tf);
						dockable.showStatus(message + " had a failure");
					}
					dockable.repaintViews(testSuite.getDescription(), fRunNotifier, fResult);
                                }catch(StoppedByUserException e){
                                	// notified like this that the user has stopped the test
                                	dockable.showStatus("Stopped");
                                }
                                dockable.runFinished(testSuite.getDescription(), fRunNotifier, fResult);
                                runnerThread = null;
                                fRunNotifier = null;
                                System.gc();
                        }
                };
                
                runnerThread.start();
        }
        //}}}
        
        //{{{ doRunTest method.
        private void doRunTest(final Runner testSuite) {
                runnerThread = new Thread("TestRunner-Thread") {
                        public void run() {
                                dockable.startTesting(testSuite.testCount());
                                try{
                                	// fireTestRunStarted is not called by the runner, so testRunStarted was not called either in listener
                                	fRunNotifier.fireTestRunStarted(testSuite.getDescription());
                                	testSuite.run(fRunNotifier);
                                	// fireTestRunFinished is not called by the runner, so testRunFinished was not called either in listener
                                	fRunNotifier.fireTestRunFinished(new Result());
					dockable.showInfo("Finished: " 
						 + (fResult.getRunTime() / 1000)
						 + " seconds");
                                }catch(StoppedByUserException e){
                                	// notified like this that the user has stopped the test
                                	dockable.showStatus("Stopped");
                                }
                                dockable.runFinished(testSuite.getDescription(), fRunNotifier, fResult);
                                runnerThread = null;
                                System.gc();
                        }
                };
                
                // make sure that the test result is created before we start the
                // test runner thread so that listeners can register for it.
                
                fRunNotifier = createTestResult();
                fResult = new DetailedResult();
                fRunNotifier.addListener(fResult);
                fRunNotifier.addListener(TestRunner.this);
                dockable.aboutToStart(testSuite.getDescription(), fRunNotifier, fResult);
                runnerThread.start();
        }
        //}}}
        
        //{{{ synchUI method.
        /**
        * Wait until all the events are processed in the event thread
        */
        private void synchUI() {
                try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                                        public void run() {
                                        }
                        });
                } catch (Exception e) {
                        e.printStackTrace();
                }
        } 
        //}}}
        
        //{{{ reset method.
        private void reset() {
                failures.clear();
                dockable.reset();
        } 
        //}}}
        
        //{{{ appendFailure method.
        private void appendFailure(String kind, Failure failure) {
                failures.addElement(failure);
                if (failures.size() == 1)
                        dockable.revealFailure(failure);
        } 
        //}}}
        
        //{{{ removeFailure method.
        private void removeFailure(Description test) {
                for(int i = 0; i < failures.getSize(); i++) {
                        Failure tf = (Failure)failures.getElementAt(i);
			// physical equality won't work here
                        if(tf.getDescription().equals(test)) {
                                failures.removeElementAt(i);
                                return; 
                        }
                }
        } //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
