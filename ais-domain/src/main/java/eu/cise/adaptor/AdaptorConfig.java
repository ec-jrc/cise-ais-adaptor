/*
 * Copyright CISE AIS Adaptor (c) 2018, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.cise.adaptor;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.converters.DurationConverter;

import java.io.PrintStream;
import java.time.Duration;

/**
 * This file is containing the adaptor application configuration with different
 * details about the senders and receivers services.
 */
@Sources({"file:${conf.dir}ais-adaptor.properties",
        "classpath:ais-adaptor.properties"})
public interface AdaptorConfig extends Config {

    /**
     * In the CISE demo environment of the JRC is needed that all the vessels
     * have an IMO number (PK in a database). To avoid breaking the demo when
     * this flag is set to true the MMSI number will be copied over to the IMO
     * number.
     *
     * @return true if is a cise demo internal to JRC false otherwise
     */
    @Key("demo-environment")
    boolean isDemoEnvironment();

    /**
     * Setting this property to true will override the timestamp coming from the
     * AIS message with the current processing time. This could be necessary for
     * systems with strict policies or filtering of messaging non belonging
     *
     * @return true if the timestamp should be regenerated false otherwise
     */
    @Key("override-timestamps")
    boolean isOverridingTimestamps();

    /**
     * Address of the gateway that will receive the messages from the current
     * adaptor.
     *
     * @return the string with the gateway address
     */
    @Key("gateway.address")
    String getGatewayAddress();

    /**
     * Service Id of the sender public authority that is sending the messages.
     *
     * @return the string with the service id
     */
    @Key("sender.service.id")
    String getServiceId();

    /**
     * A parameter indicating the data freshness to inform the receivers.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the data freshness.
     */
    @Key("sender.service.data-freshness-type")
    String getDataFreshnessType();

    /**
     * A parameter indicating the sea basin of the receivers.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the sea basin.
     */
    @Key("sender.service.sea-basin-type")
    String getSeaBasinType();

    /**
     * A parameter indicating the url of the Legacy System sender service.
     *
     * @return the string with the sender service url.
     */
    @Key("sender.service.participant.url")
    String getEndpointUrl();

    /**
     * Service operation of the sender that sending the messages.
     * It can be 'PullRequest', 'PullResponse', 'Push'.
     * If unsure use 'Push'
     *
     * @return the string with the service operation
     */
    @Key("sender.service.operation")
    String getServiceOperation();

    /**
     * This field will be used when creating the Push message for a
     * subscription. The NODE needs a recipient even if the message won't
     * be sent to it but to the list of subscribers for validity reasons.
     *
     * @return the string with the subscription recipient service id
     */
    @Key("subscription.service.id")
    String getSubscribeServiceId();

    /**
     * A parameter indicating the priority of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the priority
     */
    @Key("message.priority")
    String getMessagePriority();

    /**
     * A parameter indicating the security level of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the security level
     */
    @Key("message.security-level")
    String getSecurityLevel();

    /**
     * A parameter indicating the sensitivity of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the sensitivity
     */
    @Key("message.sensitivity")
    String getSensitivity();

    /**
     * A parameter indicating the purpose of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the purpose
     */
    @Key("message.purpose")
    String getPurpose();

    /**
     * A parameter indicating the idle time of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the idle time
     */
    @Key("processing.idle.time")
    long getProcessingIdleTime();

    /**
     * A parameter indicating the number of entities that will be sent to the
     * sender in a single CISE message.
     *
     * @return the number of entities
     */
    @Key("processing.entities-per-message")
    int getNumberOfEntitiesPerMessage();

    /**
     * A parameter indicating the number of milliseconds to wait before sending a message when the
     * processing.entities-per-message has not been reached and the buffer of entities is not full.
     * It is needed to avoid to wait indefinitely if the processing.entities-per-message is not
     * reached.
     *
     * @return the timeout to wait before sending a message
     */
    @ConverterClass(DurationConverter.class)
    @Key("processing.sending.timeout")
    Duration getEntityBufferTimeout();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile service id
     */
    @Key("profile.${profile.number}.service_id")
    String getProfileServiceId();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile community
     */
    @Key("profile.${profile.number}.community")
    String getProfileCommunity();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile country
     */
    @Key("profile.${profile.number}.country")
    String getProfileCountry();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile data freshness
     */
    @Key("profile.${profile.number}.data_freshness")
    String getProfileDataFreshness();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile function
     */
    @Key("profile.${profile.number}.function")
    String getProfileFunction();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile sea basin
     */
    @Key("profile.${profile.number}.sea_basin")
    String getProfileSeaBasin();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile service operation
     */
    @Key("profile.${profile.number}.service_operation")
    String getProfileServiceOperation();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile service role
     */
    @Key("profile.${profile.number}.service_role")
    String getProfileServiceRole();

    /**
     * Specifies the Nth service profile parameter
     *
     * @return the service profile service type
     */
    @Key("profile.${profile.number}.service_type")
    String getProfileServiceType();

    @Key("app-context.type")
    String getAppContextType();

    /**
     * A convenience method to print all the properties
     *
     * @param out an out stream where to print the properties.
     */
    void list(PrintStream out);
}
