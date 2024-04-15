package com.vti.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.util.List;

@Entity
@Table(name = "homestay")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Homestay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "homestay_id")
    private Long id;

    @Lob
    @Column(name = "homstay_image")
    private Blob homestayImage = null;

    private String homestayName;
    private String homestayAddress;
    private String description = null;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    private User owner;

    @JsonIgnore
    @OneToMany(mappedBy = "homestay", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JsonManagedReference
    private List<Room>  rooms;
}
