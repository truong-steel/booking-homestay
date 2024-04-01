package com.vti.hotelbooking.repository;

import com.vti.hotelbooking.model.Homestay;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long>  {
    @Query(value = "SELECT * FROM homestay h WHERE h.owner_id = :userId", nativeQuery = true)
    List<Homestay> findHomestaysByOwnerId(@Param("userId") Long userId);


    @Modifying
    @Transactional
    @Query("UPDATE Homestay h SET h.owner.id = :userId WHERE h.id = :homestayId")
    void updateHomestayOwnerById(@Param("homestayId") Long homestayId, @Param("userId") Long userId);


}
