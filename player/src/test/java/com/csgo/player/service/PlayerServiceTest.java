package com.csgo.player.service;


import com.csgo.player.entity.PlayerEntity;
import com.csgo.player.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;


import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @InjectMocks
    @Autowired
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    PlayerEntity playerModel;

    @BeforeEach
    public void createPlayer8() {
        playerModel = new PlayerEntity(1, "Shadow", "wbette0@feedburner.com", 46.58, "224.24.171.0");

    }

    @DisplayName("Given an player id, then return a player, when id's player match")
    @Test
    void getUserById_test() {

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
    void getUserByIdNotFound_test() {

        when(playerRepository.findById(1234)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            playerService.findPlayerById(1234);
        });

        assertEquals("Usuario con el id: " + 1234 + " no encontrado", exception.getMessage());

    }

    @DisplayName("Given an player email, then return a email player, when email's player match")
    @Test
    void getUserByEmail_test() {


        playerModel.setEmail("angelvp@lpl.com");

        when(playerRepository.findByEmail("angelvp@lpl.com")).thenReturn(Optional.of(playerModel));

        PlayerEntity result = playerService.findPlayerByEmail("angelvp@lpl.com");

        assertNotNull(result);
        assertEquals(playerModel.getEmail(), result.getEmail());

        verify(playerRepository, times(1)).findByEmail("angelvp@lpl.com");
    }

    @DisplayName("Given an player emmail, then return a not found")
    @Test
    void getUserByEmailNotFound_test() {


        String email ="notfound@test";

        when(playerRepository.findByEmail(email)).thenReturn(null);


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            playerService.findPlayerByEmail(email);
        });

        assertEquals("Usuario con el email: " + email + " no encontrado", exception.getMessage());

    }


}
