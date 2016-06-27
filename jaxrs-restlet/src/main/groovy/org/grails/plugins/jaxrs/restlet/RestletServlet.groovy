/*
 * Copyright 2009, 2016 the original author or authors.
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
package org.grails.plugins.jaxrs.restlet

import groovy.transform.CompileStatic
import org.grails.plugins.jaxrs.core.JaxrsApplicationConfig
import org.restlet.Application
import org.restlet.Context
import org.restlet.ext.jaxrs.JaxRsApplication
import org.restlet.ext.servlet.ServerServlet

/**
 * Servlet that dispatches JAX-RS requests to Restlet.
 *
 * @author Martin Krasser
 */
@CompileStatic
class RestletServlet extends ServerServlet {
    /**
     * JAX-RS Application.
     */
    JaxrsApplicationConfig applicationConfig

    /**
     * Creates a new {@link RestletServlet}
     *
     * @param applicationConfig JAX-RS configuration of the current {@link org.grails.plugins.jaxrs.core.JaxrsContext}.
     */
    RestletServlet(JaxrsApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig
    }

    /**
     * Destroys this servlet removing all Restlet-specific attributes from the
     * servlet context.
     */
    @Override
    void destroy() {
        getServletContext().getAttributeNames().find { String it ->
            it.startsWith('org.restlet')
        }.each { String it ->
            getServletContext().removeAttribute(it)
        }

        super.destroy()
    }

    /**
     * Creates a {@link JaxRsApplication} for the given Restlet parent context.
     * A custom object factory is provided to lookup JAX-RS resource and
     * provider objects from the Spring web application context.
     *
     * @param Restlet parent context.
     * @return a new {@link JaxRsApplication} instance.
     */
    @Override
    protected Application createApplication(Context parentContext) {
        JaxRsApplication jaxRsApplication = new JaxRsApplication(parentContext.createChildContext())
        jaxRsApplication.setObjectFactory(new ApplicationContextObjectFactory(getServletContext()))
        jaxRsApplication.add(applicationConfig)
        return jaxRsApplication
    }
}
