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

import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.web.pages.FastStringWriter

/**
 * Similar to DefaultGrailsDomainClassInjector, but inject to code directly.
 * 
 * @author <a href='mailto:limcheekin@vobject.com'>Lim Chee Kin</a>
 *
 * @since 0.2
 */
class GrailsDomainClassCodeInjector {
	private static final char NEW_LINE = '\n'
	public String performInjection(String code) {
		Writer writer = new FastStringWriter(code.length()) 
		new StringReader(code).eachLine { line -> 
			if (!injectIdVersionPropertyAndToStringMethod(writer, line, code) && 
			!injectAssociations(writer, line)) {
				writeLine writer, line
			}
		}
		return writer.toString()
	}
	
	private void writeLine(Writer writer, String line) {
		writer.write line
		writer.write NEW_LINE
	}
	
	private boolean injectIdVersionPropertyAndToStringMethod(Writer writer, String line, String code) {
		final boolean isClass = line.indexOf("class") > -1
		if (isClass) {
			final boolean hasId = code.indexOf(GrailsDomainClassProperty.IDENTITY) > -1
			final boolean hasVersion = code.indexOf(GrailsDomainClassProperty.VERSION) > -1
			final boolean hasToString = code.indexOf("String toString()") > -1
			writeLine writer, line  
			if (!hasId) {
				writeLine writer, "Long ${GrailsDomainClassProperty.IDENTITY}"
			}        
			if (!hasVersion) {
				writeLine writer, "Long ${GrailsDomainClassProperty.VERSION}"
			}
			if (!hasToString) {
				writeLine writer, "public String toString() { return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this) }"
			}
		}
		return isClass
	}		
	
	private boolean injectAssociations(Writer writer, String line) {
		String[] keyValue
		String variable
		String type
		
		final boolean isHasManyProperty = line.indexOf(GrailsDomainClassProperty.RELATES_TO_MANY) > -1 ||
				line.indexOf(GrailsDomainClassProperty.HAS_MANY) > -1	
		if (isHasManyProperty) {
			line.substring(line.indexOf('[')+1 ,line.indexOf(']')).split(',').each { mapEntry ->
				keyValue = mapEntry.split(':')
				variable = keyValue[0].stripIndent()
				type = keyValue[1].stripIndent() 
				writeLine writer, "Set<$type> $variable"
			}
		}
		
		final boolean isBelongsTo = line.indexOf(GrailsDomainClassProperty.BELONGS_TO) > -1 || 
				line.indexOf(GrailsDomainClassProperty.HAS_ONE) > -1
		final boolean isMap = line.indexOf('[') > -1 && line.indexOf(']') > -1 && line.indexOf(':') > -1		              
		if (isBelongsTo && isMap) {
			line.substring(line.indexOf('[')+1 ,line.indexOf(']')).split(',').each { mapEntry ->
				keyValue = mapEntry.split(':')
				variable = keyValue[0].stripIndent()
				type = keyValue[1].stripIndent() 
				writeLine writer, "$type $variable"
			}
		}
		return isHasManyProperty || isBelongsTo
	}
}
