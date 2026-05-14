package com.fullstack.users.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fullstack.users.dto.AuthResponse;
import com.fullstack.users.dto.LoginRequest;
import com.fullstack.users.dto.RegisterRequest;
import com.fullstack.users.dto.UserProfileResponse;
import com.fullstack.users.model.User;
import com.fullstack.users.model.UserRol;
import com.fullstack.users.model.UserStatus;
import com.fullstack.users.repository.UserRepository;
import com.fullstack.users.security.JwtService;
import com.fullstack.users.singleton.SessionManager;

import lombok.RequiredArgsConstructor;


//service que se encarga de la lógica de negocio relacionada con los usuarios, como el registro, login, gestión de sesiones y obtención de perfiles.
//valida las credenciales, maneja los estados de los usuarios, y coordina con el SessionManager para crear y cerrar sesiones, así como con el JwtService para generar los tokens JWT necesarios para la autenticación.
//y llama al sessionManager para crear una sesión y generar un token JWT válido, que luego se devuelve al cliente para su uso en futuras solicitudes autenticadas.
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionManager sessionManager;
    private final JwtService jwtService;

    public AuthResponse registrar(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo ya está registrado"
            );
        }

        User user = User.builder()
                .name(request.getName())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .rol(UserRol.CLIENT)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        String token = sessionManager.createSession(savedUser);

        return buildAuthResponse(
                savedUser,
                token,
                "Usuario registrado correctamente"
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"
                ));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario inactivo o bloqueado"
            );
        }

        boolean passwordValid = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordValid) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales inválidas"
            );
        }

        String token = sessionManager.createSession(user);

        return buildAuthResponse(
                user,
                token,
                "Login exitoso"
        );
    }

    public UserProfileResponse getAuthenticatedProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .rol(user.getRol())
                .status(user.getStatus())
                .build();
    }

    public void logout(String token) {
        sessionManager.closeSession(token);
    }

    public String sessionInfo() {
        return "SessionManager Singleton activo"
                + "\nHash instancia: " + sessionManager.getInstanceHashCode()
                + "\nSesiones activas: " + sessionManager.getActiveSessionsCount()
                + "\nInicializado en: " + sessionManager.getInitializationDate();
    }

    private AuthResponse buildAuthResponse(User user, String token, String message) {
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .rol(user.getRol())
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .message(message)
                .build();
    }
}