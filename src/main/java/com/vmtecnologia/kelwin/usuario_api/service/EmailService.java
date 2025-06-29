package com.vmtecnologia.kelwin.usuario_api.service;

public interface EmailService {
    void enviarEmailConfirmacao(String para, String nomeUsuario);
}
