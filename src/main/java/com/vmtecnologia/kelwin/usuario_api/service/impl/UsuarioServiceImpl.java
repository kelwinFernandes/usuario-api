package com.vmtecnologia.kelwin.usuario_api.service.impl;

import com.vmtecnologia.kelwin.usuario_api.dto.PageResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioCadastroDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.exception.EmailAlreadyExistsException;
import com.vmtecnologia.kelwin.usuario_api.model.Usuario;
import com.vmtecnologia.kelwin.usuario_api.repository.UsuarioRepository;
import com.vmtecnologia.kelwin.usuario_api.service.EmailService;
import com.vmtecnologia.kelwin.usuario_api.service.UsuarioService;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(UsuarioCadastroDTO usuarioDto) {
        usuarioRepository.findByEmail(usuarioDto.getEmail()).ifPresent(u -> {
            throw new EmailAlreadyExistsException(usuarioDto.getEmail());
        });

        String senhaCriptografada = passwordEncoder.encode(usuarioDto.getSenha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(usuarioDto.getNome());
        novoUsuario.setEmail(usuarioDto.getEmail());
        novoUsuario.setSenha(senhaCriptografada);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        emailService.enviarEmailConfirmacao(usuarioSalvo.getEmail(), usuarioSalvo.getNome());

        return new UsuarioResponseDTO(usuarioSalvo.getId(), usuarioSalvo.getNome(), usuarioSalvo.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<UsuarioResponseDTO> consultarUsuarios(String nome, Pageable pageable) {
        Page<Usuario> usuariosPage;
        if (nome != null && !nome.trim().isEmpty()) {
            usuariosPage = usuarioRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            usuariosPage = usuarioRepository.findAll(pageable);
        }

        List<UsuarioResponseDTO> dtos = usuariosPage.getContent().stream()
                .map(usuario -> new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail()))
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                dtos,
                usuariosPage.getNumber(),
                usuariosPage.getSize(),
                usuariosPage.getTotalPages(),
                usuariosPage.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> consultarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail()));
    }
}