package com.example.demo.controllers;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.demo.models.UsuarioModel;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.services.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@RestController
@RequestMapping("/api")
public class UsuarioController {
    @Autowired
    UsuarioService usuarioService;
    
    @Autowired
    private UsuarioRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String userLogin = loginRequest.getUser();
        String password = loginRequest.getPassword();

        // Buscar el usuario en la base de datos por username y password
        Optional<UsuarioModel> userOptional = userRepository.findByUserAndPassword(userLogin, password);

        if (userOptional.isPresent()) {
            // Usuario encontrado, generar y devolver un token
        	UsuarioModel user = userOptional.get();
            String token = generateToken(user);

            // Actualizar el token en la entidad y en la base de datos (si es necesario)
            user.setToken(token);
            userRepository.save(user);

            return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            // Usuario no encontrado, devolver código de error 401
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }
    
    private String generateToken(UsuarioModel user) {
        // Lógica para generar un token (puede ser un UUID, un valor aleatorio, etc.)
        // Aquí por simplicidad, se genera un UUID básico.
        return java.util.UUID.randomUUID().toString();
    }
    
    @GetMapping("/users")
    public ResponseEntity<byte[]> getUsersData() {
        List<UsuarioModel> users = (List<UsuarioModel>) userRepository.findAll();
        
        // Quitar las contraseñas de cada usuario
        for (UsuarioModel user : users) {
            user.setPassword(null);
        }

        // Convertir data a JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] jsonData;
        try {
            jsonData = objectMapper.writeValueAsBytes(users);
        } catch (JsonProcessingException e) {
            // Exception si conversion a JSON falla
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", "users_data.json");

        return new ResponseEntity<>(jsonData, headers, HttpStatus.OK);
    }
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Async
    @PostMapping("/copy")
    public ResponseEntity<String> copyFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Archivo vacío", HttpStatus.BAD_REQUEST);
        }

        try {
            // Ruta de destino
            Path destinationPath = Paths.get("src", "main", "resources", "copied_files", file.getOriginalFilename());

            // Copiar el archivo al destino
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Log o mensaje de éxito
            System.out.println("File copy started asynchronously.");

            return new ResponseEntity<>("File copy started asynchronously.", HttpStatus.OK);
        } catch (IOException e) {
            // Log o mensaje de error
            e.printStackTrace();
            return new ResponseEntity<>("Error durante la copia del archivo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}