package org.filteredpush.kuration.services.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	InternalDateValidationServiceTest.class, 
	GeoRefServiceTest.class,
	COLServiceTest.class,
	IPNIServiceTest.class,
	WoRMSServiceTest.class,
	GBIFServiceTest.class,
	SciNameServiceParentTest.class,
	IndexFungorumServiceTest.class,
	Geolocate3Test.class
	})
public class AllTests {

}
