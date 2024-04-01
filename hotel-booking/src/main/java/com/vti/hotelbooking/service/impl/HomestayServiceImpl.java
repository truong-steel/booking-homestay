package com.vti.hotelbooking.service.impl;

import com.vti.hotelbooking.exception.HomestayNotFoundException;
import com.vti.hotelbooking.model.Homestay;
import com.vti.hotelbooking.repository.HomestayRepository;
import com.vti.hotelbooking.repository.UserRepository;
import com.vti.hotelbooking.service.HomestayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class HomestayServiceImpl implements HomestayService {

    @Autowired
    private final HomestayRepository homestayRepository;
    @Override
    public List<Homestay> getAllHomestay() {
        return homestayRepository.findAll();
    }

    @Override
    public Optional<Homestay> findByHomestayId(Long homestayId) {
        return homestayRepository.findById(homestayId);
    }


    @Override
    public Homestay addNewHomestay(String homestayName, String homestayAddress) {
        Homestay homestay = new Homestay();
        homestay.setHomestayName(homestayName);
        homestay.setHomestayAddress(homestayAddress);

        return homestayRepository.save(homestay);
    }

    public void updateHomestayOwner(Long homestayId, Long userId){
        homestayRepository.updateHomestayOwnerById(homestayId, userId);
    }

    @Override
    public Homestay updateHomestayInfo(Long homestayId, String homestayName, String homestayAddress) {
        Homestay homestayToUpdate = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new HomestayNotFoundException("Homestay Not Found with ID: " + homestayId));
        homestayToUpdate.setHomestayName(homestayName);
        homestayToUpdate.setHomestayAddress(homestayAddress);
        return homestayRepository.save(homestayToUpdate);
    }

    @Override
    public void deleteHomestay(Long homestayId) {

    }

    @Override
    public List<Homestay> getHomestayByOwnerId(Long ownerId) {
        return homestayRepository.findHomestaysByOwnerId(ownerId);
    }
}
