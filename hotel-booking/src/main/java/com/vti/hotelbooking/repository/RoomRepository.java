package com.vti.hotelbooking.repository;

import com.vti.hotelbooking.model.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query ("SELECT DISTINCT r.roomType FROM Room r")
    List<String> findDistinctRoomTypes();

    @Query(" SELECT r FROM Room r " +
            " WHERE r.roomType LIKE %:roomType% " +
            " AND r.id NOT IN (" +
            "  SELECT br.room.id FROM BookedRoom br " +
            "  WHERE ((br.checkInDate <= :checkOutDate) AND (br.checkOutDate >= :checkInDate))" +
            ")")

    List<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    @Query("SELECT r FROM Room r WHERE r.homestay.id = :homestayId")
    List<Room> findAllByHomestayId(@Param("homestayId") Long homestayId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Room r WHERE r.room_id = :roomId", nativeQuery = true)
    void deleteRoomById(@Param("roomId") Long roomId);

    @Query(value = "SELECT DISTINCT r.* FROM room r " +
            "JOIN homestay h ON r.homestay_id = h.homestay_id " +
            "LEFT JOIN booked_room br ON r.room_id = br.room_id " +
            "WHERE h.homestay_address = :homestayAddress " +
            "AND (br.checkin_date IS NULL OR :checkOutDate <= br.checkin_date OR :checkInDate >= br.checkout_date)",
            nativeQuery = true)
    List<Room> findAvailableRoomsByAddress(
            @Param("homestayAddress") String homestayAddress,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);
}

