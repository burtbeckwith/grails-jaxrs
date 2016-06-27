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

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.MessageBodyWriter
import java.lang.annotation.Annotation
import java.lang.reflect.Type

/**
 * Base class for simple message body writers.
 *
 * @param < T >  type of object to be written to the response entity.
 * @author Martin Krasser
 */
@CompileStatic
abstract class MessageBodyWriterSupport<T> extends ProviderSupport implements MessageBodyWriter<T> {

    MessageBodyWriterSupport() {
        setTypeArgument(ProviderUtils.getWriterTypeArgument(this))
    }

    /**
     * Always returns <code>-1</code>.
     */
    long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1
    }

    /**
     * @see #isSupported(Class, Type, Annotation [ ], MediaType)
     */
    boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isSupported(type, genericType, annotations, mediaType)
    }

    void writeTo(T t, Class<?> type, Type genericType,
                 Annotation[] annotations, MediaType mediaType,
                 MultivaluedMap<String, Object> httpHeaders,
                 OutputStream entityStream) throws IOException,
        WebApplicationException {
        writeTo(t, httpHeaders, entityStream)
    }

    /**
     * Writes an object of type given by this class type parameter to the
     * response entity output stream.
     *
     * @param t object to be written.
     * @param httpHeaders HTTP response headers.
     * @param entityStream response entity output stream.
     * @throws IOException
     * @throws WebApplicationException
     */
    protected abstract void writeTo(T t, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
        throws IOException, WebApplicationException
}
