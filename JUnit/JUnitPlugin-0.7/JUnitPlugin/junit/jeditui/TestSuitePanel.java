/*
 * DefaultFailureDetailView.java 
 * Copyright (c) 2001 - 2003 Andre Kaplan, Calvin Yu
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

import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.*;

import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.RunListener;
import junit.jeditui.DetailedResult;

import org.gjt.sp.jedit.gui.AnimatedIcon;
import org.gjt.sp.jedit.GUIUtilities;

import junit.JUnitPlugin;

/** 
 * A Panel showing a test suite as a tree.
 */
class TestSuitePanel extends JPanel{
        private final static int EXTRA_INDEX = 2;
        private JTree fTree;
        private JScrollPane fScrollTree;
        private TestTreeModel fModel;
        private RunListener listener;
        
        //{{{ constructor..
        public TestSuitePanel() {
                super(new BorderLayout());
                setPreferredSize(new Dimension(300, 100));
                fTree = new JTree();
                fTree.setModel(null);
                fTree.setRowHeight(20);
                ToolTipManager.sharedInstance().registerComponent(fTree);
                fTree.putClientProperty("JTree.lineStyle", "Angled");
                fScrollTree = new JScrollPane(fTree);
                add(fScrollTree, BorderLayout.CENTER);
                listener = new RunListenerImpl();
        } 
        //}}}
        
        public RunListener getListener(){
        	return listener;
        }
        
        private class RunListenerImpl extends RunListener {
        	
		//{{{ testFailure method.
		@Override
		public void testFailure(Failure f) {
			Description test = f.getDescription();
			if(JUnitPlugin.isFailure(f)){
				fModel.addFailure(test);
			}else{
				fModel.addError(test);
			}
			fireTestChanged(test, true);
		} 
		//}}}
		
		//{{{ testAssumptionFailure method.
		@Override
		public void testAssumptionFailure(Failure f){
				Description test = f.getDescription();
				fModel.addError(test);
				fireTestChanged(test, true);
		}
		//}}}
		
		//{{{ testFinished method.
		/**
		 * A test ended.
		 */
		@Override
		public void testFinished(Description test) {
			fModel.addRunTest(test);
			fModel.setCurrentTest(null);
			fireTestChanged(test, false);
		} 
		//}}}
		
		//{{{ startTest method.
		/**
		 * A test started.
		 */
		@Override
		public void testStarted(Description test) {
			if (test == null) return;
			fModel.setCurrentTest(test);
			fireTestChanged(test, true);
		} 
		//}}}
        }
        
        //{{{ getSelectedTest method.
        /**
         * Returns the selected test or null if multiple or none is selected
         */
        public Description getSelectedTest() {
                TreePath[] paths = fTree.getSelectionPaths();
                if (paths != null && paths.length == 1)
                        return (Description) paths[0].getLastPathComponent();
                return null;
        } 
        //}}}
        
        //{{{ getTree method.
        /**
         * Returns the Tree
         */
        public JTree getTree() {
                return fTree;
        } 
        //}}}
        
        //{{{ nextFailure method.
        public void nextFailure(boolean next) {
                int index = -1; 
                boolean flag = false;
                Description nextTest = null;
                Description selectedTest = getSelectedTest();
                
                ArrayList tests = fModel.getRunTests(); 
                if (selectedTest != null) {
                        index = tests.indexOf(selectedTest);
                }
                
                if (next) {
                        for(int i = 0; i < tests.size(); i++) {
                                Description t = (Description)tests.get(i);
                                if (fModel.isError(t) || fModel.isFailure(t)) {
                                        if (index < tests.indexOf(t)) {
                                                nextTest = t;
                                                break;
                                        }
                                }
                                
                        }
                } else {
                        for(int i = tests.size(); 0 < i ; i--) {
                                Description t = (Description)tests.get(i - 1);
                                if (fModel.isError(t) || fModel.isFailure(t)) {
                                        if (tests.indexOf(t) < index) {
                                                nextTest = t;
                                                break;
                                        }
                                }
                                
                        }
                }
                if (nextTest !=null) {
                        findTestForExpand(nextTest);
                }
        } 
        //}}}
        
        //{{{ refresh method.
        public void refresh(Description test, RunNotifier runNotifier, DetailedResult result) {
                Description t = getSelectedTest();
                if(t == null)return;
                if (result.wasSuccessful()) {
                        fModel.delFailure(t);
                        fModel.delError(t);
                } else if (result.getFailureCount() == 1) {
                        fModel.addFailure(t);
                } else {
                        fModel.addError(t);
                }
                TreePath path = fTree.getSelectionPath();
                path = path.getParentPath();
                int index = fModel.findTest(t, (Description)fModel.getRoot(), new Vector());
                fModel.fireNodeChanged(path,  index);
        } //}}}
        
