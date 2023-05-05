package com.csgo.player.controller;

import com.csgo.player.dto.PlayerDTO;
import com.csgo.player.entity.InventoryEntity;
import com.csgo.player.entity.PlayerEntity;
import com.csgo.player.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
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

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Void>  deletePlayerById(@PathVariable int id){
        playerService.deletePlayerById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/players")
    public ResponseEntity<PlayerEntity> createPlayer(@Valid @RequestBody PlayerEntity player){
        return new ResponseEntity<>(playerService.createPlayer(player),HttpStatus.CREATED);
    }

    @PatchMapping("/players/{id}")
    public ResponseEntity<PlayerEntity>  updatePlayerById(@PathVariable int id, @RequestBody PlayerEntity player){
        return new ResponseEntity<PlayerEntity>(playerService.updatePlayer(id,player),HttpStatus.CREATED);
    }


    @PatchMapping("/addfounds/{id}/{money}")
    public ResponseEntity<PlayerEntity>  updateMoney(@PathVariable int id, @PathVariable Double money){
        return new ResponseEntity<PlayerEntity>(playerService.addfounds(id,money),HttpStatus.CREATED);
    }

    @GetMapping("/players/{id}/inventory")
    public Mono<PlayerDTO> getPlayerWithInventory(@PathVariable int id){
        return playerService.findPlayerByIdWithInventory(id);
    }

}
