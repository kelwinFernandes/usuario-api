package com.vmtecnologia.kelwin.usuario_api.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("O email '" + email + "' já está cadastrado no sistema.");
    }
}
