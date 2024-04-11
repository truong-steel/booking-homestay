package com.vti.hotelbooking.service;

import com.vti.hotelbooking.model.Homestay;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public interface HomestayService {

    List<Homestay> getAllHomestay();

    Optional<Homestay> findByHomestayId(Long homestayId);

    Homestay addNewHomestay(String homestayName, String homestayAddress, String ownerEmail);

    void   updateHomestayOwner(Long homestayId, Long userId );
    Homestay updateHomestayInfo(Long homestayId, String homestayName, String homestayAddress,String description, byte[] homestayImageBytes);

    void    deleteHomestay(Long homestayId);

    List<Homestay> getHomestayByOwnerId(Long ownerId);

    byte[] getHomestayImageById(Long roomId) throws SQLException;

    List<String> getAllHomestayAddress();

}
