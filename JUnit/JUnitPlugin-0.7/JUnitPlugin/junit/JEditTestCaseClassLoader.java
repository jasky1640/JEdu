/*
* JEditTestCaseClassLoader.java
* Copyright (c) 2001, 2002 Andre Kaplan
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

import org.gjt.sp.jedit.JARClassLoader;
import org.gjt.sp.util.Log;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * class providing a sole loadClass() method,
 * which will load objects from the given classpath.
 * An URLClassLoader is used internally with the JUnitPlugin
 * JARClassLoader as a parent.
 * This grabs the new version of a class
 * after a plugin is reloaded.
 */
public class JEditTestCaseClassLoader{
	private ClassLoader loader;
	
	public JEditTestCaseClassLoader(String classPath) {
		if(classPath.length()  == 0){
			loader = JUnitPlugin.class.getClassLoader();
		}else{
			String[] paths = classPath.split(File.pathSeparator);
			List<URL> urls = new ArrayList<URL>(paths.length);
			for(int i=0;i<paths.length;i++){
				try{
					urls.add(new File(paths[i]).toURI().toURL());
				}catch(MalformedURLException e){
					Log.log(Log.ERROR,JEditTestCaseClassLoader.class,"invalid classpath : "+paths[i]);
					Log.log(Log.ERROR,JEditTestCaseClassLoader.class,e);
				}
			}
			loader = new URLClassLoader(urls.toArray(new URL[]{}), JUnitPlugin.class.getClassLoader());
		}
	}
	
	//{{{ loadClass method.
	public synchronized Class loadClass(String name) 
	throws ClassNotFoundException 
	{
		try {
			return loader.loadClass(name);
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException(name);
		}
	}
	//}}}
}
