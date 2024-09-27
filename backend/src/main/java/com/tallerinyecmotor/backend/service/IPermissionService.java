package com.tallerinyecmotor.backend.service;

import com.tallerinyecmotor.backend.model.Permission;

import java.util.List;
import java.util.Optional;

public interface IPermissionService {
    List findAll();
    /*
     Se puede pensar en él como una caja que puede estar vacía o contener un objeto. Si la caja está vacía,
     se dice que el Optional está «vacío». Si la caja contiene un objeto, se dice que el Optional está «presente»
     */
    Optional findById(Long id);
    Permission save(Permission permission);
    void deleteById(Long id);
    Permission update(Permission permission);
}
