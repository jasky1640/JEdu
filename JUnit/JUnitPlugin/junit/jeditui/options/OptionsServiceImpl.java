/*
 * OptionsServiceImpl.java
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

package junit.jeditui.options;

import projectviewer.config.*;
import projectviewer.vpt.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;

/** returns the JUnit classpath option pane for ProjectViewer */
public class OptionsServiceImpl implements OptionsService{
	public OptionGroup getOptionGroup(VPTProject proj){
		return null;
	}
	public OptionPane getOptionPane(VPTProject project){
		ProjectOptionsPanel panel = new ProjectOptionsPanel(
			"junit.pconfig", 
			project);
		
		panel.setPath(junit.JUnitPlugin.getClassPath());
		if (project != null) {
			panel.setStartDirectory(project.getRootPath());
		}
		return panel;
	} 
	
}
