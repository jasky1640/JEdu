/*
* JUnit3Test.java
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

import junit.framework.*;
import static junit.framework.Assert.*;

/**
 */
public class JUnit3Test extends TestCase {

	public void testSucceeding1(){
		System.out.println("JUnit3Test.testSucceeding1()");
	}

	public void testSucceeding2(){
		System.out.println("JUnit3Test.testSucceeding2()");
	}

	public void testFailing(){
		assertTrue("Junit3Test.testFailing",false);
	}
	
	public void testError(){
		throw new RuntimeException("JUnit3Test.testError()");
	}
	
	public void notATest(){
		throw new UnsupportedOperationException("Don't call notATest !");
	}
}
