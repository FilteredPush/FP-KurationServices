package org.filteredpush.kuration.services.test;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.services.sciname.GBIFService;
import org.filteredpush.kuration.services.sciname.SciNameServiceParent;
import org.junit.Before;
import org.junit.Test;

import edu.harvard.mcz.nametools.ICNafpAuthorNameComparator;
import edu.harvard.mcz.nametools.ICZNAuthorNameComparator;

public class SciNameServiceParentTest {
	private static final Log logger = LogFactory.getLog(SciNameServiceParentTest.class);
	
	private SciNameServiceParent service;

	@Before
	public void setUp() throws Exception {
		service = new GBIFService();
	}

	@Test
	public void testAddToComment() {
		assertEquals("",service.getComment());
		service.addToComment("Test");
		assertEquals("Test",service.getComment());
		service.addToComment("More");
		assertEquals("Test | More",service.getComment());
	}

	@Test
	public void testAddToServiceName() {
		assertEquals("", service.getServiceName());
		service.addToServiceName("GBIF");
		assertEquals("GBIF", service.getServiceName());
	}

	@Test
	public void testGetAuthorNameComparator() {
		assertEquals(ICZNAuthorNameComparator.class,service.getAuthorNameComparator("Turner, 1984", "Animalia").getClass());
		assertEquals(ICNafpAuthorNameComparator.class,service.getAuthorNameComparator("L.", "Plantae").getClass());
	}

}
