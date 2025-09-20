package br.com.fiap.fiapvideos.service;

import br.com.fiap.fiapvideos.api.client.AuthAPIClient;
import br.com.fiap.fiapvideos.api.dto.ERole;
import br.com.fiap.fiapvideos.api.dto.Role;
import br.com.fiap.fiapvideos.api.dto.UsuarioDTO;
import br.com.fiap.fiapvideos.dto.request.LoginRequest;
import br.com.fiap.fiapvideos.dto.response.LoginResponse;
import br.com.fiap.fiapvideos.security.manager.TokenManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TokenManager tokenManager;

    @Mock
    private AuthAPIClient authAPIClient;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        ERole eRole = ERole.ROLE_MODERATOR;
        Role role = new Role();
        role.setName(eRole);

        loginRequest = new LoginRequest("usuario@teste.com", "senha123");
        loginResponse = new LoginResponse("token-jwt-123", "Bearer", "Fulano", "usuario@teste.com", role);
        usuarioDTO = new UsuarioDTO("4f4db78c-2ef6-4ecf-bbae-455f332cf625", "Usuario Teste", "usuario@teste.com",
                                   "11122233344", LocalDate.now(), LocalDateTime.now(), role);
    }

    @Test
    void login_QuandoCredenciaisValidas_DeveRetornarLoginResponse() {
        // Arrange
        when(authAPIClient.login(loginRequest)).thenReturn(loginResponse);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(loginResponse.token(), response.token());
        assertEquals(loginResponse.tipo(), response.tipo());

        verify(authAPIClient).login(loginRequest);
        verify(tokenManager).storeToken(loginResponse.token());
    }

    @Test
    void login_QuandoCredenciaisInvalidas_DeveRetornarNull() {
        // Arrange
        when(authAPIClient.login(loginRequest)).thenReturn(null);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNull(response);

        verify(authAPIClient).login(loginRequest);
        verify(tokenManager, never()).storeToken(any());
    }

    @Test
    void logout_DeveLimparContextoDeSeguranca() {
        // Act
        authService.logout();

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void getUsuarioLogado_QuandoUsuarioAutenticado_DeveRetornarUsuarioDTO() {
        // Arrange
        when(authAPIClient.getUsuarioLogado()).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO result = authService.getUsuarioLogado();

        // Assert
        assertNotNull(result);
        assertEquals(usuarioDTO.id(), result.id());
        assertEquals(usuarioDTO.nome(), result.nome());
        assertEquals(usuarioDTO.email(), result.email());

        verify(authAPIClient).getUsuarioLogado();
    }

    @Test
    void getUsuarioLogado_QuandoUsuarioNaoAutenticado_DeveRetornarNull() {
        // Arrange
        when(authAPIClient.getUsuarioLogado()).thenReturn(null);

        // Act
        UsuarioDTO result = authService.getUsuarioLogado();

        // Assert
        assertNull(result);

        verify(authAPIClient).getUsuarioLogado();
    }
}
