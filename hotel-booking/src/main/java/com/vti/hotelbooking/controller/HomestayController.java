package com.vti.hotelbooking.controller;


import com.vti.hotelbooking.exception.PhotoRetrievalException;
import com.vti.hotelbooking.model.BookedRoom;
import com.vti.hotelbooking.model.Homestay;
import com.vti.hotelbooking.model.Room;
import com.vti.hotelbooking.response.BookingResponse;
import com.vti.hotelbooking.response.HomestayResponse;
import com.vti.hotelbooking.response.RoomResponse;
import com.vti.hotelbooking.service.BookingService;
import com.vti.hotelbooking.service.HomestayService;
import com.vti.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/homestays")
public class HomestayController {
    private final HomestayService homestayService;
    private final RoomService roomService;
    private final BookingService bookingService;

    @PostMapping("/add/new-homestay")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Homestay> addNewHomestay(
            @RequestParam("homestayName") String homestayName,
            @RequestParam("homestayAddress") String homestayAddress,
            @RequestParam("ownerEmail") String ownerEmail){
        Homestay newHomestay = homestayService.addNewHomestay(homestayName, homestayAddress, ownerEmail);
        return ResponseEntity.ok(newHomestay);
    }

    @PutMapping("/homestay/{homestayId}/add-owner")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateHomestayOwner(
            @PathVariable Long homestayId,
            @RequestParam("ownerId") Long userId){
        homestayService.updateHomestayOwner(homestayId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/all-address")
    public List<String> getAllHomestayAddress(){
        return homestayService.getAllHomestayAddress();
    }

//    @GetMapping("/all")
//    public List<Homestay> getAllHomestay(){
//       return homestayService.getAllHomestay();
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<List<HomestayResponse>> getAllHomestayWithRooms() {
//        List<Homestay> homestays = homestayService.getAllHomestay();
//        List<HomestayResponse> responses = homestays.stream()
//                .map(homestay -> {
//                    try {
//                        return convertToResponse(homestay);
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(responses);
//    }


    @GetMapping("/all")
    public ResponseEntity<List<HomestayResponse>> getAllHomestayWithRooms() throws SQLException {
        List<Homestay> homestays = homestayService.getAllHomestay();
        List<HomestayResponse> responses = new ArrayList<>();
               for( Homestay homestay : homestays){
                   HomestayResponse homestayResponse = convertToResponse(homestay);
                   responses.add(homestayResponse);
               }
        return ResponseEntity.ok(responses);
    }




    @GetMapping("/homestay/{homestayId}")
    public ResponseEntity<Optional<Homestay>> getHomestayById(@PathVariable Long homestayId){
        Optional<Homestay> thisHomestay = homestayService.findByHomestayId(homestayId);
        if (thisHomestay.isPresent()) {
            return ResponseEntity.ok(thisHomestay);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/homestay/owner/{userId}")
    public List<Homestay> getHomestayByOwnerId(@PathVariable Long userId){
        return homestayService.getHomestayByOwnerId(userId);
    }
    @GetMapping(value = "/homestay/image/{homestayId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getHomestayImageById(@PathVariable Long homestayId) throws SQLException {
        byte[] imageBytes = homestayService.getHomestayImageById(homestayId);
        if (imageBytes != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/homestay/{homestayId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<Void> updateHomestayInfo(@PathVariable Long homestayId,
                                       @RequestParam(required = false) String homestayName,
                                       @RequestParam(required = false) String homestayAddress,
                                       @RequestParam(required = false) String description,
                                       @RequestParam(required = false) MultipartFile homestayImage) throws IOException, SQLException {
        byte[] photoBytes = homestayImage != null && !homestayImage.isEmpty() ?
                homestayImage.getBytes() : homestayService.getHomestayImageById(homestayId);
        Blob photoBlob = photoBytes != null && photoBytes.length >0 ? new SerialBlob(photoBytes): null;
        Homestay theHomestay = homestayService.updateHomestayInfo(homestayId, homestayName, homestayAddress, description, photoBytes);
        theHomestay.setHomestayImage(photoBlob);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/delete/homestay/{homestayId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> deleteHomestay(@PathVariable Long homestayId) {
        try {
            homestayService.deleteHomestay(homestayId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete homestay");
        }
    }
    private HomestayResponse convertToResponse(Homestay homestay) throws SQLException {
        HomestayResponse response = new HomestayResponse();
        response.setId(homestay.getId());
        response.setHomestayName(homestay.getHomestayName());
        response.setHomestayAddress(homestay.getHomestayAddress());
        response.setDescription(homestay.getDescription());
        response.setOwner(homestay.getOwner());

        List<Room> rooms = roomService.findRoomsByHomestayId(homestay.getId());
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms){
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0){
                String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(photoBase64);
                roomResponses.add(roomResponse);
            }
        }
        response.setRooms(roomResponses);
        return response;
    }

    private RoomResponse getRoomResponse(Room room) {
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        List<BookingResponse>  bookingInfo = bookings.stream()
                .map(booking -> new BookingResponse(booking.getBookingId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getBookingConfirmationCode())).toList();
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null){
            try{
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            }
            catch (SQLException e){
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        return new RoomResponse(room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.isBooked(),
                photoBytes, bookingInfo);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }

}
