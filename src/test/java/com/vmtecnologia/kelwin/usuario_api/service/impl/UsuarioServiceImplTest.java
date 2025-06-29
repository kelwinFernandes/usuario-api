package com.vmtecnologia.kelwin.usuario_api.service.impl;

import com.vmtecnologia.kelwin.usuario_api.dto.PageResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioCadastroDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.exception.EmailAlreadyExistsException;
import com.vmtecnologia.kelwin.usuario_api.model.Usuario;
import com.vmtecnologia.kelwin.usuario_api.repository.UsuarioRepository;
import com.vmtecnologia.kelwin.usuario_api.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImplTest")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioCadastroDTO usuarioCadastroDto;
    private UsuarioResponseDTO usuarioResponseDto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setSenha("hashedPassword");

        usuarioCadastroDto = new UsuarioCadastroDTO();
        usuarioCadastroDto.setNome("New User");
        usuarioCadastroDto.setEmail("new@example.com");
        usuarioCadastroDto.setSenha("rawPassword123");

        usuarioResponseDto = new UsuarioResponseDTO(1L, "Test User", "test@example.com");
    }

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso e enviar email")
    void shouldRegisterNewUserSuccessfullyAndSendEmail() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO result = usuarioService.cadastrarUsuario(usuarioCadastroDto);

        assertNotNull(result);
        assertEquals(usuario.getId(), result.getId());
        assertEquals(usuario.getNome(), result.getNome());
        assertEquals(usuario.getEmail(), result.getEmail());

        verify(passwordEncoder, times(1)).encode(usuarioCadastroDto.getSenha());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(emailService, times(1)).enviarEmailConfirmacao(usuario.getEmail(), usuario.getNome());
    }

    @Test
    @DisplayName("Não deve cadastrar usuário se o email já existe e deve lançar EmailAlreadyExistsException")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        Usuario existingUser = new Usuario(2L, "Existing User", "existing@example.com", "existingPass");
        UsuarioCadastroDTO duplicateEmailDto = new UsuarioCadastroDTO();
        duplicateEmailDto.setNome("Another User");
        duplicateEmailDto.setEmail("existing@example.com");
        duplicateEmailDto.setSenha("anotherPass");

        when(usuarioRepository.findByEmail(duplicateEmailDto.getEmail())).thenReturn(Optional.of(existingUser));

        EmailAlreadyExistsException thrown = assertThrows(
                EmailAlreadyExistsException.class,
                () -> usuarioService.cadastrarUsuario(duplicateEmailDto)
        );

        assertEquals("O email 'existing@example.com' já está cadastrado no sistema.", thrown.getMessage());

        verify(usuarioRepository, times(1)).findByEmail(duplicateEmailDto.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(emailService, never()).enviarEmailConfirmacao(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve consultar usuários sem filtro e retornar uma página paginada")
    void shouldConsultUsersWithoutFilterAndReturnPaginatedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Usuario> usuariosList = Arrays.asList(usuario, new Usuario(2L, "Outro User", "outro@example.com", "pass"));
        Page<Usuario> usuariosPage = new PageImpl<>(usuariosList, pageable, usuariosList.size());

        when(usuarioRepository.findAll(pageable)).thenReturn(usuariosPage);

        PageResponseDTO<UsuarioResponseDTO> result = usuarioService.consultarUsuarios(null, pageable);

        assertNotNull(result);
        assertEquals(usuariosList.size(), result.getContent().size());
        assertEquals(usuariosPage.getNumber(), result.getPageNumber());
        assertEquals(usuariosPage.getSize(), result.getPageSize());
        assertEquals(usuariosPage.getTotalElements(), result.getTotalElements());
        assertEquals(usuariosPage.getTotalPages(), result.getTotalPages());
        verify(usuarioRepository, times(1)).findAll(pageable);
        verify(usuarioRepository, never()).findByNomeContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve consultar usuários com filtro por nome e retornar uma página paginada")
    void shouldConsultUsersWithFilterAndReturnPaginatedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Usuario> filteredList = Collections.singletonList(usuario);
        Page<Usuario> filteredPage = new PageImpl<>(filteredList, pageable, filteredList.size());

        when(usuarioRepository.findByNomeContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(filteredPage);

        PageResponseDTO<UsuarioResponseDTO> result = usuarioService.consultarUsuarios("Test", pageable);

        assertNotNull(result);
        assertEquals(filteredList.size(), result.getContent().size());
        assertEquals(filteredPage.getNumber(), result.getPageNumber());
        assertEquals(filteredPage.getSize(), result.getPageSize());
        assertEquals(filteredPage.getTotalElements(), result.getTotalElements());
        assertEquals(filteredPage.getTotalPages(), result.getTotalPages());
        verify(usuarioRepository, times(1)).findByNomeContainingIgnoreCase("Test", pageable);
        verify(usuarioRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve consultar usuário por ID e encontrá-lo")
    void shouldConsultUserByIdAndFindIt() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponseDTO> result = usuarioService.consultarUsuarioPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(usuario.getId(), result.get().getId());
        assertEquals(usuario.getNome(), result.get().getNome());
        assertEquals(usuario.getEmail(), result.get().getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve consultar usuário por ID e não encontrá-lo")
    void shouldConsultUserByIdAndNotFindIt() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> result = usuarioService.consultarUsuarioPorId(2L);

        assertFalse(result.isPresent());
        verify(usuarioRepository, times(1)).findById(2L);
    }
}