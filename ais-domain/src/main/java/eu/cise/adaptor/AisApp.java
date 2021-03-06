/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
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

import eu.cise.servicemodel.v1.message.Message;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Main class in the domain module. The run() method start the process of
 * reading from the selected AisSource, translate the ais info into cise
 * messages and dispatch the cise messages through a rest protocol to the
 * gateway.
 * <p>
 * Three phases are composing the transformation:
 * - toFluxString: generate the source and feed the flux stream of ais messages in
 * string format
 * - toCiseMessages: transform the string in {NMEAMessage}, then in {AISMessage}
 * and finally in {AisMsg}
 * - dispatchMessages: using multi threading dispatches the cise messages
 * to the gateway absorbing peaks
 */
public class AisApp implements Runnable {

    private final AdaptorLogger logger;
    private final AdaptorConfig config;
    private final AisStreamGenerator aisStreamGenerator;
    private final DefaultPipeline aisStreamProcessor;
    private final Dispatcher dispatcher;

    /**
     * The App is mainly built with a stream generator a processor and a message
     * dispatcher.
     * <p>
     * The generator will produce a stream of strings reading them from
     * different possible sources:
     * - plain text files
     * - tcp sockets
     * - whatever other AIS information producer
     * <p>
     * The processor will transform the incoming stream of ais strings into a
     * sequence of CISE push messages objects. The transformation will be
     * performed in multiple stages.
     * - String -&gt; AisMsg: where the latter is a decoded representation
     * of the message in an domain object
     * - AisMsg -&gt; {@code Optional<Entity>}: the ais message is translated
     * into a cise
     * vessel if is of type 1,2,3 or 5, otherwise it will be an empty optional.
     * - {@code List<Entity>} -&gt; Push:
     *
     * @param aisStreamGenerator stream generator of ais strings
     * @param aisStreamProcessor stream processor of ais strings into cise messages
     * @param dispatcher         dispatcher of cise messages
     * @param config             application configuration object
     */
    public AisApp(AisStreamGenerator aisStreamGenerator,
                  DefaultPipeline aisStreamProcessor,
                  Dispatcher dispatcher,
                  AdaptorLogger logger,
                  AdaptorConfig config) {
        this.aisStreamGenerator = aisStreamGenerator;
        this.aisStreamProcessor = aisStreamProcessor;
        this.dispatcher = dispatcher;
        this.logger = logger;
        this.config = config;
    }

    /**
     * The run method is the domain object composing the behaviour.
     * Generate AIS string messages and pipeline them into the transformation
     * into cise messages and that dispatches them.
     */
    @Override
    public void run() {
        dispatchMessages(aisStreamProcessor.process(Flux.fromStream(aisStreamGenerator.generate())));
    }

    /**
     * The flux stream allows to publish in a thread pool the dispatching
     * so to make http request in a parallel non blocking manner.
     *
     * @param messageStream is a stream of CISE messages
     */
    private void dispatchMessages(Flux<Message> messageStream) {
        messageStream
                .publishOn(Schedulers.elastic())
                .map(msg -> dispatcher.send(msg, config.getGatewayAddress()))
                .doOnNext(logger::logDispatchResponses)
                .doOnError(logger::logDispatchError)
                .blockLast();
    }

}
