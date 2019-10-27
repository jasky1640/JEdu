/*
 * TestTreeModel.java 
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

import java.util.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.junit.runner.*;
import org.junit.runner.notification.Failure;

/**
 * A tree model for a Description.
 */
class TestTreeModel extends DefaultTreeModel {
        private Description fRoot;
        private Description currTest;
        private Vector fModelListeners = new Vector();
        private Hashtable fFailures = new Hashtable();
        private Hashtable fErrors = new Hashtable();
        private ArrayList fRunTests = new ArrayList();
        
        //{{{ constructor.
        /**
         * Constructs a tree model with the given test as its root.
         */
        public TestTreeModel(Description root) {
                super(null);
                fRoot = root; 
        } //}}}
        
        //{{{ addTreeModelListener method.
        /**
         * adds a TreeModelListener
         */
        public void addTreeModelListener(TreeModelListener l) {
                if (!fModelListeners.contains(l))
                        fModelListeners.addElement(l);
        } //}}}
        
        //{{{ removeTreeModelListener method.
        /**
         * Removes a TestModelListener
         */
        public void removeTreeModelListener(TreeModelListener l) {
                fModelListeners.removeElement(l);
        } //}}}
        
        //{{{ findTest method.
        /**
         * Finds the path to a test. Returns the index of the test in its parent test
         * suite.
         */
        public int findTest(Description target, Description node, Vector path) {
                if (target.equals(node))
                        return 0;
                List<Description> l = node.getChildren();
                for (int i = 0; i < l.size(); i++) {
                        Description t = l.get(i);
                        int index = findTest(target, t, path);
                        if (index >= 0) {
                                path.insertElementAt(node, 0);
                                if (path.size() == 1)
                                        return i;
                                return index;
                        }
                }
                return -1;
        } 
        //}}}
        
        //{{{ fireNodeChanged method.
        /**
         * Fires a node changed event
         */
        public void fireNodeChanged(TreePath path, int index) {
                int[] indices = { index };
                Object[] changedChildren = { getChild(path.getLastPathComponent(), index) };
                TreeModelEvent event = new TreeModelEvent(this, path, indices,
                        changedChildren);
                Enumeration e = fModelListeners.elements();
                while (e.hasMoreElements()) {
                        TreeModelListener l = (TreeModelListener) e.nextElement();
                        l.treeNodesChanged(event);
                }
        } 
        //}}}
        
        //{{{ getChild method.
        /**
         * Gets the test at the given index
         */
        public Object getChild(Object parent, int index) {
                java.util.List<Description> children = ((Description)parent).getChildren();
                if (children != null)
                        return children.get(index);
                return null;
        } //}}}
        
        //{{{ getChildCount method.
        /**
         * Gets the number of tests.
         */
        public int getChildCount(Object parent) {
                java.util.List<Description> children = ((Description)parent).getChildren();
                if (children != null)
                        return children.size();
                return 0;
        } 
        //}}}
        
        //{{{ getRunTests method.
        /**
         * Gets the runned tests.
         */
        public ArrayList getRunTests() {
                return fRunTests;
        } //}}}
        
        //{{{ getIndexOfChild method.
        /**
         * Gets the index of a test in a test suite
         */
        public int getIndexOfChild(Object parent, Object child) {
                java.util.List<Description> children = ((Description)parent).getChildren();
                if (children != null)
                     return children.indexOf(child);
                return -1;
        } 
        //}}}
        
        //{{{ getRoot method.
        /**
         * Returns the root of the tree
         */
        public Object getRoot() {
                return fRoot;
        } //}}}
        
        //{{{ isLeaf method.
        /**
         * Tests if the test is a leaf.
         */
        public boolean isLeaf(Object node) {
                java.util.List<Description> children = ((Description)node).getChildren();
                if (children != null)return children.size()==0;
                return true;
        } //}}}
        
        // {{{ valueForPathChanged method.
        /**
         * Called when the value of the model object was changed in the view
         */
        public void valueForPathChanged(TreePath path, Object newValue) {
                // we don't support direct editing of the model
                System.out.println("TreeModel.valueForPathChanged: not implemented");
        } 
        // }}}
        
        //{{{ addFailure method.
        /**
         * Remembers a test failure
         */
        void addFailure(Description t) {
                fFailures.put(t, t);
        } 
        //}}}
        
        //{{{ addError method.
        /**
         * Remembers a test error
         */
        void addError(Description t) {
                fErrors.put(t, t);
        } //}}}
        
        //{{{ delFailure method.
        void delFailure(Description t) {
                fFailures.remove(t);
        } //}}}
        
        //{{{ delError method.
        void delError(Description t) {
                fErrors.remove(t);
        } //}}}
        
        //{{{ addRunTest method.
        /**
         * Remembers that a test was run.
         */
        void addRunTest(Description t) {
                fRunTests.add(t);
        } //}}}
        
        //{{{ wasRun method.
        /**
         * Returns whether a test was run.
         */
        boolean wasRun(Description t) {
                return fRunTests.contains(t);
        } //}}}
        
        //{{{ isError method.
        /**
         * Tests whether a test was an error
         */
        boolean isError(Description t) {
                return (fErrors != null) && fErrors.get(t) != null;
        } //}}}
        
        //{{{ isFailure method.
        /**
         * Tests whether a test was a failure
         */
        boolean isFailure(Description t) {
                return (fFailures != null) && fFailures.get(t) != null;
        } 
        //}}}
        
        //{{{ isRinning method.
        boolean isRinning(Description t) {
                return t.equals(currTest);
        } //}}}
        
        //{{{ hasFailures method.
        /**
         * Tests whether a suite has failures or errors.
         */
        boolean hasFailures(Object parent) {
                java.util.List<Description> children = ((Description)parent).getChildren();
                if (children != null){
                        for(int i = 0; i < children.size() ; i++) {
                                Description test = children.get(i);
                                if (isError(test) || isFailure(test)) 
                                        return true;
                        }
                }
                
                return false;
        } 
        //}}}
        
        //{{{ resetResults method.
        /**
         * Resets the test results
         */
        void resetResults() {
                fFailures = new Hashtable();
                fRunTests = new ArrayList(); 
                fErrors = new Hashtable();
        } 
        //}}}
        
        //{{{ setCurrentTest method.
        /**
         * Sets the current test.
         */
        void setCurrentTest(Description t) {
                currTest = t;
        } 
        //}}}

        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
