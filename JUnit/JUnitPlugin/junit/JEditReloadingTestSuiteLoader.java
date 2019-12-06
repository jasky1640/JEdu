package junit;

/**
* A TestSuite loader that can reload classes.
* It's used by junit.jeditui.TestRunner.
*/
public class JEditReloadingTestSuiteLoader  {
        private String classPath;
        
        public JEditReloadingTestSuiteLoader() {
                this(System.getProperty("java.class.path"));
        }
        
        public JEditReloadingTestSuiteLoader(String classPath) {
                this.classPath = classPath;
        }
        
        public Class load(String suiteClassName) throws ClassNotFoundException {
                JEditTestCaseClassLoader loader = new JEditTestCaseClassLoader(this.classPath);
                return loader.loadClass(suiteClassName);
        }
        
        public Class reload(Class aClass) throws ClassNotFoundException {
                JEditTestCaseClassLoader loader = new JEditTestCaseClassLoader(this.classPath);
                return loader.loadClass(aClass.getName());
        }
}
