/*
 * TestRunView.java
 * Copyright (c) 2002 Calvin Yu
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
import javax.swing.*;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * A TestRunView is shown as a page in a tabbed folder. It contributes the page
 * contents and can return the currently selected tests. A TestRunView is
 * notified about the start and finish of a run.
 */
interface TestRunView {
        
        /**
         * Returns the currently selected Test in the View
         */
        public Description getSelectedTest();
        
        /**
         * Activates the TestRunView
         */
        public void activate();
        
        /**
         * Reveals the given failure
         */
        public void revealFailure(Failure failure);
        
        public void refresh(Description test, RunNotifier rn, DetailedResult result);
        
        /**
         * Returns the component that represents this view.
         */
        public Component getComponent();
        
        /**
         * Informs that the suite is about to start
         */
        public void aboutToStart(Description suite, RunNotifier rn, DetailedResult result);
        
        /**
         * Informs that the run of the test suite has finished
         */
        public void runFinished(Description suite, RunNotifier rn, DetailedResult result);
        
        /**
         * Goto next failed test.
         */
         public void nextFailure();
         
         /**
         * Goto prev failed test.
         */
         public void prevFailure();
}
