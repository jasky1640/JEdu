/*
* SpinningTest.java
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
public class SpinningTest{

	@Test
	public void infiniteDepth(){
		infiniteDepth();
	}

	@Test
	public void loopingTest(){
		long l = 0;
		for(int i=0;i<Integer.MAX_VALUE;i++){
			l+= i;
		}
		System.err.println("done loopingTest "+l);
	}

}
