package com.example.silverbridgeX_user.user.repository;

import com.example.silverbridgeX_user.user.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
}
