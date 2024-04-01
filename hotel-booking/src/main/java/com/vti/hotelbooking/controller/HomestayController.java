package com.vti.hotelbooking.controller;


import com.vti.hotelbooking.model.Homestay;
import com.vti.hotelbooking.service.HomestayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/homestay/{homestayId}/update")
    public Homestay updateHomestayInfo(@PathVariable Long homestayId,
                                       @RequestParam("homestayName") String homestayName,
                                       @RequestParam("homestayAddress") String homestayAddress){
        return homestayService.updateHomestayInfo(homestayId, homestayName, homestayAddress);
    }
}
