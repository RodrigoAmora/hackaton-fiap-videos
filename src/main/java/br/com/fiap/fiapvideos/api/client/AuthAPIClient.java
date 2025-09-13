package br.com.fiap.fiapvideos.api.client;

import br.com.fiap.fiapvideos.api.dto.UsuarioDTO;
import br.com.fiap.fiapvideos.api.interceptor.FeignClientConfig;
import br.com.fiap.fiapvideos.dto.request.LoginRequest;
import br.com.fiap.fiapvideos.dto.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${ms-auth.url}", configuration = FeignClientConfig.class)
public interface AuthAPIClient {

    @PostMapping("/api/auth/login")
    LoginResponse login(@RequestBody LoginRequest request);

    @GetMapping("/api/usuario/me")
    UsuarioDTO getUsuarioLogado();

}
