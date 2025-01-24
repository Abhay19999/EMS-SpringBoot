package io.org.reactivestax.type.exception;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String s) {
        log.error(s);
    }
}
