package com.vmtecnologia.kelwin.usuario_api.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EmailServiceImplTest")
class EmailServiceImplTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Deve simular o envio de email de confirmação com sucesso")
    void shouldSimulateEmailConfirmationSuccessfully() {
        String para = "teste@example.com";
        String nomeUsuario = "Usuario Teste";

        emailService.enviarEmailConfirmacao(para, nomeUsuario);

        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("Simulando envio de e-mail de confirmação:"), "Deve conter a mensagem de simulação.");
        assertTrue(consoleOutput.contains("Para: " + para), "Deve conter o email do destinatário.");
        assertTrue(consoleOutput.contains("Assunto: Confirme seu cadastro no nosso sistema!"), "Deve conter o assunto correto.");
        assertTrue(consoleOutput.contains("Conteúdo: Olá " + nomeUsuario + ", por favor, clique no link para confirmar seu endereço de e-mail."), "Deve conter o conteúdo correto.");
    }
}