/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.http4.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.util.IOHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

/**
 *
 * @version $Revision$
 */
public class BasicValidationHandler implements HttpRequestHandler {

    protected String expectedMethod;
    protected String expectedQuery;
    protected Object expectedContent;
    protected String responseContent;

    public BasicValidationHandler(String expectedMethod, String expectedQuery,
                                  Object expectedContent, String responseContent) {
        this.expectedMethod = expectedMethod;
        this.expectedQuery = expectedQuery;
        this.expectedContent = expectedContent;
        this.responseContent = responseContent;
    }

    public void handle(final HttpRequest request, final HttpResponse response,
                       final HttpContext context) throws HttpException, IOException {

        if (expectedMethod != null && !expectedMethod.equals(request.getRequestLine().getMethod())) {
            response.setStatusCode(HttpStatus.SC_METHOD_FAILURE);
            return;
        }

        try {
            String query = new URI(request.getRequestLine().getUri()).getQuery();            
            if (expectedQuery != null && !expectedQuery.equals(query)) {
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }
        } catch (URISyntaxException e) {
            throw IOHelper.createIOException(e);
        }

        if (expectedContent != null) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
            String content = EntityUtils.toString(entity);

            if (!expectedContent.equals(content)) {
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }
        }

        response.setStatusCode(HttpStatus.SC_OK);
        if (responseContent != null) {
            response.setEntity(new StringEntity(responseContent, HTTP.ASCII));
        }
    }

}