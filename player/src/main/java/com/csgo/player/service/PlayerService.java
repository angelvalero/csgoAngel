package com.csgo.player.service;

import com.csgo.player.entity.PlayerEntity;
import com.csgo.player.repository.PlayerRepository;
import com.csgo.player.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PlayerService {
    private PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List <PlayerEntity> findAll(){
        log.info("Finding All Players");
        return playerRepository.findAll();
    }

    public PlayerEntity findPlayerById(Integer id){
        Optional <PlayerEntity> player = playerRepository.findById(id);
        if (player.isEmpty()){
            log.error("Not found a player with that ID");
            throw new EntityNotFoundException("Usuario con el id: "+id+" no encontrado");
        }
        log.info("Return a Player");
        return player.get();
    }

    public PlayerEntity findPlayerByEmail(String email){
        Optional<PlayerEntity> player = playerRepository.findByEmail(email);

        if (player.isEmpty()){
            log.error("Not found a player with that email");
            throw new EntityNotFoundException("Usuario con el email: "+email+" no encontrado");
        }
        log.info("Return a Player");
        return player.get();
    }
}
