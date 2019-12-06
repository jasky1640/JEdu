/*
 * CounterPanel.java
 * Copyright (c) 2001, 2002 Andre Kaplan
 * Copyright (c) 2006 Denis Koryavov
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

import java.awt.FlowLayout;
import java.awt.Font;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.jEdit;

/**
 * A panel with test run counters.
 * Doesn't show ignored tests for now.
 */
 public class CounterPanel extends JPanel {
         public static final Font PLAIN_FONT = new Font("dialog", Font.PLAIN, 12);
         public static final Font BOLD_FONT = new Font("dialog", Font.BOLD, 12);
         static final private Map icons = new HashMap(3);
         private JLabel numErrors;
         private JLabel numFailures;
         private JLabel numRuns;
         private JLabel numAssumptions;
         private int fTotal;
         
         //{{{ constructor.
         /**
         * Create a new <code>CounterPanel</code>.
         */
         public CounterPanel() {
                 super(new FlowLayout(FlowLayout.LEFT, 10, 0));
                 add(createIconLabel("runs"));
                 add(numRuns = createOutputField());
                 add(createIconLabel("errors"));
                 add(numErrors = createOutputField());
                 add(createIconLabel("failures"));
                 add(numFailures = createOutputField());
                 add(createIconLabel("assumptions"));
                 add(numAssumptions = createOutputField());
         } 
         //}}}
         
         //{{{ reset method.
         public void reset() {
                 setLabelValue(numErrors, 0);
                 setLabelValue(numFailures, 0);
                 setLabelValue(numRuns, 0);
                 fTotal = 0;
         } 
         //}}}
         
         public void setTotal(int value) {
                 fTotal = value;
         }
         
         public void setRunValue(int value) {
                 numRuns.setText(asString(value) + "/" + fTotal);
         }
         
         public void setErrorValue(int value) {
                 setLabelValue(numErrors, value);
         }
         
         public void setFailureValue(int value) {
                 setLabelValue(numFailures, value);
         }
         
         public void setAssumptionValue(int value) {
                 setLabelValue(numAssumptions, value);
         }

         private JLabel createOutputField() {
                 JLabel field = new JLabel("0");
                 field.setFont(BOLD_FONT);
                 return field;
         }
         
         private String asString(int value) {
                 return Integer.toString(value);
         }
         
         private void setLabelValue(JLabel label, int value) {
                 label.setText(asString(value));
         }
         
         //{{{ createIconLabel method.
         /**
          * Create a new icon label.
          */
         private JLabel createIconLabel(String name) {
                 Icon icon = getIcon(name);
                 JLabel label = new JLabel(icon);
                 label.setText(jEdit.getProperty("junit." + name + ".label") + ": ");
                 label.setToolTipText(jEdit.getProperty("junit." + name + ".tooltip"));
                 return label;
         } 
         //}}}
         
         //{{{ getIcon method.
         /**
          * Returns the the named icon.
          */
         static private Icon getIcon(String name) {
                 Icon icon = (Icon) icons.get(name);
                 if (icon == null) {
                         String path = "icons/" + name + ".gif";
                         URL url = CounterPanel.class.getResource(path);
                         if (url == null) {
                                 throw new IllegalArgumentException("Cannot find icon at path: " + path);
                         }
                         icon = new ImageIcon(url);
                         icons.put(name, icon);
                 }
                 return icon;
         } 
         //}}}
         
         // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
         
 }
