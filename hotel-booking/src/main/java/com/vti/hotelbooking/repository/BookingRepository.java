package com.vti.hotelbooking.repository;

import com.vti.hotelbooking.model.BookedRoom;
import com.vti.hotelbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {

    List<BookedRoom> findByRoomId(Long roomId);

    Optional<BookedRoom> findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> findByUser(User user);
}
