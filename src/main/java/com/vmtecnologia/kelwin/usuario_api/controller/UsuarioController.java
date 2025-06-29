package com.vmtecnologia.kelwin.usuario_api.controller;

import com.vmtecnologia.kelwin.usuario_api.dto.PageResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioCadastroDTO;
import com.vmtecnologia.kelwin.usuario_api.dto.UsuarioResponseDTO;
import com.vmtecnologia.kelwin.usuario_api.service.UsuarioService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para cadastro de um novo usuário.
     * POST /usuarios
     * @param usuarioDto Dados do usuário para cadastro.
     * @return ResponseEntity com o UsuarioResponseDto e status 201 Created.
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrarUsuario(@Valid @RequestBody UsuarioCadastroDTO usuarioDto) {
        UsuarioResponseDTO novoUsuario = usuarioService.cadastrarUsuario(usuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    /**
     * Endpoint para consulta de usuários com paginação e filtro opcional por nome.
     * GET /usuarios?page=0&size=10&sort=nome,asc&nome=fulano
     * @param nome Filtro opcional para qualquer parte do nome do usuário.
     * @param pageable Objeto Pageable para configurar paginação (page, size, sort).
     * @return ResponseEntity com um Page de UsuarioResponseDto e status 200 OK.
     */
    @GetMapping
    public ResponseEntity<PageResponseDTO<UsuarioResponseDTO>> consultarUsuarios(
            @RequestParam(required = false) String nome,
            @PageableDefault(page = 0, size = 10, sort = "nome") Pageable pageable) {
        PageResponseDTO<UsuarioResponseDTO> usuarios = usuarioService.consultarUsuarios(nome, pageable);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Endpoint para consulta de usuário por ID.
     * GET /usuarios/{id}
     * @param id ID do usuário a ser consultado.
     * @return ResponseEntity com o UsuarioResponseDto e status 200 OK, ou 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> consultarUsuarioPorId(@PathVariable Long id) {
        Optional<UsuarioResponseDTO> usuario = usuarioService.consultarUsuarioPorId(id);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}