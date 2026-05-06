package com.fullstack.users.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fullstack.users.dto.AuthResponse;
import com.fullstack.users.dto.LoginRequest;
import com.fullstack.users.dto.RegisterRequest;
import com.fullstack.users.model.User;
import com.fullstack.users.model.UserRol;
import com.fullstack.users.model.UserStatus;
import com.fullstack.users.repository.UserRepository;
import com.fullstack.users.singleton.LoginSingletonManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse registrar(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo ya está registrado");
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

        return AuthResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .lastname(savedUser.getLastname())
                .email(savedUser.getEmail())
                .rol(savedUser.getRol())
                .token(generarTokenTemporal())
                .message("Usuario registrado correctamente")
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"
                ));

        //aplicamos el singleton para validar el login, se encarga de comparar la contraseña ingresada con la contraseña almacenada en la base de datos, y registra el intento de 
        //login
        LoginSingletonManager loginManager = LoginSingletonManager.getInstance();

        boolean validLogin = loginManager.validateLogin(
                user,
                request.getPassword(),
                passwordEncoder
        );

        if (!validLogin) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .rol(user.getRol())
                .token(generarTokenTemporal())
                .message("Login exitoso")
                .build();
    }


    //método para probar el singleton, si el login sigue utilizando la misma instancia del singleton, el contador de intentos de login se incrementará 
    // cada vez que se llame a este método, y la fecha de inicialización será la misma
    public String SingletonTest() {
        LoginSingletonManager login1 = LoginSingletonManager.getInstance();
        LoginSingletonManager login2 = LoginSingletonManager.getInstance();
        LoginSingletonManager login3 = LoginSingletonManager.getInstance();

        boolean SameInstance = login1 == login2 && login2 == login3;

        return "¿Son la misma instancia? " + SameInstance
                + "\nTotal intentos de login: " + login1.getTotalLoginAttempts()
                + "\nInicializado en: " + login1.getInitializationDate();
    }

    //token temporal simulado, se debe aplicar JWT en un futuro.
    private String generarTokenTemporal() {
        return UUID.randomUUID().toString();
    }
}