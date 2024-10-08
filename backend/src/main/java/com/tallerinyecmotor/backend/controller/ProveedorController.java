package com.tallerinyecmotor.backend.controller;


import com.tallerinyecmotor.backend.dto.DTOProveedor;
import com.tallerinyecmotor.backend.dto.RespuestaService;
import com.tallerinyecmotor.backend.model.Proveedor;
import com.tallerinyecmotor.backend.service.IProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProveedorController {
    @Autowired
    private IProveedorService iProveedor;

    @GetMapping("/proveedor/all")
    public ResponseEntity<?> getProveedor(){

        try{
            List<Proveedor> proveedores = iProveedor.getProveedor();
            if (proveedores.isEmpty()){

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay proveedores cargadas en la base de datos");

            }else {
                return new ResponseEntity<List<Proveedor>>(proveedores, HttpStatus.OK);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/proveedor/crear")
    public ResponseEntity<?> createProveedor(@Valid @RequestBody DTOProveedor proveedorDto, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try{
            RespuestaService res = iProveedor.saveProveedor(proveedorDto);

            if (res.isExito() == true){
                return ResponseEntity.status(HttpStatus.CREATED).body(res);
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @DeleteMapping("/proveedor/eliminar/{id}")
    public ResponseEntity<?> deleteProveedor(@PathVariable Long id){

        try{
            Proveedor proveedorFound = iProveedor.findProveedor(id);

            if(proveedorFound==null){
                RespuestaService resNotFound = new RespuestaService(false,"El proveedor que se quiere eliminar no existe según el id enviado","");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resNotFound);
            }else {
                RespuestaService res = iProveedor.deleteProveedor(id);
                if (res.isExito() == true) {
                    return ResponseEntity.status(HttpStatus.OK).body(res);
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
                }
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PatchMapping("/proveedor/editar")
    public ResponseEntity<?> updateProveedor(@RequestBody DTOProveedor proveedor){

        try{
            iProveedor.updateProveedor(proveedor.getId(), proveedor.getCuit(), proveedor.getTel(), proveedor.getDireccion(), proveedor.getNombre(), proveedor.getEmail());

            Proveedor proveedorUpdated = iProveedor.findProveedor(proveedor.getId());

            return new ResponseEntity<Proveedor>(proveedorUpdated, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping("/proveedor/get-by-id/{id}")
    public ResponseEntity<?> getProveedorById(@PathVariable Long id){

        try{
            Proveedor proveedorFound = iProveedor.findProveedor(id);

            if(proveedorFound==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El proveedor que se quiere encontrar no existe según el id enviado");
            }else {
                return new ResponseEntity<Proveedor>(proveedorFound ,HttpStatus.FOUND);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
}
