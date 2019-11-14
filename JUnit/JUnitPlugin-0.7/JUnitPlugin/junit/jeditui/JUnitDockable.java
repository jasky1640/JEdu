/*
 * jUnitDockable.java
 * Copyright (c) 2003 Calvin Yu
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

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.junit.runner.Describable;

import junit.JUnitPlugin;
import junit.PluginTestCollector;

import org.junit.runner.*;
import org.junit.runner.notification.*;

import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import projectviewer.*;

import projectviewer.config.*;

import projectviewer.vpt.*;

// TODO: [if ProjectViewer.getActiveProject(jEdit.getActiveView()) = null {classPathButton.setEnabled(false)}]

class JUnitDockable extends JPanel {
        //{{{ private members.
        private static final Icon RUN_ICON = GUIUtilities.loadIcon("Run.png"); 
        private static final Icon STOP_ICON = GUIUtilities.loadIcon("Cancel.png");
        
        private static final Icon NEXT_ICON = GUIUtilities.loadIcon("ArrowR.png"); 
        private static final Icon PREV_ICON = GUIUtilities.loadIcon("ArrowL.png"); 
        
        private static final Icon OPEN_ICON = GUIUtilities.loadIcon("Open.png"); 
        private static final Icon PROPS_ICON = GUIUtilities.loadIcon("ButtonProperties.png");
        
        private static final int GAP = 4;
        private static final String FAILUREDETAILVIEW_KEY = "FailureViewClass";
        private TestRunner runner;
        private TestHierarchyRunView hierarchyRunView;
        private FailureRunView failureRunView;
        
        private HistoryTextField currentTest;
        private ProgressBar progressBar;
        private CounterPanel counter;
        private DefaultFailureDetailView failureDetailView;
        
        private RolloverButton runButton;
        private RolloverButton stopButton;
        private RolloverButton nextButton;
        private RolloverButton prevButton;
        private RolloverButton browseButton;
        private RolloverButton classPathButton;
        
        private JLabel animationLabel;
        private AnimatedIcon animation;
        
        private JCheckBox toggleViewsCheckBox;
        private JCheckBox filterCheckBox;
        private JPanel testRunViewsPanel;
        
        /**
        * Views associated with testRunViews.
        */
        private Vector testRunViews = new Vector(); 
        //}}}
        
        //{{{ constructor.
        public JUnitDockable(TestRunner aRunner, String position, boolean selected) {
                runner = aRunner;
                currentTest = createCurrentTestField();
                
                Box box = new Box(BoxLayout.X_AXIS);
                box.add(new JTextArea("Enter a text Class"));
//                animationLabel = new JLabel();
//                animationLabel.setBorder(new EmptyBorder(2,3,2,3));
//                Toolkit toolkit = getToolkit();
//
//                animation = new AnimatedIcon(
//                        toolkit.getImage(getClass().getResource("/junit/jeditui/icons/Blank.png")),
//                        new Image[] {
//                                toolkit.getImage(getClass().getResource("/junit/jeditui/icons/Active1.png")),
//                                toolkit.getImage(getClass().getResource("/junit/jeditui/icons/Active2.png")),
//                                toolkit.getImage(getClass().getResource("/junit/jeditui/icons/Active3.png")),
//                                toolkit.getImage(getClass().getResource("/junit/jeditui/icons/Active4.png"))
//                        },10,animationLabel
//                );
//                animationLabel.setIcon(animation);
//                animationLabel.setVisible(false);
//                animation.stop();
//                JPanel animationPane = new JPanel(new BorderLayout());
//                animationPane.setPreferredSize(new Dimension(24,24));
//                animationPane.add(animationLabel, BorderLayout.CENTER);
                //box.add(animationPane);
                
                //box.add(toggleViewsCheckBox = createToggleViewsCheckBox());
                //box.add(browseButton = createBrowseButton());
                //box.add(classPathButton = createSetClassPathButton());
                //box.add(runButton = createRunButton());
                // box.add(stopButton = createStopButton());
                //box.add(Box.createHorizontalStrut(5));
                
                //box.add(prevButton = createPrevButton());
                //box.add(nextButton = createNextButton());
                //box.add(Box.createHorizontalStrut(5));
                
                counter = createCounterPanel();
                
                progressBar = new ProgressBar();
                testRunViewsPanel = createTestRunViews();
                
                Box traceBox = new Box(BoxLayout.X_AXIS);
                filterCheckBox = new JCheckBox("Filter Stack Trace", selected);
                filterCheckBox.addActionListener(new ActionListener(){
                                public void actionPerformed(ActionEvent e) {
                                       toggleFilter();
                                }
                });
                traceBox.add(filterCheckBox);
                traceBox.add(Box.createGlue());

                failureDetailView = createFailureDetailView();
                
                JPanel tracePanel = new JPanel(new BorderLayout());
                tracePanel.add(traceBox, BorderLayout.NORTH);
                tracePanel.add(failureDetailView.getComponent(), BorderLayout.CENTER);
                
                JScrollPane tracePane = new JScrollPane(tracePanel,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                
                
                int orientation = JSplitPane.HORIZONTAL_SPLIT;
                
                setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = -1;
                // comp gbc w fill wx anchor
                if (position.equals(DockableWindowManager.LEFT) ||
			position.equals(DockableWindowManager.RIGHT)) 
                {
                        orientation = JSplitPane.VERTICAL_SPLIT;
                        
                        startRow(box, gbc, 3, GridBagConstraints.NONE, 0,
                                GridBagConstraints.EAST);
                        startRow(currentTest, gbc, 3, 
                                GridBagConstraints.HORIZONTAL, 1,
                                GridBagConstraints.CENTER);
                        
                        startRow(counter, gbc, 1, 
                                GridBagConstraints.NONE, 0,
                                GridBagConstraints.WEST);
                
                        startRow(progressBar, gbc, 3, 
                                GridBagConstraints.HORIZONTAL, 1,
                                GridBagConstraints.CENTER);
                        
                } else {
                        startRow(currentTest, gbc, 2, 
                                GridBagConstraints.HORIZONTAL, 1,
                                GridBagConstraints.CENTER);
                        nextCol(box, gbc, 1, GridBagConstraints.NONE, 0,
                                GridBagConstraints.EAST);
                        
                        startRow(counter, gbc, 1, 
                                GridBagConstraints.NONE, 0,
                                GridBagConstraints.WEST);
                        
                        nextCol(progressBar, gbc, 3, 
                                GridBagConstraints.HORIZONTAL, 1,
                                GridBagConstraints.CENTER);
                }
                
                 JSplitPane splitter = new JSplitPane(orientation,
                        testRunViewsPanel, tracePane);
                 splitter.setDividerLocation(300);
                startRow(splitter, gbc, 3, GridBagConstraints.BOTH, 1,
                        GridBagConstraints.WEST);
        } 
        //}}}
        
        //{{{ toggleFilter method.
        private void toggleFilter() {
                jEdit.setBooleanProperty("junit.filter-stack-trace", 
                        filterCheckBox.isSelected());
                
                boolean b = toggleViewsCheckBox.isSelected();
                TestRunView rView = b ? failureRunView : hierarchyRunView;
                rView.activate();
        } 
        //}}}
        
        //{{{ setCurrentTest method.
        public void setCurrentTest(String suiteName) {
                currentTest.setText(suiteName);
        } //}}}
        
        //{{{ startTesting method.
        public void startTesting(final int testCount) {
                SwingUtilities.invokeLater(
                        new Runnable() {
                                public void run() {
                                        progressBar.start(testCount);
                                        counter.setTotal(testCount);
                                        showInfo("Running...");
                                        stopButton.setEnabled(true);
                                        runButton.setEnabled(false);
                                        animationLabel.setVisible(true);
                                        animation.start();
                                }
                        });
        } //}}}
        
        //{{{ aboutToStart method.
        public void aboutToStart(Description testSuite, RunNotifier rn, DetailedResult r) {
                for (Enumeration e = testRunViews.elements(); e.hasMoreElements();) {
                        TestRunView v = (TestRunView) e.nextElement();
                        v.aboutToStart(testSuite, rn, r);
                }
        } //}}}
        
        //{{{ runFinished method.
        public void runFinished(final Description testSuite, final RunNotifier rn, final DetailedResult result) {
                SwingUtilities
                .invokeLater(
                        new Runnable() {
                                public void run() {
                                        stopButton.setEnabled(false);
                                        runButton.setEnabled(true);
                                        animationLabel.setVisible(false);
                                        animation.stop();
                                        for (Enumeration e = testRunViews.elements(); e.hasMoreElements();) {
                                                TestRunView v = (TestRunView) e.nextElement();
                                                v.runFinished(testSuite, rn, result);
                                        }
                                }
                        });
        }
        //}}}
        
        //{{{ showFailureDetail method.
        public void showFailureDetail(Description test) {
                Hashtable filters = (Hashtable)JUnitPlugin.getFilters(false);
                if (test != null) {
                        ListModel failures = runner.getFailures();
                        for (int i = 0; i < failures.getSize(); i++) {
                                Failure failure = (Failure) failures.getElementAt(i);
                                if (failure.getDescription().equals(test)) {
                                        failureDetailView.showFailure(failure);
                                        return;
                                }
                        }
                }
                failureDetailView.clear();
        } 
        //}}}
        
        //{{{ clearStatus method.
        public void clearStatus() {
                View view = jEdit.getActiveView();
                view.getStatus().setMessageAndClear("");
        } //}}}
        
        //{{{ reset method.
        public void reset() {
                counter.reset();
                progressBar.reset();
                failureDetailView.clear();
        } //}}}
        
        //{{{ repaintViews method.
        public void repaintViews(Description test, RunNotifier rn, DetailedResult result) {
                hierarchyRunView.refresh(test, rn, result);
                // failureRunView.refresh(test, result);
        } 
        //}}}
        
        //{{{ getCurrentTest method.
        public String getCurrentTest() {
                if (currentTest == null)
                        return "";
                return currentTest.getText();
        } //}}}
        
        //{{{ revealFailure method.
        public void revealFailure(Failure test) { 
                for (Enumeration e = testRunViews.elements(); e.hasMoreElements();) {
                        TestRunView v = (TestRunView) e.nextElement();
                        v.revealFailure(test);
                }
        } //}}}
        
        //{{{ browseTestClasses method.
        public void browseTestClasses() {
                PluginTestCollector collector = runner.createTestCollector();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                TestSelector selector = new TestSelector(runner.getView(), collector.collectTests());
                setCursor(Cursor.getDefaultCursor());
                if (selector.isEmpty()) {
                        JOptionPane.showMessageDialog(runner.getView(), 
                                "No Test Cases found.");
                        return;
                }
                
                selector.setVisible(true);
                String className = selector.getSelectedItem();
                
                if (className != null)
                        setCurrentTest(className);
                currentTest.requestFocus();
        } 
        //}}}
        
        //{{{ configureClassPath method.
        public void configureClassPath() {
                VPTProject pr = ProjectViewer
                .getActiveProject(jEdit.getActiveView());
                if (pr == null) {
                        JOptionPane.showMessageDialog(runner.getView(),
                                jEdit
                                .getProperty("junit.error.no-project-selected.message"),
                                jEdit.getProperty("junit.dock.title"),
                                JOptionPane.ERROR_MESSAGE
                                );
                        return;
                }
                projectviewer.PVActions.editProject(pr, "junit.pconfig");
                runner.setClassPath(JUnitPlugin.getClassPath());
        } 
        //}}}
        
        //{{{ setErrorCount method.
        public void setErrorCount(int count) {
                counter.setErrorValue(count);
        } //}}}
        
        //{{{ setFailureCount method.
        public void setFailureCount(int count) {
                counter.setFailureValue(count);
        } //}}}
        
        //{{{ setRunCount method.
        public void setRunCount(int count) {
                counter.setRunValue(count);
        } //}}}

        //{{{ setAssumptionCount method.
        public void setAssumptionCount(int count) {
                counter.setAssumptionValue(count);
        } //}}}
        
        //{{{ showInfo method.
        public void showInfo(final String message) {
                View view = jEdit.getActiveView();
                if (SwingUtilities.isEventDispatchThread()) {
                        view.getStatus().setMessage(message);
                } else {
                        SwingUtilities
                        .invokeLater(new Runnable() {
                                        public void run() {
                                                showInfo(message);
                                        }
                        });
                }
        } 
        //}}}
        
        //{{{ showStatus method.
        public void showStatus(final String message) {
                showInfo(message);
        } //}}}
        
        //{{{ updateProgress method.
        public void updateProgress(boolean success) {
                progressBar.step(success);
        } //}}}
        
        //{{{ createCurrentTestField method.
        private HistoryTextField createCurrentTestField() {
                HistoryTextField field =
                new HistoryTextField("junit.test-suite.history", false, true);
                field.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                        try {
                                                runner.runSuite();
                                        } catch (NoClassDefFoundError e) {
                                                int result = GUIUtilities
                                                .confirm(runner.getView(),
                                                        "junit.error.class-not-found",
                                                        new String[] { e.getMessage() },
                                                        JOptionPane.OK_CANCEL_OPTION,
                                                        JOptionPane.ERROR_MESSAGE);
                                                
                                                if (result == JOptionPane.OK_OPTION) {
                                                        configureClassPath();
                                                }
                                        }
                                }
                });
                
                
                field.getDocument().addDocumentListener(new DocumentListener() {
                                public void changedUpdate(DocumentEvent evt) {
                                        textChanged();
                                }
                                
                                public void insertUpdate(DocumentEvent evt) {
                                        textChanged();
                                }
                                
                                public void removeUpdate(DocumentEvent evt) {
                                        textChanged();
                                }
                });
                
                field.setToolTipText(jEdit.getProperty("junit.type-test.tooltip"));
                return field;
        }
        //}}}
        
        //{{{ createBrowseButton method.
        private RolloverButton createBrowseButton() {
                RolloverButton button = createImageButton(OPEN_ICON,
                        jEdit.getProperty("junit.browse-tests.tooltip"));
                button.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        browseTestClasses();
                                }
                });
                
                return button;
        }
        //}}}
        
        //{{{ createRunButton method.
        private RolloverButton createRunButton() {
                RolloverButton button = createImageButton(RUN_ICON,
                        jEdit.getProperty("junit.run-tests.tooltip"));
                button.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        runner.runSuite();
                                }
                });
                
                return button;
        }
        //}}}
        
        //{{{ createNextButton method.
        private RolloverButton createNextButton() {
                RolloverButton button = createImageButton(NEXT_ICON,
                        jEdit.getProperty("junit.next-error.tooltip"));
                button.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        TestRunView view = 
                                        toggleViewsCheckBox.isSelected() 
                                        ? failureRunView : hierarchyRunView;
                                        view.nextFailure();
                                }
                });
                
                return button;
        }
        //}}}
      
        //{{{ createPrevButton method.
        private RolloverButton createPrevButton() {
                RolloverButton button = createImageButton(PREV_ICON,
                        jEdit.getProperty("junit.previous-error.tooltip"));
                button.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        TestRunView view = 
                                        toggleViewsCheckBox.isSelected() 
                                        ? failureRunView : hierarchyRunView;
                                        view.prevFailure();
                                }
                });
                
                return button;
        }
        //}}}
        
        //{{{ createCounterPanel method.
        private CounterPanel createCounterPanel() {
                return new CounterPanel();
        } //}}}
        
        //{{{ createFailureDetailView method.
        private DefaultFailureDetailView createFailureDetailView() {
        	//removed the ability to specify a custom FailureDetailView
                return new DefaultFailureDetailView();
        }
        //}}}
        
        //{{{ createToggleViewsCheckBox method.
        private JCheckBox createToggleViewsCheckBox() {
                final JCheckBox toggle = new JCheckBox("Failures Only");
                toggle.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                CardLayout l = (CardLayout)testRunViewsPanel.getLayout();
                                if (toggle.isSelected()) {
                                        l.last(testRunViewsPanel);
                                } else {
                                        l.first(testRunViewsPanel);
                                }
                        }
                });
                return toggle;
        } 
        //}}}
        
        //{{{ createTestRunViews method.
        private JPanel createTestRunViews() {
                JPanel panel = new JPanel(new CardLayout());
                testRunViews.addElement(
                        hierarchyRunView = new TestHierarchyRunView(runner));
                testRunViews.addElement(
                        failureRunView = new FailureRunView(runner));
                
                panel.add(hierarchyRunView.getComponent(), 
                        jEdit.getProperty("junit.test.hierarchy.label"));
                panel.add(failureRunView.getComponent(),
                        jEdit.getProperty("junit.test.failures.label"));
                return panel;
        } 
        //}}}
        
        //{{{ createSetClassPathButton method.
        private RolloverButton createSetClassPathButton() {
                RolloverButton button = createImageButton(PROPS_ICON,
                        jEdit.getProperty("junit.set-class-path.tooltip"));
                button.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                        configureClassPath();
                                }
                });
                
                return button;
        } 
        //}}}
        
        //{{{ createStopButton method.
        private RolloverButton createStopButton() {
                RolloverButton button = createImageButton(STOP_ICON,
                        jEdit.getProperty("junit.stop.tooltip"));
                button.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                        runner.runSuite();
                                }
                });
                
                button.setEnabled(false);
                return button;
        } //}}}
        
        //{{{ createStatusLine method.
        private StatusBar createStatusLine() {
                return new StatusBar(jEdit.getActiveView());
        } //}}}
        
        //{{{ textChanged method.
        private void textChanged() {
                clearStatus();
        } //}}}
        
        //{{{ getIconResource method.
        private Icon getIconResource(String name) {
                return TestRunner.getIconResource(getClass(), name);
        } 
        //}}}
        
        //{{{ createImageButton method.
        private RolloverButton createImageButton(Icon icon, String tooltip) {
                RolloverButton button = new RolloverButton(icon);
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setToolTipText(tooltip);
                return button;
        } 
        //}}}
        
        //{{{ startRow method.
        private void startRow(Component co, GridBagConstraints gbc,
                int w, int fill, double wx, int anchor) 
        {
                gbc.gridx = 0;
                gbc.gridy++;
                addGrid(co, gbc, w, fill, wx, anchor);
        } 
        //}}}
        
        //{{{ nextCol method.
        private void nextCol(Component co, GridBagConstraints gbc,
                int w, int fill, double wx, int anchor) 
        {
                gbc.gridx += gbc.gridwidth;
                addGrid(co, gbc, w, fill, wx, anchor);
        } //}}}
        
        //{{{ addGrid method.
        private void addGrid(Component co, GridBagConstraints gbc,
                int w, int fill, double wx, int anchor) 
        {
                gbc.gridwidth = w;
                gbc.anchor = anchor;
                gbc.weightx = wx;
                gbc.fill = fill;
                
                if (fill == GridBagConstraints.BOTH 
                        || fill == GridBagConstraints.VERTICAL)
                        gbc.weighty = 1.0;
                else
                        gbc.weighty = 0;
                        gbc.insets = new Insets(
                                gbc.gridy == 0 ? GAP : 0, 
                                gbc.gridx == 0 ? GAP : 0,
                                GAP, GAP);
                        add(co, gbc);
        } //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:            
}
