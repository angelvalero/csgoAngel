package com.csgo.player.controller;

import com.csgo.player.entity.PlayerEntity;
import com.csgo.player.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlayerController {
    private PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public List <PlayerEntity> getAllPlayers(){
        return playerService.findAll();
    }

    @GetMapping("/players/{id}")
    public PlayerEntity getPlayerById(@PathVariable int id){
        return playerService.findPlayerById(id);
    }

    @GetMapping("/players/email/{email}")
    public ResponseEntity<PlayerEntity> getPlayerByEmail(@PathVariable String email){
        return new ResponseEntity<PlayerEntity>(playerService.findPlayerByEmail(email), HttpStatus.OK);
    }
}
