package com.vti.hotelbooking.service.impl;

import com.sun.security.auth.UserPrincipal;
import com.vti.hotelbooking.exception.InternalServerException;
import com.vti.hotelbooking.exception.ResourceNotFoundException;
import com.vti.hotelbooking.model.Homestay;
import com.vti.hotelbooking.model.Room;
import com.vti.hotelbooking.repository.RoomRepository;
import com.vti.hotelbooking.service.HomestayService;
import com.vti.hotelbooking.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor

public class RoomServiceImpl implements RoomService {

    @Autowired
    private final RoomRepository  roomRepository;
    private final HomestayService homestayService;

    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice, Long homestayId) throws SQLException, IOException {
        Room room = new Room();
        if(homestayId != null) {
            Optional<Homestay> thisHomestay = homestayService.findByHomestayId(homestayId);

            if (thisHomestay.isPresent()) {
                Homestay theHomestay = new Homestay(thisHomestay.get().getId(), thisHomestay.get().getHomestayImage(),
                        thisHomestay.get().getHomestayName(), thisHomestay.get().getHomestayAddress(),
                        thisHomestay.get().getDescription(), thisHomestay.get().getOwner(),
                        thisHomestay.get().getRooms());
                room.setHomestay(theHomestay);
            }
        }
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
        if (!file.isEmpty()){
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if (theRoom.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Room not found!");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if (photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

//    @Override
//    public void deleteRoom(Long roomId) {
//        // Check if room exists
//        if (roomRepository.existsById(roomId)) {
//            // Room exists, proceed with deletion
//            roomRepository.deleteRoomById(roomId);
//        } else {
//            // Room does not exist, throw IllegalArgumentException
//            throw new IllegalArgumentException("Room with ID " + roomId + " does not exist");
//        }
//    }

    public void deleteRoom(Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalUsername = authentication.getName();

        Room room = roomRepository.findById(roomId).get();
        String ownerEmail = room.getHomestay().getOwner().getEmail();
        if (principalUsername.equals(ownerEmail)) {
            roomRepository.deleteRoomById(roomId);
        } else {
            throw new AccessDeniedException("You don't have permission to delete this room");
        }
}

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalUsername = authentication.getName();

        Room room = roomRepository.findById(roomId).get();
        String ownerEmail = room.getHomestay().getOwner().getEmail();
        if (principalUsername.equals(ownerEmail)) {
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (photoBytes != null && photoBytes.length > 0) {
                try {
                    room.setPhoto(new SerialBlob(photoBytes));
                } catch (SQLException ex) {
                    throw new InternalServerException("Fail updating room");
                }
            }
            return roomRepository.save(room);
        } else {
            throw new AccessDeniedException("You don't have permission to update this room");
        }
    }


    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailableRoomsByDatesAndType(checkInDate, checkOutDate, roomType);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> findRoomsByHomestayId(Long homestayId) {
        return roomRepository.findAllByHomestayId(homestayId);
    }

    @Override
    public List<Room> getAvailableRoomsByAddress(String address, LocalDate checkInDate, LocalDate checkOutDate) {
        return roomRepository.findAvailableRoomsByAddress(address, checkInDate, checkOutDate);
    }


}
