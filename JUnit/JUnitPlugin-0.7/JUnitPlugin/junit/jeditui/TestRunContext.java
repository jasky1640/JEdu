/*
 * TestRunContext.java 
 * Copyright (c) 2001 - 2003 Andre Kaplan, Calvin Yu
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

import javax.swing.ListModel;
import org.junit.runner.Description;

/**
 * The interface for accessing the Test run context. Test run views should use
 * this interface rather than accessing the TestRunner directly.
 */
public interface TestRunContext {

  /**
   * Run the current test.
   */
  public void runSelectedTest(Description test);

  /**
   * Handles the selection of a Test.
   */
  public void handleTestSelected(Description test);

  /**
   * Returns the failure model
   */
  public ListModel getFailures();
}
