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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Homestay> addNewHomestay(
            @RequestParam("homestayName") String homestayName,
            @RequestParam("homestayAddress") String homestayAddress){
        Homestay newHomestay = homestayService.addNewHomestay(homestayName, homestayAddress);
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
            // Trả về 404 Not Found nếu không tìm thấy hình ảnh
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/homestay/{homestayId}/update")
//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<Void> updateHomestayInfo(@PathVariable Long homestayId,
                                       @RequestParam("homestayName") String homestayName,
                                       @RequestParam("homestayAddress") String homestayAddress,
                                       @RequestParam("description") String description,
                                       @RequestParam("homestayImage") MultipartFile homestayImage) throws IOException, SQLException {
        byte[] photoBytes = homestayImage != null && !homestayImage.isEmpty() ?
                homestayImage.getBytes() : homestayService.getHomestayImageById(homestayId);
        Blob photoBlob = photoBytes != null && photoBytes.length >0 ? new SerialBlob(photoBytes): null;
        Homestay theHomestay = homestayService.updateHomestayInfo(homestayId, homestayName, homestayAddress, description, photoBytes);
        theHomestay.setHomestayImage(photoBlob);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
