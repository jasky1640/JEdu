/*
 * TestHierarchyRunView.java
 * Copyright (c) 2002 Calvin Yu
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

import java.awt.Component;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import java.util.Vector;

import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
* A hierarchical view of a test run. The contents of a test suite is shown as a
* tree.
*/
class TestHierarchyRunView implements TestRunView {
        TestSuitePanel fTreeBrowser;
        TestRunContext fTestContext;
        
        //{{{ constructor.
        public TestHierarchyRunView(TestRunContext context) {
                fTestContext = context;
                fTreeBrowser = new TestSuitePanel();
                fTreeBrowser.setName("junit.test.hierarchy");
                
                fTreeBrowser.getTree()
                .addMouseListener(new TestRunViewHandler(this, fTestContext));
                fTreeBrowser.getTree().addTreeSelectionListener(
                        new TreeSelectionListener() {
                                public void valueChanged(TreeSelectionEvent e) {
                                        testSelected();
                                }
                        });
        } 
        //}}}
        
        //{{{ getComponent method.
        @Override
        public Component getComponent() {
                return fTreeBrowser;
        } //}}}
        
        //{{{ getSelectedTest method.
        @Override
        public Description getSelectedTest() {
                return fTreeBrowser.getSelectedTest();
        } //}}}
        
        //{{{ activate method.
        @Override
        public void activate() {
                testSelected();
        } //}}}
        
        //{{{ refresh method.
        @Override
        public void refresh(Description test, RunNotifier rn, DetailedResult result) {
                fTreeBrowser.refresh(test, rn, result);
                testSelected();
        } //}}}
        
        //{{{ nextFailure method.
        @Override
        public void nextFailure() {
                fTreeBrowser.nextFailure(true);
        } //}}}
        
        //{{{ prevFailure method.
        @Override
        public void prevFailure() {
                fTreeBrowser.nextFailure(false);
        } //}}}
        
        //{{{ revealFailure method.
        @Override
        public void revealFailure(Failure failure) {
                JTree tree = fTreeBrowser.getTree();
                TestTreeModel model = (TestTreeModel) tree.getModel();
                Vector vpath = new Vector();
                int index = model.findTest(failure.getDescription(), (Description) model.getRoot(), vpath);
                
                if (index >= 0) {
                	Object[] path;
                	if(vpath.isEmpty()){
                		path = new Object[]{failure.getDescription()};
                	}else{
				path = new Object[vpath.size() + 1];
				vpath.copyInto(path);
				Object last = path[vpath.size() - 1];
				path[vpath.size()] = model.getChild(last, index);
			}
                        TreePath selectionPath = new TreePath(path);
                        tree.setSelectionPath(selectionPath);
                        tree.makeVisible(selectionPath);
                }
        } 
        //}}}
        
        //{{{ aboutToStart method.
        @Override
        public void aboutToStart(Description suite, RunNotifier rn, DetailedResult result) {
                fTreeBrowser.showTestTree(suite);
                rn.addListener(fTreeBrowser.getListener());
        } //}}}
        
        //{{{ runFinished method.
        @Override
        public void runFinished(Description suite, RunNotifier rn, DetailedResult result) {
        	rn.removeListener(fTreeBrowser.getListener());
        } //}}}
        
        //{{{ testSelected method.
        protected void testSelected() {
                fTestContext.handleTestSelected(getSelectedTest());
        } //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
