package br.com.fiap.fiapvideos.service;

import br.com.fiap.fiapvideos.api.client.AuthAPIClient;
import br.com.fiap.fiapvideos.api.dto.UsuarioDTO;
import br.com.fiap.fiapvideos.config.security.manager.TokenManager;
import br.com.fiap.fiapvideos.dto.request.LoginRequest;
import br.com.fiap.fiapvideos.dto.response.LoginResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final TokenManager tokenManager;
    private final AuthAPIClient authAPIClient;

    public AuthService(TokenManager tokenManager,
                       AuthAPIClient authAPIClient) {
        this.tokenManager = tokenManager;
        this.authAPIClient = authAPIClient;
    }

    public LoginResponse login(LoginRequest request) {
        LoginResponse response = this.authAPIClient.login(request);

        if (response != null) {
            tokenManager.storeToken(response.token());
        }

        return response;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public UsuarioDTO getUsuarioLogado() {
        return this.authAPIClient.getUsuarioLogado();
    }
}
