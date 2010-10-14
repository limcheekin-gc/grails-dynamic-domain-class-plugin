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
package org.grails.dynamicdomain

import grails.test.*
import grails.util.GrailsNameUtils
import org.springframework.jdbc.core.JdbcTemplate


/**
 * Some codes are incorporated work from Burt at http://burtbeckwith.com/blog/?p=364
 *
 * @author <a href='mailto:limcheekin@vobject.com'>Lim Chee Kin</a>
 * @since 0.1
 *
 */
class DynamicDomainClassTests extends GrailsUnitTestCase {
	def grailsApplication
	def sessionFactory
	def dataSource
	def jdbcTemplate
	def tableName 
	DynamicDomainService dds = new DynamicDomainService()
	
	protected void setUp() {
		super.setUp()
		jdbcTemplate = new JdbcTemplate(dataSource)
		String.metaClass.underscore = {
			GrailsNameUtils.getNaturalName(delegate).replaceAll("\\s", "_").toLowerCase()
		}
		tableName = "VacationRequest".underscore()

	}
	
	protected void tearDown() {
		super.tearDown()
	}
	
	void testCreateDomainClassAndTable() {
		String code = """
package com.vobject

class VacationRequest implements Serializable {
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
    
  static mapping = {
	  table "$tableName"
    }    
}
"""
    dds.registerDomainClass code
	  dds.updateSessionFactory grailsApplication.mainContext
		assertEquals "$tableName exists", 0, getRowCount(tableName)
	}
	
	void testHasMany() { 
		String bookCode = """
		package com.foo.testapp.book
		
		class Book {
		   String title
		}
				"""			
		String authorCode = """
		package com.foo.testapp.author
		
		import com.foo.testapp.book.Book
		
		class Author {
		   static hasMany = [books: Book]
		   String name
		}
		"""						
    dds.registerDomainClass bookCode	
	  dds.registerDomainClass authorCode	
	  dds.updateSessionFactory grailsApplication.mainContext
    def Book = grailsApplication.getDomainClass('com.foo.testapp.book.Book')
    def Author = grailsApplication.getDomainClass('com.foo.testapp.author.Author')
    def author = Author.newInstance()
	  author.name = 'Stephen King'
	  def book1 = Book.newInstance()
	  book1.title = 'The Shining'
	  author.books = []
	  author.addToBooks(book1)
	  def book2 = Book.newInstance()
	  book2.title = 'Rose Madder'
	  author.addToBooks(book2)
    author.save(failOnError: true)
    /* error in test:
    No signature of method: org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass.newInstance() 
    is applicable for argument types: (java.util.LinkedHashMap) values: [[name:Stephen King]] Possible solutions: newInstance()
		def author = Author.newInstance(name: 'Stephen King')
		author.addToBooks(Book.newInstance(title: 'The Shining'))
		author.addToBooks(Book.newInstance(title: 'Rose Madder'))
		author.save(failOnError: true) 	
		*/  
       	
	}
	
	
	void testAddGormDynamicMethods() {
		assertEquals "$tableName exists", 0, getRowCount(tableName)
		def vacationRequestClass = grailsApplication.getDomainClass("com.vobject.VacationRequest")	  
		def vacationRequestInstance = vacationRequestClass.newInstance()
		vacationRequestInstance.numberOfDays = 1
		vacationRequestInstance.employeeName = "John"
		vacationRequestInstance.vacationDescription = "This is john vacation"
		vacationRequestInstance.save(flush:true, failOnError:true) 
		assertNotNull "vacationRequestInstance.id", vacationRequestInstance.id
		assertEquals "Record save into $tableName", 1, getRowCount(tableName)
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


