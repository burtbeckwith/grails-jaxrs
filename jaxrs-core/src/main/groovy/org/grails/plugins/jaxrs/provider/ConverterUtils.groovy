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
package org.grails.plugins.jaxrs.provider

import grails.converters.JSON
import grails.converters.XML
import grails.core.GrailsApplication
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.databinding.xml.GPathResultMap
import org.grails.web.converters.configuration.ConvertersConfigurationHolder

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap

/**
 * Utility class for XML to map conversions.
 *
 * @author Martin Krasser
 */
@CompileStatic
@Slf4j
class ConverterUtils {
    /**
     * Returns character encoding settings for the given Grails application.
     *
     * @param application Grails application.
     * @return charset name.
     */
    static String getDefaultEncoding(GrailsApplication application) {
        // TODO: use platform encoding if application doesn't provide
        application.config.getProperty('grails.converters.encoding', String, 'UTF-8')
    }

    /**
     * Returns the default XML converter character encoding for the given
     * Grails application.
     */
    static String getDefaultXMLEncoding(GrailsApplication application) {
        def encoding = ConvertersConfigurationHolder.getConverterConfiguration(XML).encoding
        encoding ?: getDefaultEncoding(application)
    }

    /**
     * Returns the default JSON converter character encoding for the given
     * Grails application.
     */
    static String getDefaultJSONEncoding(GrailsApplication application) {
        def encoding = ConvertersConfigurationHolder.getConverterConfiguration(JSON).encoding
        encoding ?: getDefaultEncoding(application)
    }

    /** Extracts encoding from HTTP headers or MediaType parameters */
    static String getEncoding(MultivaluedMap httpHeaders, MediaType mediaType, String defaultEncoding) {
        // if HTTP headers specify the charset and this was parsed into the MediaType object, return the charset from MediaType
        // Jersey parses http headers into MediaType object
        if (mediaType.parameters['charset']) {
            return mediaType.parameters['charset']
        }

        // Restlet does not parse the headers into MediaType so we need to extract them manually
        def contentTypeHeaderKey = httpHeaders.keySet().find {  key -> ((String)key).toLowerCase() == 'content-type' }
        if (contentTypeHeaderKey) {
            String contentTypeHeaderValue = httpHeaders.getFirst(contentTypeHeaderKey)
            if (contentTypeHeaderValue.contains('charset=')) {
                return contentTypeHeaderValue.substring(contentTypeHeaderValue.indexOf('charset=') + 'charset='.length())
            }
        }

        // no encoding specified
        return defaultEncoding
    }

    static Map jsonToMap(InputStream input, String encoding) {
        (Map) new JsonSlurper().parse(new InputStreamReader(input, encoding))
    }

    /**
     * Reads an XML stream from <code>input</code> and converts it to a map
     * that can be used to construct a Grails domain objects (passing the map
     * to the domain object constructor).
     *
     * @param input XML input stream.
     * @param encoding charset name.
     * @return a map representing the input XML stream.
     */
    static Map xmlToMap(InputStream input, String encoding) {
        new GPathResultMap(new XmlSlurper(false, false).parse(new InputStreamReader(input, encoding)))
    }
}
