/*
 * FailureRunView.java 
 * Copyright (c) Tue Aug 01 23:38:52 MSD 2006 Denis Koryavov
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
import javax.swing.border.*;
import javax.swing.event.*;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import junit.JUnitPlugin;

/**
* A view presenting the test failures as a list.
*/
class FailureRunView implements TestRunView {
        JList fFailureList;
        TestRunContext fRunContext;
        
        //{{{ constructor.
        public FailureRunView(TestRunContext context) {
                fRunContext = context;
                fFailureList = new JList(fRunContext.getFailures());
                fFailureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                fFailureList.setCellRenderer(new FailureListCellRenderer());
                fFailureList.setToolTipText("Failure - grey X; Error - red X");
                fFailureList.setVisibleRowCount(5);
                fFailureList.addMouseListener(new TestRunViewHandler(this, context));
                fFailureList.addListSelectionListener(
                        new ListSelectionListener() {
                                public void valueChanged(ListSelectionEvent e) {
                                        testSelected();
                                }
                        });
        } 
        //}}}
        
        //{{{ getSelectedTest method.
        @Override
        public Description getSelectedTest() {
                int index = fFailureList.getSelectedIndex();
                if (index == -1)
                        return null;
                ListModel model = fFailureList.getModel();
                Failure failure = (Failure) model.getElementAt(index);
                return failure.getDescription();
        } 
        //}}}
        
        //{{{ activate method.
        @Override
        public void activate() {
                testSelected();
        } 
        //}}}
        
        //{{{ refresh method.
        @Override
        public void refresh(Description test, RunNotifier rn, DetailedResult result) {} //}}}
        
        //{{{ getComponent method.
        public Component getComponent() {
                JScrollPane scroll = new JScrollPane(fFailureList,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scroll.setName("junit.test.failures");
                return scroll;
        } 
        //}}}
        
        //{{{ revealFailure method.
        @Override
        public void revealFailure(Failure failure) {
                fFailureList.setSelectedIndex(0);
        } 
        //}}}
        
        //{{{ aboutToStart and runFinished methods.
        @Override
        public void aboutToStart(Description suite, RunNotifier rn, DetailedResult result) {}
        
        @Override
        public void runFinished(Description suite, RunNotifier rn, DetailedResult result) {} 
        //}}}
        
        //{{{ testSelected method.
        protected void testSelected() {
                fRunContext.handleTestSelected(getSelectedTest());
        } 
        //}}}
        
        //{{{ nextFailure method.
        @Override
        public void nextFailure() {
                int index = fFailureList.getSelectedIndex();
                int nextIndex = (index == -1) ? 0 : index + 1;
                int size = fFailureList.getModel().getSize();
                if (size > 0 && nextIndex < size) {
                        fFailureList.setSelectedValue(
                                fFailureList.getModel().getElementAt(nextIndex), 
                                true);
                }
        } 
        //}}}
        
        //{{{ prevFailure method.
        @Override
        public void prevFailure() {
                int index = fFailureList.getSelectedIndex();
                int nextIndex = (index == -1) ? 0 : index - 1;
                int size = fFailureList.getModel().getSize();
                if (0 <= nextIndex && size > 0) {
                        fFailureList.setSelectedValue(
                                fFailureList.getModel().getElementAt(nextIndex), 
                                true);
                }
        } 
        //}}}
        
        //{{{ FailureListCellRenderer class.
        /**
         * Renders TestFailures in a JList
         */
         private static class FailureListCellRenderer extends JLabel 
         implements ListCellRenderer 
         {
                 private final Icon ERROR_ICON = TestRunner.getIconResource(
                         getClass(), "icons/error.gif");
                 private final Icon FAILURE_ICON = TestRunner.getIconResource(
                         getClass(), "icons/failure.gif");
                 private Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
                 
                 
                 FailureListCellRenderer() {
                         super();
                         this.setOpaque(true);
                 }
                 
                 public Component getListCellRendererComponent(
                         JList list, Object value, int modelIndex,
                         boolean isSelected, boolean cellHasFocus) 
                 {
                         
                         if (isSelected) {
                                 setBackground(list.getSelectionBackground());
                                 setForeground(list.getSelectionForeground());
                         } else {
                                 setBackground(list.getBackground());
                                 setForeground(list.getForeground());
                         }
                         
                         Failure failure = (Failure) value;
                         String text = failure.getTestHeader();
                         String msg = failure.getException().getMessage();
                         
                         if (msg != null)
                                 text += ":" + msg;
                         
                                 if (JUnitPlugin.isFailure(failure)) 
                                 {
                                         setIcon(FAILURE_ICON);
                                 } else {
                                         setIcon(ERROR_ICON);
                                 }
                                 
                                 setText(text);
                                 setToolTipText(text);
                                 return this;
                 }
                 
         }
        //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
