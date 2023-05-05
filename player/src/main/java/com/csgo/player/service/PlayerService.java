

package com.csgo.player.service;

import com.csgo.player.dto.PlayerDTO;
import com.csgo.player.entity.InventoryEntity;
import com.csgo.player.entity.PlayerEntity;
import com.csgo.player.exception.DuplicateEmailException;
import com.csgo.player.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.modelmapper.ModelMapper;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlayerService {
    private PlayerRepository playerRepository;

    private ModelMapper modelMapper;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.modelMapper = new ModelMapper();
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
        Optional<PlayerEntity> player =  Optional.ofNullable(playerRepository.findByEmail(email));

        if (player.isEmpty()){
            log.error("Not found a player with that email");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario con el email: "+email+" no encontrado");
        }
        log.info("Return a Player");
        return player.get();
    }
    public void deletePlayerById(int id){
        try {
            playerRepository.deleteById(id);
            log.info("Player eliminado");
        }catch(Exception e){
            log.error("No se ha eliminado el player");
            throw new EntityNotFoundException("No se ha podido eliminar el player con el id: "+id+" de la base de datos");

        }
    }

    public PlayerEntity createPlayer(PlayerEntity player){
        String email = player.getEmail();

        if(playerRepository.existsByEmail(email)){
            throw new DuplicateEmailException("Email ya introducido en la base de datos");
        }
        log.info("Player creado");
        return playerRepository.save(player);
    }

    public PlayerEntity updatePlayer(int id, PlayerEntity player){


        PlayerEntity existingPlayer = playerRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if(playerRepository.existsByEmail(player.getEmail())) {
            throw new DuplicateEmailException("The email " + player.getEmail() + " is already in use");
        }
        player.setId(existingPlayer.getId());
        modelMapper.map(player, existingPlayer);
        log.info("Updated user with id " + id);
        return playerRepository.save(existingPlayer);

    }


    public PlayerEntity addfounds(int id, Double money){


        PlayerEntity existingPlayer = playerRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        Double existingbalance= existingPlayer.getMoney();
        Double newbalance = existingbalance+money;
        existingPlayer.setMoney(newbalance);

        return playerRepository.save(existingPlayer);
    }

    public Mono<List<InventoryEntity>> getInventories() {
        WebClient webClient = WebClient.create();

        Mono<List<InventoryEntity>> response = webClient.get()
                .uri("http://localhost:8081/inventories")
                .retrieve()
                .bodyToFlux(InventoryEntity.class)
                .collectList();

        return response;
    }

    public Mono<PlayerDTO> findPlayerByIdWithInventory(Integer id) {
        Optional<PlayerEntity> player = playerRepository.findById(id);
        if (player.isEmpty()) {
            log.error("Not found a player with that ID");
            throw new EntityNotFoundException("Usuario con el id: " + id + " no encontrado");
        }
        log.info("Return a Player with Inventory");

        Mono<List<InventoryEntity>> inventory = getInventories()
                .map(inventories -> inventories.stream()
                        .filter(inv -> inv.getPlayerId().equals(id))
                        .collect(Collectors.toList()));

        return  inventory.map(inv ->{
            PlayerDTO playerDTO = new PlayerDTO();
            playerDTO.setId(player.get().getId());
            playerDTO.setName(player.get().getName());
            playerDTO.setEmail(player.get().getEmail());
            playerDTO.setMoney(player.get().getMoney());
            playerDTO.setInventory(inv);
            return playerDTO;
        });
    }

}
