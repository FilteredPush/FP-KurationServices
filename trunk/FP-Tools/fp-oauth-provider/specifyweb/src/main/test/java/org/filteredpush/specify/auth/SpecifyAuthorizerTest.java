package org.filteredpush.specify.auth;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.junit.Before;
import org.junit.Test;

public class SpecifyAuthorizerTest {

	private String userId;
	private String password;
	private String baduser;
	
	@Before
	public void setUp() throws IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/test.properties"));
		
		userId = properties.getProperty("userid");
		password = properties.getProperty("password");
		baduser = properties.getProperty("baduser");
		
		String resourceName = "/test.properties";

		URL resource = SpecifyAuthorizerTest.class.getResource(resourceName);
        
        if (resource == null) {
            throw new IOException("resource not found: " + resource);
        }
        
		SpecifyAuthorizer.loadDbProperties(resource);
	}

	@Test
	public void testAuthenticate() throws LoginException {
		
		boolean authenticated = SpecifyAuthorizer.authenticate(userId, password);
		assertTrue(userId + " did not authenticate", authenticated);
		
		authenticated = SpecifyAuthorizer.authenticate(baduser, password);
		assertFalse(baduser + " should not have authenticated", authenticated);
		
		authenticated = SpecifyAuthorizer.authenticate(userId, "bad" + password);
		assertFalse(userId + " should not have authenticated with an incorrect password", authenticated);
	}

}
