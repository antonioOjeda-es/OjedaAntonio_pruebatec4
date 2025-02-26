package com.hackaboss.app.repositories;

import com.hackaboss.app.entities.Habitacion;
import com.hackaboss.app.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepositoryInterface extends JpaRepository<Habitacion, Long> {
    Hotel findByHotelNombre(String nombreHote);
}
