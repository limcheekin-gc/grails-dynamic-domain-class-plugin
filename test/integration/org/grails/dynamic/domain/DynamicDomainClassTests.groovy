package org.grails.dynamic.domain


import grails.test.*
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import grails.util.GrailsNameUtils


class DynamicDomainClassTests extends GrailsUnitTestCase {
	def grailsApplication
	def sessionFactory
	def configurableLocalSessionFactoryBean
	def dataSource
	def jdbcTemplate
	def tableName 
	
	protected void setUp() {
		super.setUp()
		configurableLocalSessionFactoryBean = grailsApplication.mainContext.getBean("&sessionFactory")
		String.metaClass.underscore = {
			GrailsNameUtils.getNaturalName(delegate).replaceAll("\\s", "_").toLowerCase()
		}
		tableName = "VacationRequest".underscore()
	}
	
	protected void tearDown() {
		super.tearDown()
	}

/*import org.codehaus.groovy.grails.validation.Validateable

@Validateable*/		
	private loadEntityClass(String tableName) {
		String code = """
package com.vobject

import javax.persistence.*

@Entity(name="$tableName")
class VacationRequest implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id
  @Version
  Long version  
	String employeeName
	Integer numberOfDays
	String vacationDescription
	String approvalRemark 
	Boolean resendRequest
  Date dateCreated
  Date lastUpdated
  	
  static constraints = {
		employeeName blank:false, size:5..50
		numberOfDays range:1..14
		vacationDescription blank:false, size:5..255
		approvalRemark nullable:true
		resendRequest nullable:true
		dateCreated blank:false
    lastUpdated nullable:true		
    }
}
"""
		return grailsApplication.classLoader.parseClass(code)
	}
	
	void testCreateDomainClassAndTable() {
		/* def gcl = new GrailsAwareClassLoader()
		 def injector = new DefaultGrailsDomainClassInjector() {
		 boolean shouldInject(URL url) { true }
		 protected boolean isDomainClass(ClassNode classNode, SourceUnit sourceNode) { true  }
		 protected boolean shouldInjectClass(ClassNode classNode) { true }
		 }
		 gcl.setClassInjectors([injector] as ClassInjector[])
		 */ 
		def entityClass = loadEntityClass(tableName)
		GrailsDomainClass domainClass = grailsApplication.addArtefact(DomainClassArtefactHandler.TYPE, entityClass)
		assert (configurableLocalSessionFactoryBean.configuration instanceof org.hibernate.cfg.AnnotationConfiguration)
		configurableLocalSessionFactoryBean.configuration.addAnnotatedClass(entityClass)
		configurableLocalSessionFactoryBean.updateDatabaseSchema()
		assertEquals "$tableName exists", 0, getRowCount(tableName)
	}
	
	void testAddGormDynamicMethods() {
		assertEquals "$tableName exists", 0, getRowCount(tableName)
		HibernateSupport.enhanceSessionFactory(sessionFactory, grailsApplication, grailsApplication.mainContext)			  
		def vacationRequestClass = grailsApplication.getDomainClass("com.vobject.VacationRequest")
		def vacationRequestInstance = vacationRequestClass.newInstance()
		vacationRequestInstance.numberOfDays = 1
		vacationRequestInstance.employeeName = "John"
		vacationRequestInstance.save(flush:true, failOnError:true) 
		assertNotNull "vacationRequestInstance.id", vacationRequestInstance.id
		assertEquals "Record save into $tableName", 1, getRowCount(tableName)
	} 
	
	void testAddGormValidations() { 
		 assertEquals "$tableName exists", 0, getRowCount(tableName)
		 def vacationRequestClass = grailsApplication.getDomainClass("com.vobject.VacationRequest")
		 def vacationRequestInstance = vacationRequestClass.newInstance()
		 try {
		 ValidationSupport.addDynamicMethods(grailsApplication, vacationRequestInstance, grailsApplication.mainContext)
		 assertFalse "vacationRequestInstance.validate()", vacationRequestInstance.validate()
		 } catch (Exception e) {
		   println e
		   throw e
		 }
	}  
	
	private listAllBeanNames() {
		println "BEAN NAMES:"
		grailsApplication.mainContext.beanDefinitionNames.each { println it }
	}
	
	private listAllDomainClasses() {
		println "DOMAIN CLASSES:"
		grailsApplication.domainClasses?.each { println it }
	}	
	
	private listAllHibernateProperties() {
		println "HIBERNATE PROPERTIES:"
		grailsApplication.mainContext.hibernateProperties.each { println it }
	}
	
	private getRowCount(def tableName) {
		return jdbcTemplate.queryForLong("select count(*) from ${tableName}")
	}
	
	private dropTable(def tableName) {
		jdbcTemplate.execute("drop table ${tableName}")
	}
	
	
}
