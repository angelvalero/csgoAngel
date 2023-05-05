package com.csgo.player.it;

import com.csgo.player.dto.PlayerDTO;
import com.csgo.player.entity.InventoryEntity;
import com.csgo.player.entity.PlayerEntity;
import com.csgo.player.service.PlayerService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PlayerServiceTest_IT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PlayerService playerService;

    PlayerEntity playerModel;
    InventoryEntity inventoryModel;
    PlayerDTO playerDTO;
    ObjectMapper objectMapper= new ObjectMapper();


    @RegisterExtension
    static WireMockExtension inventoryWireMock = WireMockExtension.newInstance().options(wireMockConfig().port(8088)).build();

    @BeforeEach
    public void createPlayer() {
        playerModel = new PlayerEntity(1, "Shadow","angel@lpl.com", 30.00, "224.24.171.0");
        inventoryModel = new InventoryEntity(1,1, "Rocket Pop, MP9", "Minimal Wear (MW)", false, 690.52, true);
        playerDTO = new PlayerDTO(1,"Shadow","angel@lpl.com", 46.58, List.of(inventoryModel));
    }

    @DisplayName("GIVEN a player information, WHEN method is called, THEN is expected to create player")
    @Test
    void createPlayerBasic_IT() throws Exception{
        String json = objectMapper.writeValueAsString(playerModel);

        mockMvc.perform(post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isCreated());
    }

    @DisplayName("GIVEN a player information, WHEN method is called, THEN is expected to update player")
    @Test
    public void updatePlayersById_IT() throws Exception {

        mockMvc.perform(patch("/players/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Pablo\", \"lastname\": \"Garcia\" }"))
                .andExpect(status().isCreated());
    }

    @DisplayName("GIVEN nothing, WHEN method is called, THEN is expected to retrieve all players")
    @Test
    void getAllPlayers_IT() throws Exception{

        mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

    }


    @DisplayName("GIVEN an id, WHEN method is called, THEN is expected to retrieve player with matches that id")
    @Test
    void getPlayerId_IT() throws Exception{

        mockMvc.perform(get("/players/13"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{id: 13, name: Winnie Talkington}"));

    }

    @DisplayName("GIVEN an id, WHEN method is called, THEN is expected not found")
    @Test
    void getPlayerIdNotFound_IT() throws Exception{

        mockMvc.perform(get("/players/1111"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));

    }

    @DisplayName("GIVEN an id, WHEN method is called, THEN delete player with matches that id")
    @Test
    void deletePlayerId_IT() throws Exception{

        mockMvc.perform(delete("/players/13"))
                .andExpect(status().isNoContent());

    }

    @DisplayName("GIVEN an id, WHEN method is called, THEN throws an error")
    @Test
    void deletePlayerIdNotFound_IT() throws Exception{

        mockMvc.perform(delete("/players/13122"))
                .andExpect(status().isNotFound());

    }

    @DisplayName("GIVEN an id, WHEN method is called, THEN delete player with matches that id")
    @Test
    void getPlayerEmail_IT() throws Exception{

        mockMvc.perform(get("/players/email/alpl@gft.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{id: 13, name: Winnie Talkington}"));

    }

    @DisplayName("GIVEN an id, WHEN method is called, THEN delete player with matches that id")
    @Test
    void getPlayerEmailNotFound_IT() throws Exception{

        mockMvc.perform(get("/players/email/adsaed"))
                .andExpect(status().isNotFound());

    }

    @DisplayName("GIVEN an id and money, WHEN method is called, THEN update the player founds")
    @Test
    void addfounds_IT() throws Exception{

        mockMvc.perform(patch("/addfounds/12/30.00"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.money").value(58.94));

    }

    @DisplayName("GIVEN an id and money, WHEN method is called, THEN throw not found")
    @Test
    void addfoundsNotFound_IT() throws Exception{

        mockMvc.perform(patch("/addfounds/12132/30.00"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @DisplayName("GIVEN a id,WHEN multiples methods are called,THEN return expected answers")
    @Test
    void End2End_test() throws Exception{
        playerModel.setId(101);
        String json = objectMapper.writeValueAsString(playerModel);
        
        mockMvc.perform(get("/players/101"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isCreated());

        mockMvc.perform(get("/players/101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.name").value("Shadow"))
                .andExpect(jsonPath("$.money").value(30.00));


        mockMvc.perform(patch("/addfounds/101/29.90"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.money").value(59.90));

        mockMvc.perform(delete("/players/101"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/players/101"))
                .andExpect(status().isNotFound());
    }






}
