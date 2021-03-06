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
package org.apache.camel.processor;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * @version $Revision$
 */
public class SplitterStreamingUoWIssueTest extends ContextTestSupport {

    @Override
    protected void setUp() throws Exception {
        deleteDirectory("target/splitter");
        super.setUp();
    }

    public void testSplitterStreamingUoWIssue() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("A", "B", "C", "D", "E");
        getMockEndpoint("mock:result").expectedBodiesReceived("A,B,C,D,E");

        template.sendBodyAndHeader("file:target/splitter", "A,B,C,D,E", Exchange.FILE_NAME, "splitme.txt");

        assertMockEndpointsSatisfied();
    }

    public void testSplitterTwoFilesStreamingUoWIssue() throws Exception {
        getMockEndpoint("mock:foo").expectedBodiesReceived("A", "B", "C", "D", "E", "F", "G", "H", "I");
        getMockEndpoint("mock:result").expectedBodiesReceived("A,B,C,D,E", "F,G,H,I");

        template.sendBodyAndHeader("file:target/splitter", "A,B,C,D,E", Exchange.FILE_NAME, "a.txt");
        template.sendBodyAndHeader("file:target/splitter", "F,G,H,I", Exchange.FILE_NAME, "b.txt");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:target/splitter?delete=true&sortBy=file:name")
                    .split(body().tokenize(",")).streaming()
                        .to("seda:queue")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                Thread.sleep(200);
                            }
                        })
                    .end()
                    .log("End of file ${file:name}")
                    .to("mock:result");

                from("seda:queue")
                    .log("Token: ${body}")
                    .to("mock:foo");
            }
        };
    }
}
