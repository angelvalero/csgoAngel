package com.csgo.player.dto;

import com.csgo.player.entity.InventoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDTO
{
    private Integer id;
    private String name;
    private String email;
    private Double money;
    private List<InventoryEntity> inventory;



}
