This project is a slight modification of the OAuth example consumer servlet.
See the following sites for the code repository and general information:

  http://code.google.com/p/oauth/
  http://oauth.net/

This OAuth consumer is a stub of a Filtered Push mapper that wants to
import a specimen record into a Specify database.  It redirects the browser
user to a Specify OAuth provider login page, as it follows the OAuth 1.0
protocol to obtain credentials from a Specify OAuth provider.

To build the war file for this project, you first need to create and fill in
this properties file:

  src/main/filters/filter.properties
  
There is a sample file included to help you get started.  These properties
need to match up with those expected by the OAuth provider.

The OAuth dependencies for this project can be obtained by obtaining the
sources for the example provider and building the appropriate jars.  The
example code is mavenized, so this should be straightforward.

To run this project, first make sure the provider is deployed (see the
"specifyweb" project), and then deploy the war for this project.  If you are
deploying on "localhost" on port 8080, you can visit this url:

  http://localhost:8080/fpmapperweb/
  
You can click "Test the Specify HUH OAuth Provider" to see the provider's
login page.  If you enter valid login info for the Specify database represented
by the provider, then you should get a simple text-only page returned that looks
something like this:

  null
  
  Hi, I'm the Specify Servlet.  I was called like this:
  Parameters:
  echo: Hello.
  Authorization Headers:
  OAuth oauth_token="88d8cc31cc1275f80f828d64f0060c3b",
  oauth_consumer_key="filteredpush", oauth_signature_method="HMAC-SHA1",
  oauth_timestamp="1336606940", oauth_nonce="37222849120411", oauth_version="1.0",
  oauth_signature="246kqkqdyPfDnpaOVZmdQo2Jl%2FY%3D"

If you enter invalid login info, you will get a cryptic browser error page.
That will be made more user-friendly soon.

This OAuth consumer uses cookies to store OAuth credentials.  After a
successful login, the "Test the Specify HUH OAuth Provider" link should not
redirect to the OAuth provider's login page, but directly to the "Hi" page.

To remove the cookie, click the "Reset" link on the main page.