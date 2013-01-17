This project is a slight modification of the OAuth example provider servlet.
See the following sites for the code repository and general information:

  http://code.google.com/p/oauth/
  http://oauth.net/

This OAuth provider is a thin layer over a Specify database that mimics
Specify thick client authentication.  It exists to grant web applications,
in particular the Filtered Push mapper, a way to import records into the
database on behalf of a user who has existing Specify credentials for that
database.

To build the war file for this project, you first need to create and fill in
this properties file:

  src/main/filters/filter.properties
  
There is a sample file included to help you get started.  These properties
need to match up with those of registered OAuth consumers, like the mapper.

The OAuth dependencies for this project can be obtained by obtaining the
sources for the example provider and building the appropriate jars.  The
example code is mavenized, so this should be straightforward.
 
And deploy the war in your servlet container.  You will need a consumer
application in order to test it out.  See the fpmapperweb project for an
example.
