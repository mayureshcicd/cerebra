package com.cerebra.fileprocessor.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_sequence", allocationSize = 1)
    private Long id;


    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_privillege", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "privillege_id"))
    private List<Privilege> privilleges;
}