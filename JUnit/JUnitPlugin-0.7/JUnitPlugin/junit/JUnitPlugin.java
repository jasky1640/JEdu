/*
* JUnitPlugin.java
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

package junit;

import java.io.*;
import java.util.*;
import javax.swing.JPanel;

import org.gjt.sp.jedit.*;

import junit.jeditui.*;
import junit.jeditui.options.*;

import projectviewer.*;
import projectviewer.vpt.*;
import projectviewer.config.*;
import projectviewer.event.*;
import org.gjt.sp.util.Log;

import org.junit.runner.notification.Failure;

/**
* The plugin for jUnit.
*/
public class JUnitPlugin extends EBPlugin {
        private static final String INVOKE = "java.lang.reflect.Method.invoke*";
        private static final String ASSERT = "junit.framework.Assert(*)";
        private static final String ORG_ASSERT = "org.junit.Assert*";
        private static final String TEST_CASE = "junit.framework.TestCase*";
        private static final String TEST_RESULT = "junit.framework.TestResult*";
        private static final String TEST_SUITE = "junit.framework.TestSuite*";
        private static final String SUN_REFLECT = "sun.reflect.*";
        private static final String JEDIT_TEST_RUNNER = "junit.jeditui.TestRunner$3*";
        private static final String ORG_RUNNERS = "org.junit.runners.*";
        private static final String ORG_INTERNAL = "org.junit.internal.*";
        
        private static final String SEP = System.getProperty("file.separator");
        
        private static Hashtable testRunners = new Hashtable();
        private static boolean selected = true;
        private static Properties filters;
        private static String propsPath;
        
        //{{{ start method.
        public void start() {
                filters = getDefaultFilters();
                selected = jEdit.getBooleanProperty("junit.filter-stack-trace");
                jEdit.setBooleanProperty("junit.filter-stack-trace", selected);
                
                final File settingsDir = new File(MiscUtilities.constructPath(
                        jEdit.getSettingsDirectory(), "junit"));
                if (!settingsDir.exists()) settingsDir.mkdirs();
                
                String path = settingsDir.getAbsolutePath() + SEP + "filters.txt";
                propsPath = path;
                try {
                        File f = new File(path);
                        if(!f.exists()) {
                                f.createNewFile();
                                storeFilters(getDefaultFilters());
                        }
                        
                        FileInputStream fin = new FileInputStream(f);
                        filters.load(fin);
                        fin.close();
                } catch (IOException e) {
                        e.printStackTrace();
                        Log.log(Log.ERROR, "filters", e);
                }
        }
        //}}}
        
        //{{{ stop method. 
        public void stop() {
                testRunners.clear();
        }
        //}}}
        
        //{{{ createJUnitPanelFor method.
        public static JPanel createJUnitPanelFor(View view, String position) {
                TestRunner testRunner = getTestRunner(view);
                return testRunner._createUI(position, selected);
        }
        //}}}
        
        //{{{ createPathBuilder
        public static JPanel createPathBuilder() {
                VPTProject project = getActiveProject();
                ProjectOptionsPanel panel = new ProjectOptionsPanel(
                        jEdit.getProperty("options.junit.pconfig.label"), 
                        project);
                
                panel.setPath(getClassPath());
                if (project != null) {
                        panel.setStartDirectory(project.getRootPath());
                }
                return panel;
        }
        //}}}
        
        //{{{ getClassPath method.
        public static String getClassPath() {
                String classPath = "";
                VPTProject project = getActiveProject();
                if (project != null) {
                        classPath = project.getProperty("junit.class-path");
                        if (classPath == null) {
                                project.setProperty("junit.class-path", "");
                                classPath = "";
                        }
                }
                return classPath;
        } 
        //}}}
        
        //{{{ getFilters method.
        public static Properties getFilters(boolean defaults) {
                if (defaults) {
                        return getDefaultFilters();
                } else {
                        return filters;
                }
        } 
        //}}}
        
        //{{{ getDefaultFilters method.
        private static Properties getDefaultFilters() {
                Properties filters = new Properties();
                filters.setProperty(INVOKE, "true");
                filters.setProperty(ASSERT, "true");
                filters.setProperty(TEST_CASE, "true");
                filters.setProperty(TEST_RESULT, "true");
                filters.setProperty(TEST_SUITE, "true");
                filters.setProperty(SUN_REFLECT, "true");
                filters.setProperty(JEDIT_TEST_RUNNER, "true");
                filters.setProperty(ORG_ASSERT, "true");
                filters.setProperty(ORG_RUNNERS, "true");
                filters.setProperty(ORG_INTERNAL, "true");
                return filters;
        } 
        //}}}
        
        //{{{ refresh method.
        public static void refresh(VPTProject project, View view) {
                String classPath = project.getProperty("junit.class-path");
                getTestRunner(view).setClassPath(classPath);        
        } 
        //}}}
        
        //{{{ configureClassPath method.
        public static void configureClassPath(String classPath) {
                getActiveProject().setProperty("junit.class-path", classPath);
                getTestRunner(jEdit.getActiveView()).setClassPath(classPath);  
        } 
        //}}}
        
        //{{{  getTestRunner method.
        private static TestRunner getTestRunner(View view) {
                TestRunner testRunner = (TestRunner) testRunners.get(view);
                if (testRunner == null) {
                        testRunner = new TestRunner(view);
                        testRunners.put(view, testRunner);
                }
                return testRunner;
        } 
        //}}}
        
        //{{{ getActiveProject method.
        private static VPTProject getActiveProject() {
                return ProjectViewer.getActiveProject(jEdit.getActiveView());
        } 
        //}}}
        
        //{{{ storeFilters method.
        public static void storeFilters(Properties properties) {
                try { 
                        File f = new File(propsPath);
                        FileOutputStream fout = new FileOutputStream(f);
                        properties.store(fout, "Stack Trace Filters:");
                        fout.flush();
                        fout.close();
                        filters = properties;
                } catch (IOException e) {
                        e.printStackTrace();
                        Log.log(Log.ERROR, "filters", e);
                }
        } 
        //}}}
        
        //{{{ EBPlugin.handleMessage method
        @Override
        public void handleMessage(EBMessage e) {
        	if(e instanceof ViewerUpdate) {
        		ViewerUpdate u = (ViewerUpdate)e;
        		if(u.getType() == ViewerUpdate.Type.PROJECT_LOADED) {
        			refresh((VPTProject)u.getNode(), u.getView());
        		}
        	}
        }//}}}
        
        //{{{ isFailure method
        public static boolean isFailure(Failure f) {
        	return f.getException() instanceof AssertionError;
        }
        //}}}
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
