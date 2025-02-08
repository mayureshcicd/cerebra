package com.cerebra.fileprocessor.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "privilege")
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priv_seq")
    @SequenceGenerator(name = "priv_seq", sequenceName = "priv_sequence", allocationSize = 1)
    private Long id;

    private String name;

}