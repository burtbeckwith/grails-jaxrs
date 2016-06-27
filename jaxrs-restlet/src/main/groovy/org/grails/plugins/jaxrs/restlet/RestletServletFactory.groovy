package org.grails.plugins.jaxrs.restlet

import groovy.transform.CompileStatic
import org.grails.plugins.jaxrs.core.JaxrsApplicationConfig
import org.grails.plugins.jaxrs.core.JaxrsServletConfig
import org.grails.plugins.jaxrs.servlet.ServletFactory

import javax.servlet.Servlet

/**
 * A servlet factory that handles the Restlet JAX-RS implementation.
 *
 * @author Bud Byrd
 */
@CompileStatic
class RestletServletFactory implements ServletFactory {
    /**
     * Create the servlet instance for the implementation-specific provider.
     *
     * @param applicationConfig
     * @param servletConfig
     * @return
     */
    @Override
    Servlet createServlet(JaxrsApplicationConfig applicationConfig, JaxrsServletConfig servletConfig) {
        return new RestletServlet(applicationConfig)
    }

    /**
     * Returns the fully qualified name of the class that will serve as the runtime delegate
     * for JAX-RS.
     *
     * @return
     */
    @Override
    String getRuntimeDelegateClassName() {
        return "org.restlet.ext.jaxrs.internal.spi.RuntimeDelegateImpl"
    }
}
