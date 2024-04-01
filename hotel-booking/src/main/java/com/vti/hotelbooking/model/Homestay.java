package com.vti.hotelbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "homestay")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Homestay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "homestay_id")
    private Long id;

    private String homestayName;
    private String homestayAddress;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private User owner;

    @OneToMany(mappedBy = "homestay", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<Room>  rooms;
}
