/*
* DetailedResult.java
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

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.Result;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import junit.JUnitPlugin;


public class DetailedResult extends RunListener{
	private int fNbFailures;
	private int fNbErrors;
	private int fNbAssumptions;
	private int fNbIgnored;
	private int fNbRun;
	private List<Failure> failures;
	private boolean wasSuccessful;
	private long startTime;
	private long endTime;
	
	public int getFailureCount(){
		return fNbFailures;
	}
	
	public int getErrorCount(){
		return fNbErrors;
	}
	
	public int getAssumptionCount(){
		return fNbAssumptions;
	}
	
	public int getIgnoreCount(){
		return fNbIgnored;
	}
	
	public int getRunCount(){
		return fNbRun;
	}
	
	public java.util.List<Failure> getFailures(){
		return failures;
	}
	
	public long getRunTime(){
		return endTime - startTime;
	}
	
	public boolean wasSuccessful(){
		return wasSuccessful;
	}
	
	@Override
	public void testAssumptionFailure(Failure failure){
		fNbAssumptions++;
		wasSuccessful = false;
	}
	
	@Override
	public void testFailure(Failure failure){
		if(JUnitPlugin.isFailure(failure)){
			fNbFailures++;
		}else{
			fNbErrors++;
		}
		wasSuccessful = false;
		failures.add(failure);
	}
	
	@Override
	public void testFinished(Description description){
		fNbRun++;
	}
	
	@Override
	public void testIgnored(Description description){
		wasSuccessful = true;
		fNbIgnored++;
	}
	
	@Override
	public void testRunFinished(Result result){
		endTime = System.currentTimeMillis();
	}
	
	@Override
	public void testRunStarted(Description description){
		fNbAssumptions = fNbErrors = fNbFailures = fNbIgnored = fNbRun = 0;
		startTime = System.currentTimeMillis();
		failures = new ArrayList<Failure>(description.testCount());
	}
	
	@Override
	public void testStarted(Description description){
		wasSuccessful = true;
	}
}
