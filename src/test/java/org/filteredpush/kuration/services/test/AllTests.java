package org.filteredpush.kuration.services.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	DateServiceTest.class, 
	GeoRefServiceTest.class,
	SciNameServiceTest.class,
	IPNIServiceTest.class
	})
public class AllTests {

}
