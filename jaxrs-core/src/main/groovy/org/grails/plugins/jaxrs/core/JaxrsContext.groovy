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
package org.grails.plugins.jaxrs.core

import groovy.transform.CompileStatic
import org.grails.plugins.jaxrs.servlet.ServletFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.util.Assert

import javax.servlet.Servlet
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.ext.RuntimeDelegate

/**
 * The JAX-RS context used by applications to interact with a JAX-RS
 * implementation.
 *
 * @author Martin Krasser
 * @author David Castro
 * @author Bud Byrd
 */
@CompileStatic
class JaxrsContext implements InitializingBean {
    /**
     * Name of the JAX-RS servlet.
     */
    static final String SERVLET_NAME = "org.grails.jaxrs.servlet.name"

    /**
     * Logger.
     */
    Logger log = LoggerFactory.getLogger(JaxrsContext)

    /**
     * Instance of the servlet handling JAX-RS requests.
     */
    volatile Servlet jaxrsServlet

    /**
     * Application's servlet context to use with the JAX-RS servlet.
     */
    volatile ServletContext servletContext

    /**
     * JAX-RS application configuration.
     */
    volatile JaxrsApplicationConfig applicationConfig = new JaxrsApplicationConfig()

    /**
     * Initialization parameters to pass to the JAX-RS implementation.
     */
    volatile Map<String, String> providerInitParameters = [:]

    /**
     * Servlet factory responsible for providing an implementation-specific JAX-RS provider.
     */
    ServletFactory jaxrsServletFactory

    /**
     * Reloads the JAX-RS configuration defined by Grails applications and
     * re-initializes the JAX-RS runtime.
     *
     * @throws ServletException
     * @throws IOException
     */
    void restart() throws ServletException, IOException {
        destroy()
        init()
    }

    /**
     * Initializes the JAX-RS runtime.
     *
     * @throws ServletException
     */
    void init() throws ServletException {
        Assert.isNull jaxrsServlet, "can not start the JAX-RS servlet because has already been started"

        RuntimeDelegate.setInstance(null)

        System.setProperty(RuntimeDelegate.JAXRS_RUNTIME_DELEGATE_PROPERTY, jaxrsServletFactory.getRuntimeDelegateClassName())

        JaxrsServletConfig servletConfig = createServletConfig()
        jaxrsServlet = jaxrsServletFactory.createServlet(applicationConfig, servletConfig)
        jaxrsServlet.init(servletConfig)
    }

    /**
     * Create the servlet configuration.
     *
     * @return
     */
    JaxrsServletConfig createServletConfig() {
        return new JaxrsServletConfig(
            servletContext,
            SERVLET_NAME,
            new Hashtable<String, String>(providerInitParameters)
        )
    }

    /**
     * Destroys and removes the current JAX-RS servlet, if one exists.
     */
    void destroy() {
        if (jaxrsServlet != null) {
            jaxrsServlet.destroy()
            jaxrsServlet = null
        }
    }

    /**
     * Processes an incoming request through the JAX-RS servlet.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Assert.notNull jaxrsServlet, "can not service a JAX-RS request because no servlet has been started"
        jaxrsServlet.service(request, response)
    }

    @Override
    void afterPropertiesSet() {
        Assert.notNull jaxrsServletFactory, "the 'jaxrsServletFactory' Spring bean is not set"
    }
}
