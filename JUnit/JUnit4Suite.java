import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.junit.runners.Suite.*;

@RunWith(Suite.class)
@SuiteClasses({JUnit4Test.class, JUnit4Suite.class})
public class JUnit4Suite{

	@Test
	public void succeedingTest1(){
		System.out.println("JUnit4Suite.succeedingTest1()");
	}

	@Test
	public void succeedingTest2(){
		System.out.println("JUnit4Suite.succeedingTest2()");
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
		System.out.println("JUnit4Suite.ignoredTest()");
	}
	
	@Test
	public void errorTest(){
		throw new RuntimeException("JUnit4Suite.errorTest()");
	}
}
