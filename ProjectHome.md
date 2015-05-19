# Overview #
The Grails Dynamic Domain Class Plugin is created to enable Grails application to create domain class dynamically when application is running. The plugin is created based on the works of Burt Beckwith at http://burtbeckwith.com/blog/?p=364. Thanks to Burt for his kindness to contribute the initial code base of the plugin. It is hardly imagine the possibility of creating the plugin without his contribution.

# Installation #
Install the plugin into your project with the following command:
```
grails install-plugin dynamic-domain-class
```

# Configuration #
No configuration needed, the application is ready to run!

# Install the demo application (optional) #
Install the demo application into your project with the following command:
```
grails install-ddc-demo
```

# Run the application #
Run your application with the following command:
```
grails run-app
```

By open your browser at http://localhost:8080/[yourApplicationName]/demo.gsp, you should see the following screen:

![http://grails-dynamic-domain-class-plugin.googlecode.com/svn/trunk/docs/images/main-screen.jpg](http://grails-dynamic-domain-class-plugin.googlecode.com/svn/trunk/docs/images/main-screen.jpg)


---


You can create more than one domain class by enter the domain class code to the text box (Please refer to default sample code) and click "Create Domain Class(es)" button.

Next, you will see dynamic controller(s) generated for domain class(es) defined under Dynamic Controllers section.

![http://grails-dynamic-domain-class-plugin.googlecode.com/svn/trunk/docs/images/domain-class-created-screen.jpg](http://grails-dynamic-domain-class-plugin.googlecode.com/svn/trunk/docs/images/domain-class-created-screen.jpg)


---


The dynamic controller and views works like Dynamic Scaffolding (In fact, it is created based on the scaffolding templates). You can use it like normal Grails controllers and views.

![http://grails-dynamic-domain-class-plugin.googlecode.com/svn/trunk/docs/images/book-listing-screen.jpg](http://grails-dynamic-domain-class-plugin.googlecode.com/svn/trunk/docs/images/book-listing-screen.jpg)

# Version History #
**19-Feb-2011 0.3**
  * The demo application no longer install automatically. Created `install-ddc-demo` script to install the demo application.

**30-Dec-2010 0.2.1**
  * Support grails 1.2+ as per [Burt's advice](http://code.google.com/p/grails-dynamic-domain-class-plugin/issues/detail?id=1).

**01-Nov-2010 0.2**
  * Fixed the known issue of the plugin unable to run in tomcat production environment. The plugin is tested and working in Apache Tomcat 6.0.29, MySQL5 and H2 Database in Linux environment. If you are managed to use the plugin in other application servers and database servers, please post your result to Application Server and Database Server Compatibility List at http://groups.google.com/group/grails-dynamic-domain-class-plugin/browse_thread/thread/120e99ad5ce2caa7
  * Enhance the index.gsp page to show available non-dynamic Grails controllers.

**19-Oct-2010 0.1**
  * Dynamic domain class, dynamic controller and dynamic views support in development and testing environment.

# Final Note #
We are welcome your feedback and would like to hear about in what project and how you use the plugin. You are welcome to join the project discussion forum at http://groups.google.com/group/grails-dynamic-domain-class-plugin, see you there!