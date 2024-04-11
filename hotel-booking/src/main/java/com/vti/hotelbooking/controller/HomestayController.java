package com.vti.hotelbooking.controller;


import com.vti.hotelbooking.model.Homestay;
import com.vti.hotelbooking.service.HomestayService;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/homestays")
public class HomestayController {
    private final HomestayService homestayService;

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

    @GetMapping("/all")
    public List<Homestay> getAllHomestay(){
       return homestayService.getAllHomestay();
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

}
