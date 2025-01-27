package io.org.reactivestax.type.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClientNotRegisteredException extends RuntimeException {
    private static final Log log = LogFactory.getLog(ClientNotRegisteredException.class);

    public ClientNotRegisteredException(String clientIsNotRegistered) {
        log.error(clientIsNotRegistered);
    }
}
