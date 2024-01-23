package com.example.demo.services;


import java.util.Optional;

import com.example.demo.models.UsuarioModel;
import com.example.demo.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;
    


    public Optional<UsuarioModel>  findByUsernameAndPassword(String user, String password) {
        return usuarioRepository.findByUserAndPassword(user, password);
    }


    
}