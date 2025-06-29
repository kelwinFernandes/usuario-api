package com.vmtecnologia.kelwin.usuario_api.controller;

import com.vmtecnologia.kelwin.usuario_api.dto.PageResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioCadastroDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.exception.EmailAlreadyExistsException;
import com.vmtecnologia.kelwin.usuario_api.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioControllerPureUnitTest")
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioCadastroDTO usuarioCadastroDto;
    private UsuarioResponseDTO usuarioResponseDto;

    @BeforeEach
    void setUp() {
        usuarioCadastroDto = new UsuarioCadastroDTO();
        usuarioCadastroDto.setNome("Test User");
        usuarioCadastroDto.setEmail("test@example.com");
        usuarioCadastroDto.setSenha("password123");

        usuarioResponseDto = new UsuarioResponseDTO(1L, "Test User", "test@example.com");
    }

    @Test
    @DisplayName("Deve cadastrar um novo usuário e retornar status 201 Created")
    void shouldRegisterNewUserAndReturn201Created() {
        when(usuarioService.cadastrarUsuario(any(UsuarioCadastroDTO.class))).thenReturn(usuarioResponseDto);

        ResponseEntity<UsuarioResponseDTO> responseEntity = usuarioController.cadastrarUsuario(usuarioCadastroDto);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(usuarioResponseDto.getId(), responseEntity.getBody().getId());
        assertEquals(usuarioResponseDto.getNome(), responseEntity.getBody().getNome());
        assertEquals(usuarioResponseDto.getEmail(), responseEntity.getBody().getEmail());

        verify(usuarioService, times(1)).cadastrarUsuario(usuarioCadastroDto);
    }

    @Test
    @DisplayName("Deve propagar EmailAlreadyExistsException ao tentar cadastrar usuário com email existente")
    void shouldPropagateEmailAlreadyExistsExceptionWhenRegisteringWithExistingEmail() {
        UsuarioCadastroDTO duplicateEmailDto = new UsuarioCadastroDTO();
        duplicateEmailDto.setNome("Another User");
        duplicateEmailDto.setEmail("existing@example.com");
        duplicateEmailDto.setSenha("anotherPass");

        when(usuarioService.cadastrarUsuario(any(UsuarioCadastroDTO.class)))
                .thenThrow(new EmailAlreadyExistsException("existing@example.com"));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            usuarioController.cadastrarUsuario(duplicateEmailDto);
        });

        verify(usuarioService, times(1)).cadastrarUsuario(duplicateEmailDto);
    }

    @Test
    @DisplayName("Deve consultar usuários e retornar status 200 OK com paginação customizada")
    void shouldConsultUsersAndReturn200OKWithCustomPagination() {
        UsuarioResponseDTO user1 = new UsuarioResponseDTO(1L, "Alice", "alice@example.com");
        UsuarioResponseDTO user2 = new UsuarioResponseDTO(2L, "Bob", "bob@example.com");
        List<UsuarioResponseDTO> userList = Arrays.asList(user1, user2);

        PageResponseDTO<UsuarioResponseDTO> pageResponse = new PageResponseDTO<>(userList, 0, 10, 1, 2L);

        when(usuarioService.consultarUsuarios(anyString(), any(Pageable.class))).thenReturn(pageResponse);

        Pageable pageable = PageRequest.of(0, 10);
        ResponseEntity<PageResponseDTO<UsuarioResponseDTO>> responseEntity = usuarioController.consultarUsuarios("test", pageable);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(userList.size(), responseEntity.getBody().getContent().size());
        assertEquals(pageResponse.getPageNumber(), responseEntity.getBody().getPageNumber());
        assertEquals(pageResponse.getTotalElements(), responseEntity.getBody().getTotalElements());

        verify(usuarioService, times(1)).consultarUsuarios("test", pageable);
    }

    @Test
    @DisplayName("Deve consultar usuário por ID e retornar status 200 OK quando encontrado")
    void shouldConsultUserByIdAndReturn200OKWhenFound() {
        when(usuarioService.consultarUsuarioPorId(1L)).thenReturn(Optional.of(usuarioResponseDto));

        ResponseEntity<UsuarioResponseDTO> responseEntity = usuarioController.consultarUsuarioPorId(1L);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(usuarioResponseDto.getId(), responseEntity.getBody().getId());

        verify(usuarioService, times(1)).consultarUsuarioPorId(1L);
    }

    @Test
    @DisplayName("Deve consultar usuário por ID e retornar status 404 Not Found quando não encontrado")
    void shouldConsultUserByIdAndReturn404NotFoundWhenNotFound() {
        when(usuarioService.consultarUsuarioPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<UsuarioResponseDTO> responseEntity = usuarioController.consultarUsuarioPorId(99L);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());

        verify(usuarioService, times(1)).consultarUsuarioPorId(99L);
    }
}