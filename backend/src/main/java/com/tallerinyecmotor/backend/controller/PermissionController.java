package com.tallerinyecmotor.backend.controller;

import com.tallerinyecmotor.backend.model.Permission;
import com.tallerinyecmotor.backend.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    @GetMapping
    public ResponseEntity<List> getAllPermissions() {
        List permissions = permissionService.findAll();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPermissionById(@PathVariable Long id) {
        Optional permission = permissionService.findById(id);
        //primero mapeo el permiso a ver si tiene algo y sino trajo ningun permiso y sino devuelve un notfound
        //creado con un responseEntity con el build
        return (ResponseEntity) permission.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/crear")
    public ResponseEntity createPermission(@RequestBody Permission permission) {
        Permission newPermission = permissionService.save(permission);
        return ResponseEntity.ok(newPermission);
    }

}
