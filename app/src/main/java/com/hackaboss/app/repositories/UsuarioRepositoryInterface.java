package com.hackaboss.app.repositories;

import com.hackaboss.app.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepositoryInterface extends JpaRepository<Usuario, Long> {
}
