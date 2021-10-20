package com.finskayaylochka.repository;

import com.finskayaylochka.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */
@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

  @Query("SELECT p FROM Phone p WHERE p.user.id = :appUserId")
  List<Phone> findByAppUserId(@Param("appUserId") Long appUserId);

  boolean existsByNumber(String number);

}
