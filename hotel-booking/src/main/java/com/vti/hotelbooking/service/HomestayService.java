package com.vti.hotelbooking.service;

import com.vti.hotelbooking.model.Homestay;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface HomestayService {

    List<Homestay> getAllHomestay();

    Optional<Homestay> findByHomestayId(Long homestayId);

    Homestay addNewHomestay(String homestayName, String homestayAddress);

    void   updateHomestayOwner(Long homestayId, Long userId );
    Homestay updateHomestayInfo(Long homestayId, String homestayName, String homestayAddress);

    void    deleteHomestay(Long homestayId);

    List<Homestay> getHomestayByOwnerId(Long ownerId);


}