        //{{{ showTestTree method.
        /**
         * Shows the test hierarchy starting at the given test
         */
        public void showTestTree(Description root) {
                fModel = new TestTreeModel(root);
                fTree.setModel(fModel);
                fTree.setCellRenderer(new TestTreeCellRenderer());
        } 
        //}}}
        
        //{{{ fireTestChanged method.
        private void fireTestChanged(final Description test, final boolean expand) {
                SwingUtilities.invokeLater(
                        new Runnable() {
                                public void run() {
                                        Vector vpath = new Vector();
                                        int index = fModel.findTest(test, (Description) fModel.getRoot(), vpath);
                                        if (index >= 0) {
                                                Object[] path;
                                                // when the root node has changed
                                                if(vpath.isEmpty()){
                                                	path = new Object[]{test};
                                                }else{
                                                	path = new Object[vpath.size()];
                                                	vpath.copyInto(path);
                                                }
                                                TreePath treePath = new TreePath(path);
                                                fModel.fireNodeChanged(treePath, index);
                                                
                                                if (expand) {
                                                        expandPath(treePath, index, false);
                                                } else {
                                                        Object parent = treePath.getLastPathComponent();
                                                        int count = fModel.getChildCount(parent);
                                                        
                                                        if (index == (count - 1) 
                                                                && !fModel.hasFailures(parent)) 
                                                        {
                                                                fTree.collapsePath(treePath);
                                                        }
                                                }
                                        }
                                }
                        });
        } 
        //}}}
        
        //{{{ findTestForExpand method.
        private void findTestForExpand(Description nextTest) {
                Vector vpath = new Vector();
                int index = fModel.findTest(nextTest, (Description)fModel.getRoot(), vpath);
                Object[] path = new Object[vpath.size()];
                vpath.copyInto(path);
                expandPath(new TreePath(path), index, true);
        } 
        //}}}
        
        //{{{ expandPath method.
        private void expandPath(TreePath treePath, int index, boolean select) {
                TreePath fullTreePath = treePath.pathByAddingChild(fModel.getChild(
                        treePath.getLastPathComponent(), index));
                fTree.scrollPathToVisible(fullTreePath);
                if (select) {
                        fTree.setSelectionPath(fullTreePath);
                }
        }
        //}}}
        
        //{{{ TestTreeCellRenderer class.
        private static class TestTreeCellRenderer extends DefaultTreeCellRenderer {
                private final Icon CLASS_ICON = TestRunner.getIconResource(getClass(),
                        "icons/Class.gif");
                private final Icon METHOD_ICON = TestRunner.getIconResource(getClass(),
                        "icons/Method.gif");
                
                private final Icon ERROR_ICON = TestRunner.getIconResource(getClass(), 
                        "icons/error.gif");
                private final Icon OK_ICON = TestRunner.getIconResource(getClass(), 
                        "icons/ok.gif");
                private final Icon FAILURE_ICON = TestRunner.getIconResource(getClass(), 
                        "icons/failure.gif");
                
                private final Icon RIN_ICON = TestRunner.getIconResource(getClass(), 
                        "icons/runjunit.gif");
                
                private final Font LEAF_FONT = UIManager.getFont("Tree.font");
                private final Font SUITE_FONT = LEAF_FONT.deriveFont(Font.BOLD);
                
                
                TestTreeCellRenderer() {
                        super();
                }
                
                
                String stripParenthesis(Object o) {
                        String text = o.toString();
                        int pos = text.indexOf('('); // )
                        if (pos < 1)
                                return text;
                        return text.substring(0, pos);
                }
                
                public Component getTreeCellRendererComponent(JTree tree, Object value,
                        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) 
                {
                        Component c = super.getTreeCellRendererComponent(tree, value, sel,
                                expanded, leaf, row, hasFocus);
                        TreeModel model = tree.getModel();
                        if (model instanceof TestTreeModel) {
                                TestTreeModel testModel = (TestTreeModel) model;
                                Description t = (Description) value;
                                String s = "";
                                if (!testModel.isLeaf(t)) {
                                        setIcon(CLASS_ICON);
                                } else {
                                        setIcon(METHOD_ICON);
                                }
                                
                                if (testModel.isRinning(t)) {
                                        setIcon(RIN_ICON);
                                } else if (testModel.isFailure(t)) {
                                        setIcon(FAILURE_ICON);
                                        s = " - Failed";
                                } else if (testModel.isError(t)) {
                                        setIcon(ERROR_ICON);
                                        s = " - Error";
                                } else if (testModel.wasRun(t)) {
                                        setIcon(OK_ICON);
                                        s = " - Passed";
                                }
                                
                                if (c instanceof JComponent)
                                        ((JComponent) c).setToolTipText(getText() + s);
                        }
                        setText(stripParenthesis(value));
                        setFont(leaf ? LEAF_FONT : SUITE_FONT);
                        return c;
                }
        } 
        //}}}

        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}

