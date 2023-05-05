package com.csgo.player.service;


import com.csgo.player.controller.PlayerController;
import com.csgo.player.dto.PlayerDTO;
import com.csgo.player.entity.InventoryEntity;
import com.csgo.player.entity.PlayerEntity;
import com.csgo.player.exception.DuplicateEmailException;
import com.csgo.player.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @InjectMocks
    private PlayerService playerService;


    @InjectMocks
    private PlayerController playerController;
    @Mock
    private PlayerRepository playerRepository;

    private WebClient webClient;


    private ModelMapper modelMapper;
    PlayerEntity playerModel;
    InventoryEntity inventoryModel;

    PlayerDTO playerDTO;

    @BeforeEach
    public void createPlayer() {
        playerModel = new PlayerEntity(1, "Shadow","angel@lpl.com", 46.58, "224.24.171.0");
        inventoryModel = new InventoryEntity(1,1, "Rocket Pop, MP9", "Minimal Wear (MW)", false, 690.52, true);
        playerDTO = new PlayerDTO(1,"Shadow","angel@lpl.com", 46.58,List.of(inventoryModel));
    }

    @DisplayName("GIVEN no information,WHEN the endpoint is called, THEN return a list of players")
    @Test
    void getAllPlayers_test() {
        List <PlayerEntity> expectedPlayers = new ArrayList<PlayerEntity>();
        expectedPlayers.add(playerModel);
        expectedPlayers.add(playerModel);

        when(playerRepository.findAll()).thenReturn(expectedPlayers);

        List <PlayerEntity> actualPlayers = playerService.findAll();

        assertThat(expectedPlayers).isEqualTo(actualPlayers);


    }
    @DisplayName("GIVEN a player id,WHEN the endpoint is called, THEN return a playerEntity with a player filter by ID")
    @Test
    void getPlayerById_test() {

        playerModel.setId(1);
        playerModel.setName("Angel");

        when(playerRepository.findById(1)).thenReturn(Optional.of(playerModel));

        PlayerEntity result = playerService.findPlayerById(1);

        assertNotNull(result);
        assertEquals(playerModel.getName(), result.getName());

        verify(playerRepository, times(1)).findById(1);

    }

    @DisplayName("GIVEN a player id,WHEN the endpoint is called, THEN return a exception with not found")
    @Test
    void getPlayerByIdNotFound_test() {

        when(playerRepository.findById(1234)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            playerService.findPlayerById(1234);
        });

        assertEquals("Usuario con el id: " + 1234 + " no encontrado", exception.getMessage());

    }

    @DisplayName("GIVEN a player email,WHEN the endpoint is called, THEN return a playerEntity with a player filter by email")
    @Test
    void getPlayerByEmail_test() {


        playerModel.setEmail("angelvp@lpl.com");

        when(playerRepository.findByEmail("angelvp@lpl.com")).thenReturn(playerModel);

        PlayerEntity result = playerService.findPlayerByEmail("angelvp@lpl.com");

        assertNotNull(result);
        assertEquals(playerModel.getEmail(), result.getEmail());

        verify(playerRepository, times(1)).findByEmail("angelvp@lpl.com");
    }

    @DisplayName("GIVEN a player email,WHEN the endpoint is called, THEN return a exception with not found")
    @Test
    void getPlayerByEmailNotFound_test() {


        when(playerRepository.findByEmail("notfound@test")).thenReturn(null);


        assertThatThrownBy(() -> playerService.findPlayerByEmail("notfound@test"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario con el email: notfound@test no encontrado");
    }

    @DisplayName("GIVEN no information,WHEN the endpoint is called, THEN delete this player and return 204")
    @Test
    void deletePlayerbyId_test() {
        playerService.deletePlayerById(1);
        verify(playerRepository, times(1)).deleteById(1);
    }

    @DisplayName("GIVEN a player id,WHEN the endpoint is called, THEN delete this player and return exception")
    @Test
    void deletePlayerbyIdNot_test() {
        doThrow(EmptyResultDataAccessException.class).when(playerRepository).deleteById(1234);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> playerService.deletePlayerById(1234));

        assertEquals("No se ha podido eliminar el player con el id: " + 1234 + " de la base de datos",
                exception.getMessage());
    }

    @DisplayName("GIVEN a player information,WHEN the endpoint is called, THEN create player in the database")
    @Test
    void createPlayer_test(){
        when(playerRepository.save(playerModel)).thenReturn(playerModel);
        PlayerEntity createdPlayer = playerService.createPlayer(playerModel);
        assertThat(playerModel).isEqualTo(createdPlayer);
    }

    @DisplayName("GIVEN a player information,WHEN the endpoint is called, THEN thrown an exception because email was already in the database")
    @Test
    void createPlayerEmailExists_test(){
        when(playerRepository.existsByEmail(playerModel.getEmail())).thenReturn(true);
        assertThatThrownBy(() -> playerService.createPlayer(playerModel)).isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email ya introducido en la base de datos");
    }

    @DisplayName("GIVEN a player information,WHEN the endpoint is called, THEN update player in the database")
    @Test
    void updatePlayerById_test() {
        playerModel.setId(1);
        playerModel.setName("Angel");

        int playerId = 1;
        PlayerEntity playerupdate = new PlayerEntity(1,"Pepito","pepito@123.com",99.56,"122.235.123.54");
        when(playerRepository.findById(1)).thenReturn(Optional.of(playerModel));
        when(playerRepository.existsByEmail(playerupdate.getEmail())).thenReturn(false);
        when(playerRepository.save(playerupdate)).thenReturn(playerupdate);

        PlayerEntity result = playerService.updatePlayer(playerId, playerupdate);

        assertThat(playerupdate.getName()).isEqualTo(result.getName());
    }

    @DisplayName("GIVEN a player information,WHEN the endpoint is called, THEN thrown an exception")
    @Test
    void updatePlayerByIdNoValidId_test() {

        when(playerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.updatePlayer(anyInt(), playerModel))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("User not found");

    }

    @DisplayName("GIVEN a player id and the money,WHEN the endpoint is called, THEN update money")
    @Test
    void addfoundsById_test() {
        PlayerEntity playerupdate = playerModel;
        playerModel.setMoney(250.00);
        when(playerRepository.findById(1)).thenReturn(Optional.of(playerModel));
        when(playerRepository.save(playerupdate)).thenReturn(playerupdate);
        PlayerEntity updatedPlayer = playerService.addfounds(1, 500.00);

        assertThat(updatedPlayer.getMoney()).isEqualTo(750.00);
    }

    @DisplayName("GIVEN a player id and the money,WHEN the endpoint is called, THEN thrown an exception")
    @Test
    void addfoundsByIdNotFound_test() {

        when(playerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.addfounds(anyInt(), 250.00))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("User not found");

    }


}
