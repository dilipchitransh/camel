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

import java.util.concurrent.TimeUnit;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A <code>SamplingThrottler</code> is a special kind of throttler. It also
 * limits the number of exchanges sent to a downstream endpoint. It differs from
 * a normal throttler in that it will not queue exchanges above the threshold
 * for a given period. Instead these exchanges will be stopped, precluding them
 * from being processed at all by downstream consumers.
 * <p/>
 * This kind of throttling can be useful for taking a sample from
 * an exchange stream, rough consolidation of noisy and bursty exchange traffic
 * or where queuing of throttled exchanges is undesirable.
 *
 * @version $Revision$
 */
public class SamplingThrottler extends DelegateAsyncProcessor {

    protected final transient Log log = LogFactory.getLog(getClass());
    private long samplePeriod;
    private long periodInNanos;
    private TimeUnit units;
    private long timeOfLastExchange;
    private StopProcessor stopper = new StopProcessor();
    private Object calculationLock = new Object();
    private SampleStats sampled = new SampleStats();

    public SamplingThrottler(Processor processor, long samplePeriod, TimeUnit units) {
        super(processor);

        if (samplePeriod <= 0) {
            throw new IllegalArgumentException("A positive value is required for the sampling period");
        }
        if (units == null) {
            throw new IllegalArgumentException("A invalid null value was supplied for the units of the sampling period");
        }
        this.samplePeriod = samplePeriod;
        this.units = units;
        this.periodInNanos = units.toNanos(samplePeriod);
    }

    @Override
    public String toString() {
        return "SamplingThrottler[1 exchange per: " + samplePeriod + " " + units.toString().toLowerCase() + " -> " + getProcessor() + "]";
    }

    public String getTraceLabel() {
        return "samplingThrottler[1 exchange per: " + samplePeriod + " " + units.toString().toLowerCase() + "]";
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        boolean doSend = false;

        synchronized (calculationLock) {
            long now = System.nanoTime();
            if (now >= timeOfLastExchange + periodInNanos) {
                doSend = true;
                if (log.isTraceEnabled()) {
                    log.trace(sampled.sample());
                }
                timeOfLastExchange = now;
            } else {
                if (log.isTraceEnabled()) {
                    log.trace(sampled.drop());
                }
            }
        }

        if (doSend) {
            // continue routing
            return super.process(exchange, callback);
        } else {
            // okay to invoke this synchronously as the stopper
            // will just set a property
            try {
                stopper.process(exchange);
            } catch (Exception e) {
                exchange.setException(e);
            }
        }

        // we are done synchronously
        callback.done(true);
        return true;
    }

    private static class SampleStats {
        private long droppedThisPeriod;
        private long totalDropped;
        private long totalSampled;
        private long totalThisPeriod;

        String drop() {
            droppedThisPeriod++;
            totalThisPeriod++;
            totalDropped++;
            return getDroppedLog();
        }

        String sample() {
            totalThisPeriod = 1; // a new period, reset to 1
            totalSampled++;
            droppedThisPeriod = 0;
            return getSampledLog();
        }

        String getSampledLog() {
            return String.format("Sampled %d of %d total exchanges", totalSampled, totalSampled + totalDropped);
        }

        String getDroppedLog() {
            return String.format("Dropped %d of %d exchanges in this period, totalling %d dropped of %d exchanges overall.",
                droppedThisPeriod, totalThisPeriod, totalDropped, totalSampled + totalDropped);
        }
    }

}
