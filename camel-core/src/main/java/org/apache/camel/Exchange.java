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
package org.apache.camel;

import java.util.List;
import java.util.Map;

import org.apache.camel.spi.Synchronization;
import org.apache.camel.spi.UnitOfWork;

/**
 * Exchange is the message container holding the information during the entire routing.
 * <p/><br/>
 * Most noticeable the {@link Exchange} provides access to the request, response and
 * fault {@link Message} messages.
 * <p/><br/>
 * It also provides information about what is known as the
 * {@link ExchangePattern Message Exchange Pattern} (or MEP for short). Its a status
 * which represents if the message is a <i>one-way</i> or <i>request-reply</i> exchange pattern.
 * A <i>one-way</i> pattern is defined as the {@link org.apache.camel.ExchangePattern#InOnly}
 * adn the <i>request-reply</i> is the {@link org.apache.camel.ExchangePattern#InOut}.
 * Those two patterns are the most commonly used. The other patterns is hardly used at all.
 * <p/><br/>
 * The {@link Exchange} also holds meta-data during the entire lifecycle of the exchange which
 * are stored as properties, accessible using the various {@link #getProperty(String)} method
 * and the {@link #setProperty(String, Object)} is used to store a property. For example you
 * can use this to store security related information. Also Camel itself stores information
 * as properties, like some of the <a href="http://camel.apache.org/enterprise-integration-patterns.html">
 * Enterprise Integration Patterns</a> does.
 * <p/><br/>
 * If an {@link Exchange} failed during routing the caused {@link Exception} is stored and accessible
 * using the {@link #getException()} method.
 * <p/><br/>
 * <b>Important:</b>
 * As a Camel end user and you want to update the current {@link Message} on the {@link Exchange} then
 * you should pay attention. There are two methods two retrieve the message named as {@link #getIn()}
 * and {@link #getOut()}. This API has been there since Camel 1.0. You most often should only use
 * the {@link #getIn()} method and change the current message by updating it directly. By doing this
 * you ensure all the information is kept for further processing. On the other hand if you use the
 * {@link #getOut()} method to update the {@link Message} then a <b>new</b> message is created which
 * means any headers, attachments or the likes from the {@link #getIn()} {@link Message} is lost.
 * The {@link #getOut()} method is often only used in special cases or internally by Camel or specific
 * Camel components.
 * <br/>
 * See also this <a href="http://camel.apache.org/using-getin-or-getout-methods-on-exchange.html">FAQ entry</a> for more details.
 *
 * @version $Revision$
 */
public interface Exchange {

    String AUTHENTICATION                   = "CamelAuthentication";
    String AUTHENTICATION_FAILURE_POLICY_ID = "CamelAuthenticationFailurePolicyId";
    String ACCEPT_CONTENT_TYPE              = "CamelAcceptContentType";
    String AGGREGATED_SIZE                  = "CamelAggregatedSize";
    String AGGREGATED_COMPLETED_BY          = "CamelAggregatedCompletedBy";
    String AGGREGATED_CORRELATION_KEY       = "CamelAggregatedCorrelationKey";
    String AGGREGATION_STRATEGY             = "CamelAggregationStrategy";
    String ASYNC_WAIT                       = "CamelAsyncWait";

    String BATCH_INDEX                = "CamelBatchIndex";
    String BATCH_SIZE                 = "CamelBatchSize";
    String BATCH_COMPLETE             = "CamelBatchComplete";
    String BEAN_METHOD_NAME           = "CamelBeanMethodName";
    String BEAN_MULTI_PARAMETER_ARRAY = "CamelBeanMultiParameterArray";
    String BINDING                    = "CamelBinding";

    String CHARSET_NAME     = "CamelCharsetName";
    String CONTENT_ENCODING = "Content-Encoding";
    String CONTENT_TYPE     = "Content-Type";
    String CORRELATION_ID   = "CamelCorrelationId";

    String DATASET_INDEX             = "CamelDataSetIndex";
    String DEFAULT_CHARSET_PROPERTY  = "org.apache.camel.default.charset";
    String DISABLE_HTTP_STREAM_CACHE = "CamelDisableHttpStreamCache";

