package eu.cise.adaptor;

import eu.cise.adaptor.dispatch.DispatchResult;
import eu.cise.servicemodel.v1.message.Message;

/**
 * Dispatcher interface will abstract from the protocols and details used to
 * transfer messages from one system to another.
 */
public interface Dispatcher {

    /**
     * This method will send a string payload to a given string address. The
     * protocol used could be REST, SOAP, Domibus Endpoints or other solutions
     *
     * @return a response object with the status and body returned by the
     * counterpart
     */
    DispatchResult send(Message message, String address);

}