package fp.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	DateServiceTest.class, 
	GeoRefServiceTest.class,
	SciNameServiceTest.class
	})
public class AllTests {

}
