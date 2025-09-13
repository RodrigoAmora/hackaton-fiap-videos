package br.com.fiap.fiapvideos.api.interceptor;

import br.com.fiap.fiapvideos.config.security.manager.TokenManager;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    private final TokenManager tokenManager;

    public FeignClientConfig(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Bean
    RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Não adiciona o token para a rota de login
            if (!requestTemplate.url().contains("/api/auth/login")) {
                String token = tokenManager.getStoredToken();
                if (token != null) {
                    requestTemplate.header("Authorization", "Bearer " + token);
                }
            }
        };
    }
}
