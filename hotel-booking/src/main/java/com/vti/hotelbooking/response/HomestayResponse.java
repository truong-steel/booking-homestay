package com.vti.hotelbooking.response;

import com.vti.hotelbooking.model.User;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
public class HomestayResponse {
    private Long id;
    private String homestayName;
    private String homestayAddress;
    private String description;

    private User owner;
    private List<RoomResponse> rooms;

    public HomestayResponse() {
    }

    public HomestayResponse(Long id, String homestayName, String homestayAddress, String description) {
        this.id = id;
        this.homestayName = homestayName;
        this.homestayAddress = homestayAddress;
        this.description = description;
    }

    public HomestayResponse(Long id, String homestayName, String homestayAddress, String description, User owner, List<RoomResponse> rooms) {
        this.id = id;
        this.homestayName = homestayName;
        this.homestayAddress = homestayAddress;
        this.description = description;
        this.owner = owner;
        this.rooms = rooms;
    }
}
