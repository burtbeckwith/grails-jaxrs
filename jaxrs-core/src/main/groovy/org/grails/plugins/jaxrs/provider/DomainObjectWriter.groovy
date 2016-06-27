/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.plugins.jaxrs.provider

import groovy.transform.CompileStatic

import javax.ws.rs.Produces
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider

/**
 * A concrete domain object writer that provides the same functionality as
 * {@link DomainObjectWriterSupport}. It can be disabled by setting
 * <code>org.grails.jaxrs.dowriter.disable</code> to <code>true</code> in the
 * application config. If this provider should only support resource methods
 * that declare a generic collection as return type, where the generic type
 * argument must be a Grails domain class, then set
 * <code>org.grails.jaxrs.dowriter.require.generic.collections</code> to
 * <code>true</code> in the application config.
 *
 * @author Martin Krasser
 */
@CompileStatic
@Provider
@Produces(['text/xml', 'application/xml', 'text/x-json', 'application/json'])
class DomainObjectWriter extends DomainObjectWriterSupport implements MessageBodyWriter<Object> {

    /*
     * NOTICE: a bug in Restlet requires to implement the MessageBodyWriter
     *         interface directly. It is not sufficient if it is implemented
     *         by the superclass (as of Restlet 2.0-M4).
     */

    protected boolean isEnabled() {
        !grailsApplication.config.getProperty('org.grails.jaxrs.dowriter.disable', Boolean, false)
    }

    protected boolean isRequireGenericCollection() {
        grailsApplication.config.getProperty('org.grails.jaxrs.dowriter.require.generic.collections', Boolean, false)
    }
}
