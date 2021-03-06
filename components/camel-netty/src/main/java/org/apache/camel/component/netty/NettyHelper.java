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
package org.apache.camel.component.netty;

import java.net.SocketAddress;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

/**
 * Helper class used internally by camel-netty using Netty.
 *
 * @version $Revision$
 */
public final class NettyHelper {

    private static final transient Log LOG = LogFactory.getLog(NettyHelper.class);

    private NettyHelper() {
        // Utility class
    }

    /**
     * Gets the string body to be used when sending with the textline codec.
     *
     * @param body                 the current body
     * @param exchange             the exchange
     * @param delimiter            the textline delimiter
     * @param autoAppendDelimiter  whether absent delimiter should be auto appended
     * @return the string body to send
     * @throws NoTypeConversionAvailableException is thrown if the current body could not be converted to a String type
     */
    public static String getTextlineBody(Object body, Exchange exchange, TextLineDelimiter delimiter, boolean autoAppendDelimiter) throws NoTypeConversionAvailableException {
        String s = exchange.getContext().getTypeConverter().mandatoryConvertTo(String.class, exchange, body);

        // auto append delimiter if missing?
        if (autoAppendDelimiter) {
            if (TextLineDelimiter.LINE.equals(delimiter)) {
                // line delimiter so ensure it ends with newline
                if (!s.endsWith("\n")) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Auto appending missing newline delimiter to body");
                    }
                    s = s + "\n";
                }
            } else {
                // null delimiter so ensure it ends with null
                if (!s.endsWith("\u0000")) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Auto appending missing null delimiter to body");
                    }
                    s = s + "\u0000";
                }
            }
        }

        return s;
    }

    /**
     * Writes the given body to Netty channel. Will wait until the body has been written.
     *
     * @param channel         the Netty channel
     * @param remoteAddress   the remote address when using UDP
     * @param body            the body to write (send)
     * @param exchange        the exchange
     * @throws CamelExchangeException is thrown if the body could not be written for some reasons
     *                                (eg remote connection is closed etc.)
     */
    public static void writeBodySync(Channel channel, SocketAddress remoteAddress, Object body, Exchange exchange) throws CamelExchangeException {
        // the write operation is asynchronous. Use future to wait until the session has been written
        ChannelFuture future;
        if (remoteAddress != null) {
            future = channel.write(body, remoteAddress);
        } else {
            future = channel.write(body);
        }

        // wait for the write
        if (LOG.isTraceEnabled()) {
            LOG.trace("Waiting for write to complete");
        }
        future.awaitUninterruptibly();

        // if it was not a success then thrown an exception
        if (!future.isSuccess()) {
            LOG.warn("Cannot write body: " + body + " using channel: " + channel);
            throw new CamelExchangeException("Cannot write body", exchange, future.getCause());
        }
    }

    /**
     * Closes the given channel
     *
     * @param channel the channel to close
     */
    public static void close(Channel channel) {
        if (channel != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Closing channel: " + channel);
            }
            channel.close().awaitUninterruptibly();
        }
    }

}
