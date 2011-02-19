/* Copyright 2011 the original author or authors.
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
*/

/**
*
*
* @author <a href='mailto:limcheekin@vobject.com'>Lim Chee Kin</a>
*
* @since 0.3
*
*/

includeTargets << grailsScript("Init")
ant.property (file : "application.properties")
appName = ant.project.properties.'app.name'

target(main: "Install dynamic domain class demo application") {
	ant.copy (todir:"${basedir}/grails-app/views", overwrite: true) {
		fileset dir:"${dynamicDomainClassPluginDir}/src/demo/grails-app/views"
	 }
	
	ant.copy (todir:"${basedir}/grails-app/controllers", overwrite: true) {
		fileset dir:"${dynamicDomainClassPluginDir}/src/demo/grails-app/controllers"
	 }
	
	ant.echo """\
	
******************************************************************
* Dynamic Domain Class demo application installed successfully.  *
* You can access it at http://localhost:8080/${appName}/demo.gsp *
******************************************************************

"""
}

setDefaultTarget(main)
