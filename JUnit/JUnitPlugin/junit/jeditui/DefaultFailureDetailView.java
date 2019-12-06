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

import java.awt.Component;
import java.util.regex.*;

import java.io.*;
import java.util.*;
import javax.swing.JTextArea;
import junit.JUnitPlugin;
import org.junit.runner.*;
import org.junit.runner.notification.*;

import org.gjt.sp.jedit.jEdit;

/**
 * A view that shows a stack trace of a failure
 */
class DefaultFailureDetailView{
        JTextArea fTextArea;
        
        //{{{ getComponent method.
        /**
         * Returns the component used to present the trace
         */
        public Component getComponent() {
                if (fTextArea == null) {
                        fTextArea = new JTextArea();
                        fTextArea.setRows(5);
                        fTextArea.setTabSize(0);
                        fTextArea.setEditable(false);
                }
                return fTextArea;
        } 
        //}}}
        
        //{{{ showFailure method.
        /**
         * Shows a Failure
         */
        public void showFailure(Failure failure) {
                String text = failure.getTrace();
                boolean b = jEdit.getBooleanProperty("junit.filter-stack-trace");
                Hashtable filters = JUnitPlugin.getFilters(false);
                fTextArea.setText(getFilteredText(text, filters, b));
                fTextArea.select(0, 0);
        } 
        //}}}
        
        //{{{ clear method.
        public void clear() {
                fTextArea.setText("");
        } //}}}
        
        //{{{ getFilteredText method.
        private String getFilteredText(String text, Hashtable filters, boolean b) {
                if (!b) return text;
                
                StringTokenizer st = new StringTokenizer(text, "\n");
                String result = "";
                Set keySet = filters.keySet();
                
                while (st.hasMoreTokens()) {
                        boolean addToResult = true;
                        String s = st.nextToken();
                        for (Iterator iter = keySet.iterator(); iter.hasNext();) {
                                String filter = (String)iter.next();
                                if (Boolean.parseBoolean((String)filters.get(filter)) == false)
                                        continue;
                                
                                String ps = filter.replaceAll("\\.", "\\\\.");
                                ps = ps.replaceAll("\\*", ".+?");
                                ps = ps.replaceAll("\\?", ".");
                                ps = ps.replaceAll("\\$", ".");
                                ps = ps.replaceAll("\\(", "\\\\("); // ))
                                ps = ps.replaceAll("\\)", "\\\\)");
                                ps = ".*?" + ps;
                                
                                s.replaceAll("\n", "");
                                s.replaceAll("\r", "");
                                
                                Pattern p = Pattern.compile(ps.toUpperCase().trim());
                                Matcher matcher = p.matcher(s.toUpperCase().trim());
                                if (matcher.matches()) addToResult = false;
                        }
                        if (addToResult) result += s + "\n";
                }
                return result;
        } 
        //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
