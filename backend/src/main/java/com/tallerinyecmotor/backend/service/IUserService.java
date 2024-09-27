package com.tallerinyecmotor.backend.service;


import com.tallerinyecmotor.backend.model.UserSec;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    public List findAll();
    public Optional findById(Long id);
    public UserSec save(UserSec userSec);
    public void deleteById(Long id);
    public void update(UserSec userSec);
    public String encriptPassword(String password);
}
