package com.vti.hotelbooking.service;

import com.vti.hotelbooking.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public interface RoomService {
    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, Long homestayId) throws SQLException, IOException;

    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;

    void deleteRoom(Long roomId);

    Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes);

    Optional<Room> getRoomById(Long roomId);

    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    List<String> getAllRoomTypes();

    List<Room> findRoomsByHomestayId(Long homestayId);

    List<Room> getAvailableRoomsByAddress(String address, LocalDate checkInDate, LocalDate checkOutDate);

}