    String EXCEPTION_CAUGHT     = "CamelExceptionCaught";
    String ERRORHANDLER_HANDLED = "CamelErrorHandlerHandled";

    String FAILURE_HANDLED      = "CamelFailureHandled";
    String FAILURE_ENDPOINT     = "CamelFailureEndpoint";
    String FILTER_NON_XML_CHARS = "CamelFilterNonXmlChars";
    String FILE_LOCAL_WORK_PATH = "CamelFileLocalWorkPath";
    String FILE_NAME            = "CamelFileName";
    String FILE_NAME_ONLY       = "CamelFileNameOnly";
    String FILE_NAME_PRODUCED   = "CamelFileNameProduced";
    String FILE_PATH            = "CamelFilePath";
    String FILE_PARENT          = "CamelFileParent";
    String FILE_LAST_MODIFIED   = "CamelFileLastModified";
    String FILTER_MATCHED       = "CamelFilterMatched";

    String GROUPED_EXCHANGE = "CamelGroupedExchange";
    
    String HTTP_BASE_URI           = "CamelHttpBaseUri";
    String HTTP_CHARACTER_ENCODING = "CamelHttpCharacterEncoding";
    String HTTP_METHOD             = "CamelHttpMethod";
    String HTTP_PATH               = "CamelHttpPath";
    String HTTP_PROTOCOL_VERSION   = "CamelHttpProtocolVersion";
    String HTTP_QUERY              = "CamelHttpQuery";
    String HTTP_RESPONSE_CODE      = "CamelHttpResponseCode";
    String HTTP_URI                = "CamelHttpUri";
    String HTTP_URL                = "CamelHttpUrl";
    String HTTP_CHUNKED            = "CamelHttpChunked";
    String HTTP_SERVLET_REQUEST    = "CamelHttpServletRequest";
    String HTTP_SERVLET_RESPONSE   = "CamelHttpServletResponse";

    String INTERCEPTED_ENDPOINT = "CamelInterceptedEndpoint";

    String LANGUAGE_SCRIPT          = "CamelLanguageScript";
    String LOG_DEBUG_BODY_MAX_CHARS = "CamelLogDebugBodyMaxChars";
    String LOG_DEBUG_BODY_STREAMS   = "CamelLogDebugStreams";
    String LOOP_INDEX               = "CamelLoopIndex";
    String LOOP_SIZE                = "CamelLoopSize";

    String MAXIMUM_CACHE_POOL_SIZE = "CamelMaximumCachePoolSize";
    String MULTICAST_INDEX         = "CamelMulticastIndex";
    String MULTICAST_COMPLETE      = "CamelMulticastComplete";

    String ON_COMPLETION = "CamelOnCompletion";

    String REDELIVERED          = "CamelRedelivered";
    String REDELIVERY_COUNTER   = "CamelRedeliveryCounter";
    String REDELIVERY_EXHAUSTED = "CamelRedeliveryExhausted";
    String ROLLBACK_ONLY        = "CamelRollbackOnly";
    String ROLLBACK_ONLY_LAST   = "CamelRollbackOnlyLast";
    String ROUTE_STOP           = "CamelRouteStop";

    String SOAP_ACTION        = "CamelSoapAction";
    String SKIP_GZIP_ENCODING = "CamelSkipGzipEncoding";
    String SLIP_ENDPOINT      = "CamelSlipEndpoint";
    String SPLIT_INDEX        = "CamelSplitIndex";
    String SPLIT_COMPLETE     = "CamelSplitComplete";
    String SPLIT_SIZE         = "CamelSplitSize";

    String TIMER_FIRED_TIME      = "CamelTimerFiredTime";
    String TIMER_NAME            = "CamelTimerName";
    String TIMER_PERIOD          = "CamelTimerPeriod";
    String TIMER_TIME            = "CamelTimerTime";
    String TO_ENDPOINT           = "CamelToEndpoint";
    String TRACE_EVENT           = "CamelTraceEvent";
    String TRACE_EVENT_NODE_ID   = "CamelTraceEventNodeId";
    String TRACE_EVENT_TIMESTAMP = "CamelTraceEventTimestamp";
    String TRACE_EVENT_EXCHANGE  = "CamelTraceEventExchange";
    String TRANSFER_ENCODING     = "Transfer-Encoding";

