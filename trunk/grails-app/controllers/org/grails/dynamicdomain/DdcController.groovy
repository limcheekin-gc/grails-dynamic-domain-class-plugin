package org.grails.dynamicdomain

class DdcController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    static final List DOMAIN_CLASS_SYSTEM_FIELDS = ["id", "version", "dateCreated", "lastUpdated"]
    
	def index = {
        redirect(action: "list", params: params)
    }

    def list = {
		    def domainClass = grailsApplication.getDomainClass(params.dc)
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
		domainClass.clazz.list(params).each {
			print it
		}
        [domainInstanceList: domainClass.clazz.list(params), 
			   domainInstanceTotal: domainClass.clazz.count(),
			   domainClass: domainClass]
    }

    def create = {
 		    def domainClass = grailsApplication.getDomainClass(params.dc)
        def domainInstance = domainClass.newInstance()
        //TODO domainInstance.properties = params
        return [domainInstance: domainInstance, domainClass:domainClass, 
			          multiPart:false] // multiPart:true if form have upload component
    }

    def save = {
  		  def domainClass = grailsApplication.getDomainClass(params.dc)
        def domainInstance = domainClass.newInstance()   
		   [domainClass.properties*.name.find {!(it in DOMAIN_CLASS_SYSTEM_FIELDS)}].flatten().each { field -> 
					 if (params[field]) {
					    domainInstance."${field}" = params[field]
					  }
			  }
        if (domainInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}'), domainInstance.id])}"
            redirect(action: "show", id: domainInstance.id, params:[dc:params.dc])
        }
        else {
            render(view: "create", model: [domainInstance: domainInstance, domainClass: domainClass])
        }
    }

    def show = {
  		  def domainClass = grailsApplication.getDomainClass(params.dc) 
        def domainInstance = domainClass.clazz.get(params.id)
        if (!domainInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}'), params.id])}"
            redirect(action: "list", params:[dc:params.dc])
        }
        else {
            [domainInstance: domainInstance, domainClass: domainClass]
        }
    }

    def edit = {
  		  def domainClass = grailsApplication.getDomainClass(params.dc) 
        def domainInstance = domainClass.clazz.get(params.id)
        if (!domainInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}'), params.id])}"
            redirect(action: "list", params:[dc:params.dc])
        }
        else {
            return [domainInstance: domainInstance, domainClass: domainClass]
        }
    }

    def update = {
  		  def domainClass = grailsApplication.getDomainClass(params.dc) 
        def domainInstance = domainClass.clazz.get(params.id)
        if (domainInstance) { 
            if (params.version) {
                def version = params.version.toLong()
                if (domainInstance.version > version) {
                    domainInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}')] as Object[], "Another user has updated this ${domainClass.propertyName} while you were editing")
                    render(view: "edit", model: [domainInstance: domainInstance, domainClass: domainClass])
                    return
                }
            }
            domainInstance.properties = params
            if (!domainInstance.hasErrors() && domainInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}'), domainInstance.id])}"
                redirect(action: "show", id: domainInstance.id, params:[dc:params.dc])
            }
            else {
                render(view: "edit", model: [domainInstance: domainInstance, domainClass: domainClass])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}'), params.id])}"
            redirect(action: "list", params:[dc:params.dc])
        }
    }

    def delete = {
  		  def domainClass = grailsApplication.getDomainClass(params.dc) 
        def domainInstance = domainClass.clazz.get(params.id)
        if (domainInstance) {
            try {
                domainInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}'), params.id])}"
                redirect(action: "list", params:[dc:params.dc])
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}'), params.id])}"
                redirect(action: "show", id: params.id, params:[dc:params.dc])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${domainClass.name}'), params.id])}"
            redirect(action: "list", params:[dc:params.dc])
        }
    }
    
}
