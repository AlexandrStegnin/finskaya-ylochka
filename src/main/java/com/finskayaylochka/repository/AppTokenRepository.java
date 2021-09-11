package com.finskayaylochka.repository;

import com.finskayaylochka.model.AppToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface AppTokenRepository extends JpaRepository<AppToken, Long> {

    AppToken findByAppName(String appName);

}
