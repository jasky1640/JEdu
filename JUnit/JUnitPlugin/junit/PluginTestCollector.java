/*
* PluginTestCollector.java
* Copyright (c) 2002 Calvin Yu
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

package junit;

import java.io.File;
import java.io.FileFilter;

import org.junit.runner.Description;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * Test collector that uses the junit configured class path.
 *
 * It does all the work by itself, but does it badly:
 *   - any .class file in a directory is returned
 *   - lookup of tests in jar files in not supported
 *
 * the classes are actually loaded, using a fresh JEditTestCaseClassLoader
 * configured with the classPath.
 * They could be tested with annotations or subclass of TestCase
 * for compatibility with junit 3
 */
public class PluginTestCollector{
        private String classPath;
        
        /**
         * Create a new <code>PluginTestCollector</code>.
         */
        public PluginTestCollector(String aClassPath) {
                classPath = aClassPath;
        }
        
        /**
         * Create a new <code>PluginTestCollector</code>.
         */
        public PluginTestCollector() {
                classPath = jEdit.getProperty("java.class.path");
                if(classPath == null)classPath = "";
        }

        /**
          * @return a Description containing every class found in the classpath
          */
        public Description collectTests() {
        	JEditTestCaseClassLoader loader = new JEditTestCaseClassLoader(classPath);
			String[] paths = classPath.split(File.pathSeparator);
			Description d = Description.createSuiteDescription("All Tests");
			for(int i=0;i<paths.length;i++){
				collectTests(paths[i], loader, d);
			}
			return d;
		}
		
		
		private void collectTests(String path, JEditTestCaseClassLoader loader, Description d){
			File f = new File(path);
			if(f.isDirectory()){
				collectTestsFromDir(f,loader,"",d);
			}else{
				collectTestsFromJar(f,loader,d);
			}
		}
		
		
		/**
		 * accepts directories and classes (not internal classes)
		 */
		private FileFilter filter = new FileFilter(){
			public boolean accept(File f){
				return f.isDirectory()
					|| (f.getName().endsWith(".class")
							&& !f.getName().contains("$"));
			}
		};
		
		/**
		 * @param prefix current prefix of the would-be package (includes final dot or is empty)
		 */
		private void collectTestsFromDir(File f,  JEditTestCaseClassLoader loader, String prefix, Description d){
			File[] contents = f.listFiles(filter);
			for(File c: contents){
				if(c.isDirectory()){
					String nPrefix = prefix+c.getName()+".";
					collectTestsFromDir(c,loader,nPrefix,d);
				}else{
					// remove .class
					String className = prefix+c.getName().substring(0,c.getName().length()-6);
					try{
						Class cl = loader.loadClass(className);
						d.addChild(Description.createSuiteDescription(cl));
					}catch(ClassNotFoundException e){
						Log.log(Log.WARNING,PluginTestCollector.class,"test class not found: "+className);
					}catch(ClassFormatError e){
						Log.log(Log.WARNING,PluginTestCollector.class,"this is not a test class: "+className);
					}
				}
			}
		}
		
		private void collectTestsFromJar(File f,  JEditTestCaseClassLoader loader, Description d){
			// FIXME: implement
			Log.log(Log.NOTICE,PluginTestCollector.class,"collecting tests from jar is not supported ("+f+")");
		}
}		