    String XSLT_FILE_NAME = "CamelXsltFileName";

    /**
     * Returns the {@link ExchangePattern} (MEP) of this exchange.
     *
     * @return the message exchange pattern of this exchange
     */
    ExchangePattern getPattern();

    /**
     * Allows the {@link ExchangePattern} (MEP) of this exchange to be customized.
     *
     * This typically won't be required as an exchange can be created with a specific MEP
     * by calling {@link Endpoint#createExchange(ExchangePattern)} but it is here just in case
     * it is needed.
     *
     * @param pattern  the pattern 
     */
    void setPattern(ExchangePattern pattern);

    /**
     * Returns a property associated with this exchange by name
     *
     * @param name the name of the property
     * @return the value of the given property or <tt>null</tt> if there is no property for
     *         the given name
     */
    Object getProperty(String name);

    /**
     * Returns a property associated with this exchange by name
     *
     * @param name the name of the property
     * @param defaultValue the default value to return if property was absent
     * @return the value of the given property or <tt>defaultValue</tt> if there is no
     *         property for the given name
     */
    Object getProperty(String name, Object defaultValue);

    /**
     * Returns a property associated with this exchange by name and specifying
     * the type required
     *
     * @param name the name of the property
     * @param type the type of the property
     * @return the value of the given property or <tt>null</tt> if there is no property for
     *         the given name or <tt>null</tt> if it cannot be converted to the given type
     */
    <T> T getProperty(String name, Class<T> type);

    /**
     * Returns a property associated with this exchange by name and specifying
     * the type required
     *
     * @param name the name of the property
     * @param defaultValue the default value to return if property was absent
     * @param type the type of the property
     * @return the value of the given property or <tt>defaultValue</tt> if there is no property for
     *         the given name or <tt>null</tt> if it cannot be converted to the given type
     */
    <T> T getProperty(String name, Object defaultValue, Class<T> type);

    /**
     * Sets a property on the exchange
     *
     * @param name  of the property
     * @param value to associate with the name
     */
    void setProperty(String name, Object value);

    /**
     * Removes the given property on the exchange
     *
     * @param name of the property
     * @return the old value of the property
     */
    Object removeProperty(String name);

    /**
     * Returns all of the properties associated with the exchange
     *
     * @return all the headers in a Map
     */
    Map<String, Object> getProperties();

    /**
     * Returns whether any properties has been set
     *
     * @return <tt>true</tt> if any properties has been set
     */
    boolean hasProperties();

    /**
     * Returns the inbound request message
     *
     * @return the message
     */
    Message getIn();

    /**
     * Returns the inbound request message as the given type
     *
     * @param type the given type
     * @return the message as the given type or <tt>null</tt> if not possible to covert to given type
     */
    <T> T getIn(Class<T> type);

    /**
     * Sets the inbound message instance
     *
     * @param in the inbound message
     */
    void setIn(Message in);

    /**
     * Returns the outbound message, lazily creating one if one has not already
     * been associated with this exchange.
     * <p/>
     * <br/><b>Important:</b> If you want to change the current message, then use {@link #getIn()} instead as it will
     * ensure headers etc. is kept and propagated when routing continues. Bottom line end users should rarely use
     * this method.
     * <p/>
     * <br/>If you want to test whether an OUT message have been set or not, use the {@link #hasOut()} method.
     * <p/>
     * See also the class java doc for this {@link Exchange} for more details.
     *
     * @return the response
     * @see #getIn()
     */
    Message getOut();

    /**
     * Returns the outbound request message as the given type
     * <p/>
     * <br/><b>Important:</b> If you want to change the current message, then use {@link #getIn(Class)} instead as it will
     * ensure headers etc. is kept and propagated when routing continues. Bottom line end users should rarely use
     * this method.
     * <p/>
     * See also the class java doc for this {@link Exchange} for more details.
     *
     * @param type the given type
     * @return the message as the given type or <tt>null</tt> if not possible to covert to given type
     * @see #getIn(Class)
     */
    <T> T getOut(Class<T> type);

