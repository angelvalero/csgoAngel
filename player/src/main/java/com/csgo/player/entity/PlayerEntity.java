package com.csgo.player.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(name = "player")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "name cannot be null")
    @NonNull
    private String name;


    @Column(unique = true)
    @Email(message = "please provide a valid email")
    @NotNull(message = "name cannot be null")
    @NonNull
    private String email;


    private Double money;


    @NotNull(message = "name cannot be null")
    @NonNull
    private String ip_address;



}
