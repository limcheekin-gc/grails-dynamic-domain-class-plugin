package org.grails.dynamic.domain

import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.codehaus.groovy.grails.plugins.DomainClassPluginSupport
import org.codehaus.groovy.grails.validation.ConstrainedPropertyBuilder
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class ValidationSupport {
	static final Log LOG = LogFactory.getLog(ValidationSupport)
	
	static addDynamicMethods(application, validateableClass, ctx) {
		def metaClass = validateableClass.metaClass
		metaClass.hasErrors = {-> delegate.errors?.hasErrors() }
		
		def get
		def put
		try {
			def rch = application.classLoader.loadClass("org.springframework.web.context.request.RequestContextHolder")
			get = {
				def attributes = rch.getRequestAttributes()
				if (attributes) {
					return attributes.request.getAttribute(it)
				}
				return PROPERTY_INSTANCE_MAP.get().get(it)
			}
			put = { key, val ->
				def attributes = rch.getRequestAttributes()
				if (attributes) {
					attributes.request.setAttribute(key, val)
				}
				else {
					PROPERTY_INSTANCE_MAP.get().put(key, val)
				}
			}
		}
		catch (Throwable e) {
			get = { PROPERTY_INSTANCE_MAP.get().get(it) }
			put = {key, val -> PROPERTY_INSTANCE_MAP.get().put(key, val) }
		}
		
		metaClass.getErrors = {
			->
			def errors
			def key = "org.codehaus.groovy.grails.ERRORS_${delegate.class.name}_${System.identityHashCode(delegate)}"
			errors = get(key)
			if (!errors) {
				errors = new BeanPropertyBindingResult(delegate, delegate.getClass().getName())
				put key, errors
			}
			errors
		}
		metaClass.setErrors = {Errors errors ->
			def key = "org.codehaus.groovy.grails.ERRORS_${delegate.class.name}_${System.identityHashCode(delegate)}"
			put key, errors
		}
		metaClass.clearErrors = {
			->
			delegate.setErrors(new BeanPropertyBindingResult(delegate, delegate.getClass().getName()))
		}
		
		def validationClosure = GCU.getStaticPropertyValue(validateableClass, 'constraints')
		def validateable = validateableClass.newInstance()
		if (validationClosure) {
			def constrainedPropertyBuilder = new ConstrainedPropertyBuilder(validateable)
			validationClosure.setDelegate(constrainedPropertyBuilder)
			validationClosure()
			metaClass.constraints = constrainedPropertyBuilder.constrainedProperties
		}
		else {
			metaClass.constraints = [:]
		}
		
		if (!metaClass.respondsTo(validateable, "validate")) {
			metaClass.validate = {
				->
				DomainClassPluginSupport.validateInstance(delegate, ctx)
			}
		}
	}
}