    /**
     * Returns whether an OUT message has been set or not.
     *
     * @return <tt>true</tt> if an OUT message exists, <tt>false</tt> otherwise.
     */
    boolean hasOut();

    /**
     * Sets the outbound message
     *
     * @param out the outbound message
     */
    void setOut(Message out);

    /**
     * Returns the exception associated with this exchange
     *
     * @return the exception (or null if no faults)
     */
    Exception getException();

    /**
     * Returns the exception associated with this exchange.
     * <p/>
     * Is used to get the caused exception that typically have been wrapped in some sort
     * of Camel wrapper exception
     * <p/>
     * The strategy is to look in the exception hierarchy to find the first given cause that matches the type.
     * Will start from the bottom (the real cause) and walk upwards.
     *
     * @param type the exception type
     * @return the exception (or <tt>null</tt> if no caused exception matched)
     */
    <T> T getException(Class<T> type);

    /**
     * Sets the exception associated with this exchange
     * <p/>
     * Camel will wrap {@link Throwable} into {@link Exception} type to
     * accommodate for the {@link #getException()} method returning a plain {@link Exception} type.
     *
     * @param t  the caused exception
     */
    void setException(Throwable t);

    /**
     * Returns true if this exchange failed due to either an exception or fault
     *
     * @return true if this exchange failed due to either an exception or fault
     * @see Exchange#getException()
     * @see Message#setFault(boolean)
     * @see Message#isFault()
     */
    boolean isFailed();

    /**
     * Returns true if this exchange is transacted
     */
    boolean isTransacted();

    /**
     * Returns true if this exchange is marked for rollback
     */
    boolean isRollbackOnly();

    /**
     * Returns the container so that a processor can resolve endpoints from URIs
     *
     * @return the container which owns this exchange
     */
    CamelContext getContext();

    /**
     * Creates a copy of the current message exchange so that it can be
     * forwarded to another destination
     */
    Exchange copy();

    /**
     * Returns the endpoint which originated this message exchange if a consumer on an endpoint
     * created the message exchange, otherwise this property will be <tt>null</tt>
     */
    Endpoint getFromEndpoint();

    /**
     * Sets the endpoint which originated this message exchange. This method
     * should typically only be called by {@link org.apache.camel.Endpoint} implementations
     *
     * @param fromEndpoint the endpoint which is originating this message exchange
     */
    void setFromEndpoint(Endpoint fromEndpoint);
    
    /**
     * Returns the route id which originated this message exchange if a route consumer on an endpoint
     * created the message exchange, otherwise this property will be <tt>null</tt>
     */
    String getFromRouteId();

    /**
     * Sets the route id which originated this message exchange. This method
     * should typically only be called by the internal framework.
     *
     * @param fromRouteId the from route id
     */
    void setFromRouteId(String fromRouteId);

    /**
     * Returns the unit of work that this exchange belongs to; which may map to
     * zero, one or more physical transactions
     */
    UnitOfWork getUnitOfWork();

    /**
     * Sets the unit of work that this exchange belongs to; which may map to
     * zero, one or more physical transactions
     */
    void setUnitOfWork(UnitOfWork unitOfWork);

    /**
     * Returns the exchange id (unique)
     */
    String getExchangeId();

    /**
     * Set the exchange id
     */
    void setExchangeId(String id);

    /**
     * Adds a {@link org.apache.camel.spi.Synchronization} to be invoked as callback when
     * this exchange is completed.
     *
     * @param onCompletion  the callback to invoke on completion of this exchange
     */
    void addOnCompletion(Synchronization onCompletion);

    /**
     * Handover all the on completions from this exchange to the target exchange.
     *
     * @param target the target exchange
     */
    void handoverCompletions(Exchange target);

    /**
     * Handover all the on completions from this exchange
     *
     * @return the on completions
     */
    List<Synchronization> handoverCompletions();

}
