/*
* Junit4Test.java
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

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 */
public class JUnit4Test{

	@Test
	public void succeedingTest1(){
		System.out.println("JUnit4Test.succeedingTest1()");
	}

	@Test
	public void succeedingTest2(){
		System.out.println("JUnit4Test.succeedingTest2()");
	}

	@Test
	public void failingTest(){
		assertTrue("Junit4Test.failingTest",false);
	}
	
	@Test
	public void assumeTest(){
		assumeTrue(false);
		assertTrue("not triggered",false);
	}

	@Test @Ignore
	public void ignoredTest(){
		System.out.println("JUnit4Test.ignoredTest()");
	}
	
	@Test
	public void errorTest(){
		throw new RuntimeException("JUnit4Test.errorTest()");
	}
}
