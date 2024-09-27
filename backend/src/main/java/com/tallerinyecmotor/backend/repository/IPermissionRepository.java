package com.tallerinyecmotor.backend.repository;

import com.tallerinyecmotor.backend.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPermissionRepository extends JpaRepository<Permission,Long> {
}
