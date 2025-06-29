package com.vmtecnologia.kelwin.usuario_api.service.impl;

import com.vmtecnologia.kelwin.usuario_api.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void enviarEmailConfirmacao(String para, String nomeUsuario) {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Simulando envio de e-mail de confirmação:");
        System.out.println("Para: " + para);
        System.out.println("Assunto: Confirme seu cadastro no nosso sistema!");
        System.out.println("Conteúdo: Olá " + nomeUsuario + ", por favor, clique no link para confirmar seu endereço de e-mail.");
        System.out.println("----------------------------------------------------------------------------------");
    }
}
