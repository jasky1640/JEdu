/*
 * TestSelector.java
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.junit.runner.*;

/**
* A test class selector. A simple dialog to pick the name of a test suite.
*/
class TestSelector extends JDialog {
        private JButton fCancel;
        private JButton fOk;
        private JList fList;
        private JScrollPane fScrolledList;
        private JLabel fDescription;
        private String fSelectedItem;
        
        //{{{ constructor.
        public TestSelector(Frame parent, Description testCollector) {
                super(parent, true);
                setSize(500, 400);
                setLocationRelativeTo(parent);
                setTitle("Test Selector");
                Vector list = null;
                
                try {
                        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        list = createTestList(testCollector);
                } finally {
                        parent.setCursor(Cursor.getDefaultCursor());
                }
                
                fList = new JList(list);
                fList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                fList.setCellRenderer(new TestCellRenderer());
                fScrolledList = new JScrollPane(fList);
                fCancel = new JButton("Close");
                fDescription = new JLabel("Select the Test class:");
                fOk = new JButton("Select");
                fOk.setEnabled(false);
                getRootPane().setDefaultButton(fOk);
                defineLayout();
                addListeners();
        } 
        //}}}
        
        //{{{ addListeners method.
        private void addListeners() {
                fCancel.addActionListener(
                        new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        dispose();
                                }
                        });
                
                fOk.addActionListener(
                        new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        okSelected();
                                }
                        });
                
                fList.addMouseListener(new MouseAdapter() {
                                public void mouseClicked(MouseEvent e) {
                                        if (e.getClickCount() == 2) {
                                                okSelected();
                                        }
                                }
                } );
                fList.addKeyListener(new KeyAdapter() {
                                public void keyTyped(KeyEvent e) {
                                        keySelectTestClass(e.getKeyChar());
                                }
                });
                fList.addListSelectionListener(
                        new ListSelectionListener() {
                                public void valueChanged(ListSelectionEvent e) {
                                        checkEnableOK(e);
                                }
                        });
                
                addWindowListener(
                        new WindowAdapter() {
                                public void windowClosing(WindowEvent e) {
                                        dispose();
                                }
                        });
        } 
        //}}}
        
        //{{{ defineLayout method.
        private void defineLayout() {
                getContentPane().setLayout(new GridBagLayout());
                GridBagConstraints labelConstraints = new GridBagConstraints();
                labelConstraints.gridx = 0;
                labelConstraints.gridy = 0;
                labelConstraints.gridwidth = 1;
                labelConstraints.gridheight = 1;
                labelConstraints.fill = GridBagConstraints.BOTH;
                labelConstraints.anchor = GridBagConstraints.WEST;
                labelConstraints.weightx = 1.0;
                labelConstraints.weighty = 0.0;
                labelConstraints.insets = new Insets(8, 8, 0, 8);
                getContentPane().add(fDescription, labelConstraints);
                
                GridBagConstraints listConstraints = new GridBagConstraints();
                listConstraints.gridx = 0;
                listConstraints.gridy = 1;
                listConstraints.gridwidth = 4;
                listConstraints.gridheight = 1;
                listConstraints.fill = GridBagConstraints.BOTH;
                listConstraints.anchor = GridBagConstraints.CENTER;
                listConstraints.weightx = 1.0;
                listConstraints.weighty = 1.0;
                listConstraints.insets = new Insets(8, 8, 8, 8);
                getContentPane().add(fScrolledList, listConstraints);
                
                GridBagConstraints okConstraints = new GridBagConstraints();
                okConstraints.gridx = 2;
                okConstraints.gridy = 2;
                okConstraints.gridwidth = 1;
                okConstraints.gridheight = 1;
                okConstraints.anchor = java.awt.GridBagConstraints.EAST;
                okConstraints.insets = new Insets(0, 8, 8, 8);
                
                getContentPane().add(fOk, okConstraints);
                GridBagConstraints cancelConstraints = new GridBagConstraints();
                cancelConstraints.gridx = 3;
                cancelConstraints.gridy = 2;
                cancelConstraints.gridwidth = 1;
                cancelConstraints.gridheight = 1;
                cancelConstraints.anchor = java.awt.GridBagConstraints.EAST;
                cancelConstraints.insets = new Insets(0, 8, 8, 8);
                getContentPane().add(fCancel, cancelConstraints);
        } //}}}
        
        //{{{ checkEnableOK method.
        public void checkEnableOK(ListSelectionEvent e) {
                fOk.setEnabled(fList.getSelectedIndex() != -1);
        } //}}}
        
        //{{{ okSelected method.
        public void okSelected() {
                fSelectedItem = (String) fList.getSelectedValue();
                dispose();
        } //}}}
        
        //{{{ isEmpty method.
        public boolean isEmpty() {
                return fList.getModel().getSize() == 0;
        } //}}}
        
        //{{{ keySelectTestClass method.
        public void keySelectTestClass(char ch) {
                ListModel model = fList.getModel();
                if (!Character.isJavaIdentifierStart(ch)) return;
                for (int i = 0; i < model.getSize(); i++) {
                        String s = (String) model.getElementAt(i);
                        if (TestCellRenderer.matchesKey(s, Character.toUpperCase(ch))) {
                                fList.setSelectedIndex(i);
                                fList.ensureIndexIsVisible(i);
                                return;
                        }
                }
                Toolkit.getDefaultToolkit().beep();
        } 
        //}}}
        
        //{{{ getSelectedItem method.
        public String getSelectedItem() {
                return fSelectedItem;
        } //}}}
        
        //{{{ createTestList method.
        private Vector createTestList(Description collector) {
        	if(collector==null)return new Vector(0);
                java.util.List<Description> l = collector.getChildren();
                Vector v = new Vector(l.size());
                Vector displayVector = new Vector(v.size());
                for (Description cd: l) {
                        String s = cd.getClassName();
                        v.addElement(s);
                        displayVector.addElement(TestCellRenderer.displayString(s));
                }
                // TODO: tests are not sorted...
                /*if (v.size() > 0)
                Sorter.sortStrings(displayVector, 0, displayVector.size() - 1,
                        new ParallelSwapper(v));*/
                return v;
        } 
        //}}}
        
        //{{{ TestCellRenderer class.
        /**
         * Renders TestFailures in a JList
         */
        private static class TestCellRenderer extends DefaultListCellRenderer {
                final Icon ALL_TESTS_ICON = TestRunner.getIconResource(getClass(), 
                        "icons/AllTests.gif");
                final Icon LEAF_ICON = TestRunner.getIconResource(getClass(), 
                        "icons/Method.gif");
                
                public Component getListCellRendererComponent(
                        JList list, Object value, int modelIndex,
                        boolean isSelected, boolean cellHasFocus) 
                {
                        Component c = super.getListCellRendererComponent(list, value, modelIndex,
                                isSelected, cellHasFocus);
                        String displayString = displayString((String) value);
                        if (displayString.startsWith("AllTests"))
                                setIcon(ALL_TESTS_ICON);
                        else
                                setIcon(LEAF_ICON);
                        setText(displayString);
                        return c;
                }
                
                public static String displayString(String className) {
                        int typeIndex = className.lastIndexOf('.');
                        if (typeIndex < 0)
                                return className;
                        return className.substring(typeIndex + 1) + " - "
                        + className.substring(0, typeIndex);
                }
                
                public static boolean matchesKey(String s, char ch) {
                        return ch == Character.toUpperCase(s.charAt(typeIndex(s)));
                }
                
                private static int typeIndex(String s) {
                        int typeIndex = s.lastIndexOf('.');
                        int i = 0;
                        if (typeIndex > 0)
                                i = typeIndex + 1;
                        return i;
                }
        } 
        //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:

}
