package com.vmtecnologia.kelwin.usuario_api.service;

import com.vmtecnologia.kelwin.usuario_api.dto.PageResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioCadastroDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioResponseDTO;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UsuarioService {
    UsuarioResponseDTO cadastrarUsuario(UsuarioCadastroDTO usuarioDto);
    PageResponseDTO<UsuarioResponseDTO> consultarUsuarios(String nome, Pageable pageable);
    Optional<UsuarioResponseDTO> consultarUsuarioPorId(Long id);
}
