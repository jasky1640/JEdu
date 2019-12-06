/*
* PluginOptionsPane.java 
* Copyright (c) Fri Jul 21 01:01:54 MSD 2006 Denis Koryavov
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

package junit.jeditui.options;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import junit.JUnitPlugin;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.gui.JCheckBoxList;
import org.gjt.sp.jedit.gui.JCheckBoxList.Entry;

public class PluginOptionsPane extends JUnitOptionPane {
        //{{{ private variables.
        private static final int GAP = 0;
        
        private JLabel label = new JLabel(jEdit.getProperty("options.junit.filters.label"));
        
        private JCheckBoxList table;
        private DefaultListModel model = new DefaultListModel();
        
        private JPanel btnsPanel = new JPanel(new GridBagLayout());
        private JButton btnAddFilter = new JButton("Add Filter...");
        private JButton btnEditFilter = new JButton("Edit Filter...");
        private JButton btnRemove = new JButton("Remove");
        private JButton btnEnableAll = new JButton("Enable All");
        private JButton btnDisableAll = new JButton("Disable All");
        private JButton btnRestore = new JButton("Restore defaults");
        
        //}}}
        
        //{{{ constructor.
        public PluginOptionsPane(String title) {
                super(title);
        } 
        //}}}
        
        //{{{ _init method.
        public void _init() {
                super._init();
               
               table = new JCheckBoxList(getFilters(false));
               table.setShowGrid(false);
               table.setIntercellSpacing(new Dimension(0,0));
               table.setRowHeight(table.getRowHeight() + 2);
               table.setPreferredScrollableViewportSize(new Dimension(500,200));
               table.setRequestFocusEnabled(false);
               table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = -1;
                // comp gbc w fill wx anchor
                startRow(btnAddFilter, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
                        GridBagConstraints.CENTER);
                startRow(btnEditFilter, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
                        GridBagConstraints.CENTER);
                startRow(btnRemove, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
                        GridBagConstraints.CENTER);
                startRow(Box.createHorizontalStrut(15), gbc, 1, 
                        GridBagConstraints.HORIZONTAL, 0,
                        GridBagConstraints.CENTER);
                
                startRow(btnEnableAll, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
                        GridBagConstraints.CENTER);
                startRow(btnDisableAll, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
                        GridBagConstraints.CENTER);
                startRow(Box.createHorizontalStrut(15), gbc, 1, 
                        GridBagConstraints.HORIZONTAL, 0,
                        GridBagConstraints.CENTER);
                
                startRow(btnRestore, gbc, 1, GridBagConstraints.HORIZONTAL, 0,
                        GridBagConstraints.CENTER);
                startRow(Box.createGlue(), gbc, 1, GridBagConstraints.BOTH, 1,
                        GridBagConstraints.CENTER);
                
                btnAddFilter.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        String f = showInputDialog(
                                                "Adding a New Filter", "Filter:", 
                                                null);
                                        addFilter(f);
                                }
                });
                
                btnEditFilter.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        String val = (String)table.getSelectedValue();
                                        int index = table.getSelectedRow();
                                        if (val != null) {
                                        String f = showInputDialog("", 
                                                        "Edit Filter:", val);
                                        editFilter(val, f, index);
                                        }
                                }
                });
                
                btnRemove.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        removeFilter(table.getSelectedRow());
                                }
                });
                
                btnEnableAll.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        toggleFilters(true);
                                }
                });
                
                btnDisableAll.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        toggleFilters(false);
                                }
                });
                
                btnRestore.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                        table.setModel(getFilters(true));
                                }
                });
                
                
                add(label, BorderLayout.NORTH);
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.getViewport().setBackground(table.getBackground());
                add(scrollPane, BorderLayout.CENTER);
                add(btnsPanel, BorderLayout.EAST);
        } 
        //}}}
        
        //{{{ _save method.
        protected void _save() {
                Entry[] values = table.getValues();
                Properties properties = new Properties();
                for (int i = 0; i < values.length; i++) {
                        Entry e = values[i];
                        properties.setProperty((String)e.getValue(), 
                                Boolean.toString(e.isChecked()));
                }
                JUnitPlugin.storeFilters(properties);
        } 
        //}}}
        
        //{{{ addFilter method.
        // Adds a new filter to the table.
        private void addFilter(String filter) {
                if (filter == null || filter.equals("")) return;
                
                Entry[] values = table.getValues();
                Entry[] newValues = new Entry[values.length + 1];
                newValues[0] = new Entry(true, filter);
                for (int i = 1; i < newValues.length; i++) {
                        newValues[i] = values[i-1];
                }
                table.setModel(newValues);
        } 
        //}}}
        
        //{{{ editFilter method.
        private void editFilter(String oldValue, String newValue, int index) {
                if (oldValue == null || newValue == null) return;
                
                Entry[] values = table.getValues();
                Entry[] newValues = new Entry[values.length];
                for (int i = 0; i < newValues.length; i++) {
                        Entry oldEntry = values[i];
                        if (oldValue.equals(oldEntry.getValue()) && index == i) 
                                newValues[i] = new Entry(
                                        oldEntry.isChecked(), newValue);
                        else 
                                newValues[i] = values[i];
                }
                table.setModel(newValues);
        } 
        //}}}
        
        //{{{ removeFilter method.
        // Removes filter from the table.
        private void removeFilter(int index) {
                if (index == -1) return;
                
                Entry[] values = table.getValues();
                ArrayList newValues = new ArrayList();
                for (int i = 0; i < values.length; i++) {
                        if (index != i) newValues.add(values[i]);
                }
                table.setModel(newValues.toArray());
        } 
        //}}}
        
        //{{{ toggleFilters method.
        private void toggleFilters(boolean enable) {
                if (enable) {
                        table.selectAll();
                } else {
                        Entry[] values = table.getValues();
                        for(int i = 0; i < values.length; i++) {
                                Entry newEntry = new Entry(false, values[i].getValue());
                                values[i] = newEntry;
                        }
                        table.setModel(values);
                }
        } 
        //}}}
        
        //{{{ showInputDialog method.
        private String showInputDialog(String title, String message, 
                Object initialValue) 
        {
                String result = "";
                if (initialValue == null) {
                        result = JOptionPane.showInputDialog(
                                jEdit.getActiveView(), 
                                message, title, 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        
                } else {
                        result = JOptionPane.showInputDialog(
                                jEdit.getActiveView(),
                                message,
                                initialValue
                                );
                }
                return result;
        } 
        //}}}
        
        //{{{ getFillters method.
        private Object[] getFilters(boolean defaults) {
                Properties pFilters = JUnitPlugin.getFilters(defaults);
                Object[] fObjects = new Object[pFilters.size()];
                Enumeration enumeration = pFilters.propertyNames();
                
                int index = 0;
                while(enumeration.hasMoreElements()) {
                        String key = (String)enumeration.nextElement();
                        String value = pFilters.getProperty(key);
                        Entry entry = new Entry(Boolean.valueOf(value), key);
                        fObjects[index] = entry;
                        index++; 
                }
                
                return fObjects;
        } 
        //}}}
        
        // {{{ startRow method.
        private void startRow(Component co, GridBagConstraints gbc,
                int w, int fill, double wx, int anchor) 
        {
                gbc.gridx = 0;
                gbc.gridy++;
                addGrid(co, gbc, w, fill, wx, anchor);
        } 
        // }}}
        
        // {{{ addGrid method.
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
                // t, l, b, r;
                gbc.insets = new Insets(0, 0, 5, 0);
                btnsPanel.add(co, gbc);
        } 
        //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}

