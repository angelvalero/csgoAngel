package com.csgo.player.repository;

import com.csgo.player.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity,Integer> {

    PlayerEntity findByEmail(String email);

    boolean existsByEmail(String email);
}
