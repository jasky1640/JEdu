import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.junit.runners.Suite.*;
import static Main;

@RunWith(Suite.class)
@SuiteClasses({JUnit4Test.class NewTest.class})
public class NewTest{

	@Test
	public void succeedingTest1(){
		System.out.println("NewTest.succeedingTest1()");
		assertTrue(Main.add(1,2) == 3, true);

	}

	@Test
	public void succeedingTest2(){
		System.out.println("NewTest.succeedingTest2()");
	}

	@Test
	public void failingTest(){
		assertTrue("NewTest.failingTest",false);
	}
	
	@Test
	public void assumeTest(){
		assumeTrue(false);
		assertTrue("not triggered",false);
	}

	@Test @Ignore
	public void ignoredTest(){
		System.out.println("NewTest.ignoredTest()");
	}
	
	@Test
	public void errorTest(){
		throw new RuntimeException("NewTest.errorTest()");
	}
}
