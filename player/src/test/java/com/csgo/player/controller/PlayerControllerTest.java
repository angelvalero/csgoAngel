package com.csgo.player.controller;

import com.csgo.player.dto.PlayerDTO;
import com.csgo.player.entity.InventoryEntity;
import com.csgo.player.entity.PlayerEntity;
import com.csgo.player.exception.DuplicateEmailException;
import com.csgo.player.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PlayerController playerController;

    PlayerEntity playerModel;
    InventoryEntity inventoryModel;
    PlayerDTO playerDTO;

    @BeforeEach
    public void createPlayer() {
        playerModel = new PlayerEntity(1, "Shadow","angel@lpl.com", 30.00, "224.24.171.0");
        inventoryModel = new InventoryEntity(1,1, "Rocket Pop, MP9", "Minimal Wear (MW)", false, 690.52, true);
        playerDTO = new PlayerDTO(1,"Shadow","angel@lpl.com", 46.58, List.of(inventoryModel));
    }

    @DisplayName("GIVEN no information,WHEN the endpoint is called, THEN return a list of players")
    @Test
    void getAllPlayers_test() {
        List<PlayerEntity> players = Arrays.asList(playerModel);
        when(playerService.findAll()).thenReturn(players);

        List<PlayerEntity> existingPlayers = playerController.getAllPlayers();
        assertThat(existingPlayers).isEqualTo(players);

    }


    @DisplayName("GIVEN a player id,WHEN the endpoint is called, THEN return a playerEntity with a player filter by ID")
    @Test
    void getPlayerById_test() {
        playerModel.setId(1);
        when(playerService.findPlayerById(1)).thenReturn(playerModel);

        PlayerEntity player = playerController.getPlayerById(1);

        verify(playerService, atLeastOnce()).findPlayerById(1);
        assertThat(player.getName()).isEqualTo("Shadow");

    }


    @DisplayName("GIVEN a player email,WHEN the endpoint is called, THEN return a playerEntity with a player filter by email")
    @Test
    void getPlayerByEmail_test() {
        String email = "alpl@gft.com";
        when(playerService.findPlayerByEmail(email)).thenReturn(playerModel);

        ResponseEntity<PlayerEntity> player = playerController.getPlayerByEmail(email);

        verify(playerService, atLeastOnce()).findPlayerByEmail(email);
        assertThat(player.getBody().getName()).isEqualTo("Shadow");

    }


    @DisplayName("GIVEN no information,WHEN the endpoint is called, THEN delete this player and return 204")
    @Test
    void deletePlayerbyId_test() {
        int id=1;
        doNothing().when(playerService).deletePlayerById(1);
        ResponseEntity<Void> response = playerController.deletePlayerById(1);

        verify(playerService, atLeastOnce()).deletePlayerById(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }


    @DisplayName("GIVEN a player information,WHEN the endpoint is called, THEN create player in the database")
    @Test
    void createPlayer_test(){
        when(playerService.createPlayer(playerModel)).thenReturn(playerModel);
        ResponseEntity<PlayerEntity> response = playerController.createPlayer(playerModel);

        assertThat(playerModel).isEqualTo(response.getBody());
        assertThat(HttpStatus.CREATED).isEqualTo(response.getStatusCode());
    }


    @DisplayName("GIVEN a player information,WHEN the endpoint is called, THEN update player in the database")
    @Test
    void updatePlayerById_test() {
        when(playerService.updatePlayer(1,playerModel)).thenReturn(playerModel);
        ResponseEntity<PlayerEntity> response = playerController.updatePlayerById(1,playerModel);

        assertThat(playerModel).isEqualTo(response.getBody());
        assertThat(HttpStatus.CREATED).isEqualTo(response.getStatusCode());
    }



    @DisplayName("GIVEN a player id and the money,WHEN the endpoint is called, THEN update money")
    @Test
    void addfoundsById_test() {
        when(playerService.addfounds(1,30.00)).thenReturn(playerModel);
        ResponseEntity<PlayerEntity> response = playerController.updateMoney(1,30.00);

        assertThat(response.getBody().getMoney()).isEqualTo(30.00);
    }



}
