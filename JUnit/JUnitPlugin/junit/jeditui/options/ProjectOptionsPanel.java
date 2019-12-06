/*
 * ProjectOptionsPanel.java 
 * Copyright (c) Thu Jun 21 14:02:09 MSD 2006 Denis Koryavov
 * Patched 01 August 2006 by Alan Ezust
 * Code based on PathBuilderDialog.java
 * Part of the JSwat plugin for the jEdit text editor
 * Copyright (C) 2001 David Taylor dtaylo11@bigpond.net.au
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import junit.JUnitPlugin;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

import projectviewer.vpt.VPTProject;

public class ProjectOptionsPanel extends JUnitOptionPane 
	implements ActionListener, ListSelectionListener
{

	// {{{ elements declaration
	private JTable pathElementTable;

	private PathElementTableModel pathElementModel;

	private JPanel btnPanel;

	private JButton addElementButton;

	private JButton removeElement;

	private JButton moveUp;

	private JButton moveDown;

	private boolean moveButtonsEnabled = true;

	private boolean multiSelectionEnabled = false;

	// The elements of the path.<p>
	private Vector elements = new Vector();

	// The file selection mode. By default it is FILES_AND_DIRECTORIES.<p>
	private int fileSelectionMode;

	private String addButtonText;

	private String removeButtonText;

	private String moveUpButtonText;

	private String moveDownButtonText;

	private String fileDialogTitle;

	private String fileDialogAction;

	private VPTProject project;

	// }}}

	// {{{ constructor
	/**
	 * Creates a new ProjectOptionsPanel.
	 * <p>
	 * 
	 * @param startDirectory
	 *                the initial directory to show in the file dialog.
	 * @param path
	 *                the current path elements, separated by
	 *                File.pathSeparator.
	 */
	public ProjectOptionsPanel(String title, VPTProject project)
	{
		super(title);
		this.project = project;
		_init();
	}

	// }}}

	// {{{ _init method.
	public void _init()
	{
		super._init();
		pathElementModel = new PathElementTableModel();

		addButtonText = jEdit.getProperty("junit.add-button-text") + "...";
		removeButtonText = jEdit.getProperty("junit.remove-button-text");
		moveUpButtonText = jEdit.getProperty("junit.move-up-button-text");
		moveDownButtonText = jEdit.getProperty("junit.move-down-button-text");
		fileDialogTitle = jEdit.getProperty("junit.file-dialog-title");
		fileDialogAction = jEdit.getProperty("junit.file-dialog-action");
		addElementButton = new JButton(addButtonText);
		addElementButton.addActionListener(this);
		if (project == null)
		{
			addElementButton.setEnabled(false);
		}

		removeElement = new JButton(removeButtonText);
		removeElement.addActionListener(this);

		moveUp = new JButton(moveUpButtonText);
		moveUp.addActionListener(this);

		moveDown = new JButton(moveDownButtonText);
		moveDown.addActionListener(this);

		btnPanel = new JPanel();
		btnPanel.add(addElementButton);
		btnPanel.add(removeElement);
		btnPanel.add(moveUp);
		btnPanel.add(moveDown);
		add(btnPanel, BorderLayout.SOUTH);

		removeElement.setEnabled(false);
		moveUp.setEnabled(false);
		moveDown.setEnabled(false);

		pathElementTable = new JTable(pathElementModel);
		JScrollPane tableScroller = new JScrollPane(pathElementTable);
		add(tableScroller, BorderLayout.CENTER);

		pathElementTable.getSelectionModel().addListSelectionListener(this);
		if (elements.size() > 0)
		{
			pathElementTable.setRowSelectionInterval(0, 0);
		}
		fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
		setSize(new Dimension(400, 600));
	}

	// }}}

	// {{{ setAddButtonText method.
	/**
	 * Set the text of the add element button.
	 * <p>
	 * 
	 * @param text
	 *                the String to display on the add element button.
	 */
	public void setAddButtonText(String text)
	{
		addElementButton.setText(text);
	}

	// }}}

	// {{{ setRemoveButtonText method.
	/**
	 * Set the text of the remove element button.
	 * <p>
	 * 
	 * @param text
	 *                the String to display on the remove element button.
	 */
	public void setRemoveButtonText(String text)
	{
		removeElement.setText(text);
	}

	// }}}

	// {{{ setMoveUpButtonText method.
	/**
	 * Set the text of the move up button.
	 * <p>
	 * 
	 * @param text
	 *                the String to display on the move up button.
	 */
	public void setMoveUpButtonText(String text)
	{
		moveUp.setText(text);
	}

	// }}}

	// {{{ setMoveDownButtonText method.
	/**
	 * Set the text of the move down button.
	 * <p>
	 * 
	 * @param text
	 *                the String to display on the move down button.
	 */
	public void setMoveDownButtonText(String text)
	{
		moveDown.setText(text);
	}

	// }}}

	// {{{ setFileSelectionMode method.
	/**
	 * Set a file selection mode to customise type of files can be selected.
	 * <p>
	 * 
	 * @param filter
	 *                the filter to use.
	 */
	public void setFileSelectionMode(int fsm)
	{
		this.fileSelectionMode = fsm;
	}

	// }}}

	// {{{ setStartDirectory method.
	/**
	 * Sets the initial directory to be displayed by the file dialog.
	 * <p>
	 * 
	 * @param startDirectory
	 *                the initial directory to be displayed by the file
	 *                dialog.
	 */
	public static void setStartDirectory(String startDirectory)
	{
		jEdit.setProperty("junit.file-dialog.dir", startDirectory);
	}

	public static String getStartDirectory()
	{
		return jEdit.getProperty("junit.file-dialog.dir", jEdit.getSettingsDirectory());
	}

	// }}}

	// {{{ setFileDialogTitle method.
	/**
	 * Sets the title of the file dialog.
	 * <p>
	 * 
	 * @param fileDialogTitle
	 *                the title of the file dialog.
	 */
	public void setFileDialogTitle(String fileDialogTitle)
	{
		this.fileDialogTitle = fileDialogTitle;
	}

	// }}}

	// {{{ setFileDialogAction method.
	/**
	 * Sets the label of the file dialog "approve" button.
	 * <p>
	 * 
	 * @param fileDialogAction
	 *                the label of the file dialog "approve" button.
	 */
	public void setFileDialogAction(String fileDialogAction)
	{
		this.fileDialogAction = fileDialogAction;
	}

	// }}}

	// {{{ setPath method.
	/**
	 * Set the path to be displayed in the list box.
	 * <p>
	 * 
	 * @param path
	 *                the current path elements, separated by
	 *                File.pathSeparator.
	 */
	public void setPath(String path)
	{
		int size = elements.size();
		if (size > 0)
		{
			elements.clear();
			pathElementModel.fireTableRowsDeleted(0, size - 1);
		}

		StringTokenizer st = new StringTokenizer(path, File.pathSeparator);

		while (st.hasMoreTokens())
		{
			elements.addElement(st.nextToken());
		}

		if (elements.size() > 0)
		{
			pathElementModel.fireTableRowsInserted(0, elements.size() - 1);
			pathElementTable.setRowSelectionInterval(0, 0);
		}
	}

	// }}}

	// {{{ setPathArray method.
	/**
	 * Set the path to be displayed in the list box.
	 * <p>
	 * 
	 * @param path
	 *                an array of the current path elements.
	 */
	public void setPathArray(String[] path)
	{
		int size = elements.size();
		if (size > 0)
		{
			elements.clear();
			pathElementModel.fireTableRowsDeleted(0, size - 1);
		}

		for (int i = 0; i < path.length; i++)
			elements.addElement(path[i]);

		if (elements.size() > 0)
		{
			pathElementModel.fireTableRowsInserted(0, elements.size() - 1);
			pathElementTable.setRowSelectionInterval(0, 0);
		}
	}

	// }}}

	// {{{ getPath method.
	/**
	 * Returns the path built using this PathBuilder as a single String,
	 * with the elements of the path separated by File.pathSeparator.
	 * <p>
	 * 
	 * @return the path built using this PathBuilder.
	 */
	public String getPath()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < elements.size(); i++)
		{
			sb.append((String) elements.elementAt(i));
			if (i < (elements.size() - 1))
				sb.append(File.pathSeparator);
		}

		return sb.toString();
	}

	// }}}

	// {{{ getPathArray method.
	/**
	 * Returns the path built using this PathBuilder as an array of Strings.
	 * <p>
	 * 
	 * @return the path built using this PathBuilder.
	 */
	public String[] getPathArray()
	{
		String[] pathArray = new String[elements.size()];
		for (int i = 0; i < elements.size(); i++)
			pathArray[i] = (String) elements.elementAt(i);

		return pathArray;
	}

	// }}}

	// {{{ setMoveButtonsEnabled method.
	/**
	 * Enable or disable the move buttons.
	 * <p>
	 * 
	 * @param enabled
	 *                true to enabled the move up and move down buttons,
	 *                false to hide them.
	 */
	public void setMoveButtonsEnabled(boolean enabled)
	{
		if (enabled == true && moveButtonsEnabled == false)
		{
			moveButtonsEnabled = true;
			btnPanel.add(moveUp);
			btnPanel.add(moveDown);
		}
		else if (enabled == false && moveButtonsEnabled == true)
		{
			moveButtonsEnabled = false;
			btnPanel.remove(moveDown);
			btnPanel.remove(moveUp);
		}
	}

	// }}}

	// {{{ setMultiSelectionEnabled method.
	/**
	 * Enable or disable multiple file selection in the file chooser.
	 * <p>
	 * 
	 * @param multiSelectionEnabled
	 *                true to enable multiple file selection, false to
	 *                disable it.
	 */
	public void setMultiSelectionEnabled(boolean multiSelectionEnabled)
	{
		this.multiSelectionEnabled = multiSelectionEnabled;
	}

	// }}}

	// {{{ actionPerformed method.
	/**
	 * Listen to specific GUI events.
	 * 
	 * @param evt
	 *                the GUI event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		int row;
		Object source = evt.getSource();
		if (source.equals(addElementButton))
		{
			addElement();
		}
		else if (source.equals(removeElement))
		{
			row = pathElementTable.getSelectedRow();
			if (row >= 0)
			{
				pathElementModel.remove(row);
			}
		}
		else if (source.equals(moveUp))
		{
			row = pathElementTable.getSelectedRow();
			if (row >= 1)
			{
				pathElementModel.moveUp(row);
			}
		}
		else if (source.equals(moveDown))
		{
			row = pathElementTable.getSelectedRow();
			if (row < (elements.size() - 1))
			{
				pathElementModel.moveDown(row);
			}
		}

		int tableSize = elements.size();
		if (tableSize < 1 && removeElement.isEnabled())
			removeElement.setEnabled(false);
		else if (tableSize > 0 && removeElement.isEnabled() != true)
			removeElement.setEnabled(true);

		// update the move up/down buttons
		valueChanged(null);
		JUnitPlugin.configureClassPath(getPath());
	}

	// }}}

	// {{{ addElement method.
	private void addElement()
	{
		JFileChooser chooser;

		chooser = new JFileChooser(getStartDirectory());
//		GUIUtilities.loadGeometry(chooser, "filechooser");
		chooser.setFileSelectionMode(fileSelectionMode);
		if (multiSelectionEnabled == true)
			chooser.setMultiSelectionEnabled(true);
		
		// QUESTION: [We need filter here?]
		// if(filter != null)
		// chooser.addChoosableFileFilter(filter);

		chooser.setDialogTitle(fileDialogTitle);
		int returnVal = chooser.showDialog(jEdit.getActiveView(), fileDialogAction);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
//			GUIUtilities.saveGeometry(chooser, "filechooser");	
			try
			{
				if (multiSelectionEnabled == true)
				{
					File[] files = chooser.getSelectedFiles();
					File f= files[0];
					if (f.isDirectory()) setStartDirectory(f.getPath());
					else setStartDirectory(f.getParent());
					for (int i = 0; i < files.length; i++)
						pathElementModel.add(files[i].getCanonicalPath());
				}
				else {
					File f = chooser.getSelectedFile();
					if (f.isDirectory()) setStartDirectory(f.getPath());
					else setStartDirectory(f.getParent());
					pathElementModel.add(chooser.getSelectedFile()
						.getCanonicalPath());
				}
					

				if (elements.size() == 1)
				{
					pathElementTable.setRowSelectionInterval(0, 0);
				}
			}
			catch (IOException ioe)
			{
			}
		}
	}

	// }}}

	// {{{ valueChanged method.
	/**
	 * Handle list selection events.
	 * <p>
	 * 
	 * @param evt
	 *                the list selection event.
	 */
	public void valueChanged(ListSelectionEvent evt)
	{
		int row = pathElementTable.getSelectedRow();
		int tableSize = elements.size();

		if (tableSize < 1 && removeElement.isEnabled())
			removeElement.setEnabled(false);
		else if (tableSize > 0 && removeElement.isEnabled() != true)
			removeElement.setEnabled(true);

		if (tableSize < 1)
		{
			moveUp.setEnabled(false);
			moveDown.setEnabled(false);
			return;
		}

		if (row < 1)
		{
			moveUp.setEnabled(false);
			if (tableSize > 1 && moveDown.isEnabled() != true)
				moveDown.setEnabled(true);
		}
		else if (row == (tableSize - 1))
		{
			moveDown.setEnabled(false);
			if (moveUp.isEnabled() != true)
				moveUp.setEnabled(true);
		}
		else
		{
			moveUp.setEnabled(true);
			moveDown.setEnabled(true);
		}
	}

	// }}}

	// {{{ class PathElementTableModel
	/**
	 * A simple table model of the classpathElementTable.
	 * <p>
	 */
	class PathElementTableModel extends AbstractTableModel
	{
		// {{{ getRowCount method.
		public int getRowCount()
		{
			return elements.size();
		}

		// }}}

		// {{{ getColumnCount method.
		public int getColumnCount()
		{
			return 1;
		}

		// }}}

		// {{{ getColumnName method.
		public String getColumnName(int column)
		{
			return "Classpath Elements";
		}

		// }}}

		// {{{ getValueAt method.
		public Object getValueAt(int row, int column)
		{
			return elements.elementAt(row);
		}

		// }}}

		// {{{ add method.
		/**
		 * Add an element to the path model.
		 * <p>
		 * 
		 * @param value
		 *                the path element to be added.
		 */
		protected void add(String value)
		{
			int rows = elements.size();
			elements.addElement(value);
			fireTableRowsInserted(rows, rows);
		}

		// }}}

		// {{{ remove method.
		/**
		 * Remove an element from the path model.
		 * <p>
		 * 
		 * @param row
		 *                the index of the element to remove.
		 */
		protected void remove(int row)
		{
			elements.removeElementAt(row);
			fireTableRowsDeleted(row, row);
			if (elements.size() > 0)
			{
				if (elements.size() > row)
				{
					pathElementTable.setRowSelectionInterval(row, row);
				}
				else
				{
					row = elements.size() - 1;
					pathElementTable.setRowSelectionInterval(row, row);
				}
			}
		}

		// }}}

		// {{{ moveUp method.
		/**
		 * Move an element up (towards the front of) the path.
		 * <p>
		 * 
		 * @param row
		 *                the element to be moved.
		 */
		protected void moveUp(int row)
		{
			Object a = elements.elementAt(row);
			Object b = elements.elementAt(row - 1);
			elements.setElementAt(a, row - 1);
			elements.setElementAt(b, row);
			fireTableRowsUpdated(row - 1, row);
			pathElementTable.setRowSelectionInterval(row - 1, row - 1);
		}

		// }}}

		// {{{ moveDown method.
		/**
		 * Move an element down (towards the end of) the path.
		 * <p>
		 * 
		 * @param row
		 *                the element to be moved.
		 */
		protected void moveDown(int row)
		{
			Object a = elements.elementAt(row);
			Object b = elements.elementAt(row + 1);
			elements.setElementAt(a, row + 1);
			elements.setElementAt(b, row);
			fireTableRowsUpdated(row, row + 1);
			pathElementTable.setRowSelectionInterval(row + 1, row + 1);
		}
		// }}}
	}
	// }}}

	// :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
