package com.hackaboss.app.repositories;

import com.hackaboss.app.entities.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VueloRepositoryInterface extends JpaRepository<Vuelo, Long> {
}
