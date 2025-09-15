package br.com.fiap.fiapvideos.controller.api;

import br.com.fiap.fiapvideos.api.dto.UsuarioDTO;
import br.com.fiap.fiapvideos.controller.api.doc.AuthControllerDoc;
import br.com.fiap.fiapvideos.dto.request.LoginRequest;
import br.com.fiap.fiapvideos.dto.response.LoginResponse;
import br.com.fiap.fiapvideos.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fiap/auth")
public class AuthController implements AuthControllerDoc {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = this.authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getUsuarioLogado() {
        return ResponseEntity.ok(authService.getUsuarioLogado());
    }
}
