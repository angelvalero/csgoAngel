package com.csgo.player.service;


import com.csgo.player.controller.PlayerController;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.ArrayList;
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


    private ModelMapper modelMapper;
    PlayerEntity playerModel;


    @BeforeEach
    public void createPlayer8() {
        playerModel = new PlayerEntity(1, "Shadow","angel@lpl.com", 46.58, "224.24.171.0");

    }

    @DisplayName("Return a list of players")
    @Test
    void getAllPlayers_test() {
        List <PlayerEntity> expectedPlayers = new ArrayList<PlayerEntity>();
        expectedPlayers.add(playerModel);
        expectedPlayers.add(playerModel);

        when(playerRepository.findAll()).thenReturn(expectedPlayers);

        List <PlayerEntity> actualPlayers = playerService.findAll();

        assertThat(expectedPlayers).isEqualTo(actualPlayers);


    }


    @DisplayName("Given an player id, then return a player, when id's player match")
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

    @DisplayName("Given an player id, then return a not found")
    @Test
    void getPlayerByIdNotFound_test() {

        when(playerRepository.findById(1234)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            playerService.findPlayerById(1234);
        });

        assertEquals("Usuario con el id: " + 1234 + " no encontrado", exception.getMessage());

    }

    @DisplayName("Given an player email, then return a email player, when email's player match")
    @Test
    void getPlayerByEmail_test() {


        playerModel.setEmail("angelvp@lpl.com");

        when(playerRepository.findByEmail("angelvp@lpl.com")).thenReturn(playerModel);

        PlayerEntity result = playerService.findPlayerByEmail("angelvp@lpl.com");

        assertNotNull(result);
        assertEquals(playerModel.getEmail(), result.getEmail());

        verify(playerRepository, times(1)).findByEmail("angelvp@lpl.com");
    }

    @DisplayName("Given an player email, then return a not found")
    @Test
    void getPlayerByEmailNotFound_test() {


        when(playerRepository.findByEmail("notfound@test")).thenReturn(null);


        assertThatThrownBy(() -> playerService.findPlayerByEmail("notfound@test"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario con el email: notfound@test no encontrado");


    }



    @DisplayName("Given an player id, then delete this player and return 204")
    @Test
    void deletePlayerbyId_test() {
        playerService.deletePlayerById(1);
        verify(playerRepository, times(1)).deleteById(1);
    }

    @DisplayName("Given an player id, then delete this player and return 204")
    @Test
    void deletePlayerbyIdNot_test() {
        doThrow(EmptyResultDataAccessException.class).when(playerRepository).deleteById(1234);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> playerService.deletePlayerById(1234));

        assertEquals("No se ha podido eliminar el player con el id: " + 1234 + " de la base de datos",
                exception.getMessage());
    }

    @DisplayName("Given a Player, then create this player in database")
    @Test
    void createPlayer_test(){
        when(playerRepository.save(playerModel)).thenReturn(playerModel);
        PlayerEntity createdPlayer = playerService.createPlayer(playerModel);
        assertThat(playerModel).isEqualTo(createdPlayer);
    }

    @DisplayName("Given a Player, then thrown an error because email is already un database")
    @Test
    void createPlayerEmailExists_test(){
        when(playerRepository.existsByEmail(playerModel.getEmail())).thenReturn(true);
        assertThatThrownBy(() -> playerService.createPlayer(playerModel)).isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email ya introducido en la base de datos");
    }


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

    @Test
    void updatePlayerByIdNoValidId_test() {

        when(playerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.updatePlayer(anyInt(), playerModel))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("User not found");

    }
    @Test
    void addfoundsById_test() {
        PlayerEntity playerupdate = playerModel;
        playerModel.setMoney(250.00);
        when(playerRepository.findById(1)).thenReturn(Optional.of(playerModel));
        when(playerRepository.save(playerupdate)).thenReturn(playerupdate);
        PlayerEntity updatedPlayer = playerService.addfounds(1, 500.00);

        assertThat(updatedPlayer.getMoney()).isEqualTo(750.00);
    }

    @Test
    void addfoundsByIdNotFound_test() {

        when(playerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.addfounds(anyInt(), 250.00))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("User not found");

    }




}
