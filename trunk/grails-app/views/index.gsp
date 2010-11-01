<%--
/* Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author <a href='mailto:limcheekin@vobject.com'>Lim Chee Kin</a>
 *
 * @since 0.1
 */
 --%> 
<%
String defaultDomainClassCode = """
package com.foo.testapp.book

class Book {
   String title
   String description

   static constraints = { 
        title(blank: false) 
        description(blank: false) 
   } 
}

package com.foo.testapp.author

import com.foo.testapp.book.Book

class Author {
   static hasMany = [books: Book]
   String name
}
"""
 %>
<html>
    <head>
        <title>Welcome to Grails</title>
        <meta name="layout" content="main" />
        <style type="text/css" media="screen">

        #nav {
            margin-top:20px;
            margin-left:30px;
            width:228px;
            float:left;

        }
        .homePagePanel * {
            margin:0px;
        }
        .homePagePanel .panelBody ul {
            list-style-type:none;
            margin-bottom:10px;
        }
        .homePagePanel .panelBody h1 {
            text-transform:uppercase;
            font-size:1.1em;
            margin-bottom:10px;
        }
        .homePagePanel .panelBody {
            background: url(images/leftnav_midstretch.png) repeat-y top;
            margin:0px;
            padding:15px;
        }
        .homePagePanel .panelBtm {
            background: url(images/leftnav_btm.png) no-repeat top;
            height:20px;
            margin:0px;
        }

        .homePagePanel .panelTop {
            background: url(images/leftnav_top.png) no-repeat top;
            height:11px;
            margin:0px;
        }
        h2 {
            margin-top:15px;
            margin-bottom:15px;
            font-size:1.2em;
        }
        #pageBody {
            margin-left:280px;
            margin-right:20px;
        }
        </style>
    </head>
    <body>
        <div id="nav">
            <div class="homePagePanel">
                <div class="panelTop"></div>
                <div class="panelBody">
                    <h1>Application Status</h1>
                    <ul>
                        <li>App version: <g:meta name="app.version"></g:meta></li>
                        <li>Grails version: <g:meta name="app.grails.version"></g:meta></li>
                        <li>Groovy version: ${org.codehaus.groovy.runtime.InvokerHelper.getVersion()}</li>
                        <li>JVM version: ${System.getProperty('java.version')}</li>
                        <li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
                        <li>Domains: ${grailsApplication.domainClasses.size()}</li>
                        <li>Services: ${grailsApplication.serviceClasses.size()}</li>
                        <li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
                    </ul>
                    <h1>Installed Plugins</h1>
                    <ul>
                        <g:set var="pluginManager"
                               value="${applicationContext.getBean('pluginManager')}"></g:set>

                        <g:each var="plugin" in="${pluginManager.allPlugins}">
                            <li>${plugin.name} - ${plugin.version}</li>
                        </g:each>

                    </ul>
                </div>
                <div class="panelBtm"></div>
            </div>
        </div>
        <div id="pageBody">
            <h1>Welcome to Grails</h1>
            <p>Congratulations, you have successfully started your first Grails application!
            The index.gsp file is overwritten during the installation of Grails Dynamic Domain Class Plugin, 
            but you need not to worry as the original version of overwritten index.gsp file are backup as index.bak file, 
            you can restore to original version of the overwritten file as necessary. 
            This is the default page, feel free to modify it to either redirect to a controller or display whatever
            content you may choose. First, you need to create domain class(es) using text box below and then you will see 
            list of controllers for domain class(es) you created in Dynamic Controllers section, click on each to execute its default action. 
            Next, follow by Other Controllers section to display list of available Grails controllers:</p>
						<div id="domainClassCreationPanel" class="dialog">  
						<h2>Create Dynamic Domain Class(es):</h2>       
						  <%
							if (params?.domainClassCode && params?.domainClassCode.stripIndent() != "") {
            			def dds = grailsApplication.mainContext.dynamicDomainService
                  params?.domainClassCode.stripIndent().split("package").each {
	                  if (it != "") {
	                      dds.registerDomainClass "package$it"
	                                    }
                                    }
                  dds.updateSessionFactory grailsApplication.mainContext
                               }		
						    %>  
						  <g:form>
						    <p>Enter the domain class(es) code here (must be declared in package):</p> 
							  <g:textArea name="domainClassCode" cols="80" rows="10" value="${params?.domainClassCode?:defaultDomainClassCode}" style="width: 500px"/>
                <p>
                   <span class="button">
                       <g:submitButton name="create" class="save" value="Create Domain Class(es)" />
                       <input type="button" value="Clear" onclick="javascript: document.forms[0].domainClassCode.value=''"/>
                   </span>
                </p>
							</g:form>
						</div>
            <div id="controllerList" class="dialog">
                <h2>Dynamic Controllers:</h2>
                <ul>
                    <g:each var="dc" in="${grailsApplication.domainClasses.sort { it.fullName } }">
                      <% if (!grailsApplication.getControllerClass("${dc.fullName}Controller")) { %>
                        <li class="controller"><g:link controller="ddc" params="[dc:dc.fullName]">${dc.fullName}Controller</g:link></li>
                                          <% } %>
                    </g:each>
                </ul>
            </div>
            <div id="controllerList" class="dialog">
                <h2>Other Controllers:</h2>
                <ul>
                    <g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
                    		<g:if test="${c.fullName != 'org.grails.dynamicdomain.DdcController'}">
                       	 	<li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
                       	</g:if>
                    </g:each>
                </ul>
            </div>                
        </div>
    </body>
</html>
