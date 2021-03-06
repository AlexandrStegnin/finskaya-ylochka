package com.finskayaylochka.repository;

import com.finskayaylochka.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<AppRole, Long> {
}
