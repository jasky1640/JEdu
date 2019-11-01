import org.junit.Assert;

class Test {
	@Test
	public void testAssertions() {
		//test data
		String str1 = new String ("abc");
		String str2 = new String ("abc");
      
		//Check that two objects are equal
		assertEquals(str1, str2);
	}
	
	public static void main(String[] args) {
		System.out.println("Hello World");	
	}
	
}