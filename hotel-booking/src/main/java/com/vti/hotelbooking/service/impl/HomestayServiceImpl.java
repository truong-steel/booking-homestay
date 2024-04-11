package com.vti.hotelbooking.service.impl;

import com.vti.hotelbooking.exception.HomestayNotFoundException;
import com.vti.hotelbooking.exception.InternalServerException;
import com.vti.hotelbooking.exception.ResourceNotFoundException;
import com.vti.hotelbooking.model.Homestay;
import com.vti.hotelbooking.model.Room;
import com.vti.hotelbooking.model.User;
import com.vti.hotelbooking.repository.HomestayRepository;
import com.vti.hotelbooking.repository.UserRepository;
import com.vti.hotelbooking.service.HomestayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class HomestayServiceImpl implements HomestayService {

    @Autowired
    private final HomestayRepository homestayRepository;
    private final UserRepository userRepository;
    @Override
    public List<Homestay> getAllHomestay() {
        return homestayRepository.findAll();
    }

    @Override
    public Optional<Homestay> findByHomestayId(Long homestayId) {
        return homestayRepository.findById(homestayId);
    }


    @Override
    public Homestay addNewHomestay(String homestayName, String homestayAddress, String ownerEmail) {
        User owner = null;
        if (ownerEmail != null && !ownerEmail.isEmpty()) {
            owner = userRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> new RuntimeException("Owner not found for email: " + ownerEmail));
        }
        Homestay homestay = new Homestay();
        homestay.setHomestayName(homestayName);
        homestay.setHomestayAddress(homestayAddress);
        homestay.setOwner(owner);

        return homestayRepository.save(homestay);
    }

    public void updateHomestayOwner(Long homestayId, Long userId){
        homestayRepository.updateHomestayOwnerById(homestayId, userId);
    }

    @Override
    public Homestay updateHomestayInfo(Long homestayId, String homestayName, String homestayAddress, String description, byte[] homestayImageBytes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalUsername = authentication.getName();
        Homestay homestayToUpdate = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new HomestayNotFoundException("Homestay Not Found with ID: " + homestayId));

        String ownerEmail = homestayToUpdate.getOwner().getEmail();
        if (principalUsername.equals(ownerEmail)) {
            if (homestayName != null) homestayToUpdate.setHomestayName(homestayName);
            if (homestayAddress != null) homestayToUpdate.setHomestayAddress(homestayAddress);
            if (description != null) homestayToUpdate.setDescription(description);
            if (homestayImageBytes != null && homestayImageBytes.length > 0) {
                try {
                    homestayToUpdate.setHomestayImage(new SerialBlob(homestayImageBytes));
                } catch (SQLException ex) {
                    throw new InternalServerException("Fail updating homestay");
                }
            }
            return homestayRepository.save(homestayToUpdate);
        } else {
            throw new AccessDeniedException("You don't have permission to update this homestay");
        }

    }

    @Override
    public void deleteHomestay(Long homestayId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principalUsername = authentication.getName();

        Homestay homestay = homestayRepository.findById(homestayId).get();
        String ownerEmail = homestay.getOwner().getEmail();
        if (principalUsername.equals(ownerEmail)) {
            homestayRepository.deleteById(homestayId);
        } else {
            throw new AccessDeniedException("You don't have permission to delete this homestay");
        }
    }

    @Override
    public List<Homestay> getHomestayByOwnerId(Long ownerId) {
        return homestayRepository.findHomestaysByOwnerId(ownerId);
    }

    @Override
    public byte[] getHomestayImageById(Long homestayId) throws SQLException {
        Optional<Homestay> theHomestay = homestayRepository.findById(homestayId);
        if (theHomestay.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Homestay not found!");
        }
        Blob photoBlob = theHomestay.get().getHomestayImage();
        if (photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    @Override
    public List<String> getAllHomestayAddress() {
        return homestayRepository.findDistinctHomestayAddress();
    }

}
