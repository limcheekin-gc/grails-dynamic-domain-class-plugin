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
 */

/**
 *
 * @author <a href='mailto:limcheekin@vobject.com'>Lim Chee Kin</a>
 *
 * @since 0.1.0
 */
class DynamicDomainClassGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.5 > *"
    // the other plugins this plugin depends on
    def dependsOn = [hibernate: "1.3.5 > *"]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
    def loadAfter = ['hibernate']
    def author = "Lim Chee Kin"
    def authorEmail = "limcheekin@vobject.com"
    def title = "Grails Dynamic Domain Class Plugin - Create domain class on-the-fly"
    def description = '''
 The Dynamic Domain Class plugin enabled Grails application to create domain class dynamically 
 when application is running.

 * Project Site and Documentation: http://code.google.com/p/grails-dynamic-domain-class-plugin/
 * Support: http://code.google.com/p/grails-dynamic-domain-class-plugin/issues/list
 * Discussion Forum: http://groups.google.com/group/grails-dynamic-domain-class-plugin
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/dynamic-domain-class"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        dynamicDomainService(org.grails.dynamicdomain.DynamicDomainService)
		    renderEditor(org.grails.dynamicdomain.RenderEditor)
	 }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
