package com.csgo.player.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gun_id")
    private Integer gunId;

    @Column(name = "player_id")
    private Integer playerId;
    @Column(name = "gun_name")
    private String gunName;
    @Column(name = "gun_status")
    private String gunStatus;
    @Column(name = "gun_ST")
    private boolean isStarTrack;
    @Column(name = "gun_price")
    private double gunPrice;
    @Column(name = "on_sale")
    private boolean isOnSale;

}
