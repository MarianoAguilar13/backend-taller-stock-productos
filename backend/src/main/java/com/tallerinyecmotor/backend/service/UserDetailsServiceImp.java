package com.tallerinyecmotor.backend.service;

import com.tallerinyecmotor.backend.dto.AuthLoginRequestDTO;
import com.tallerinyecmotor.backend.dto.AuthResponseDTO;
import com.tallerinyecmotor.backend.model.UserSec;
import com.tallerinyecmotor.backend.repository.IUserRepository;
import com.tallerinyecmotor.backend.utils.JWTUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {

        //tenemos User sec y necesitamos devolver UserDetails
        //traemos el usuario de la bd
        UserSec userSec = null;

        //ATENCION le puse el sneakyThrow cualquier problema cambiar
        userSec = (UserSec) userRepo.findUserEntityByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + "no fue encontrado"));


        //con GrantedAuthority Spring Security maneja permisos
        List<GrantedAuthority> authorityList = new ArrayList<>();

        //Programación funcional a full
        //tomamos roles y los convertimos en SimpleGrantedAuthority para poder agregarlos a la authorityList
        //con la primera palabra ROLE_ especifica springsecurity que ese simpleGrantedAuthority va a ser de un ROLE
        userSec.getRolesList()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole()))));


        //ahora tenemos que agregar los permisos
        //se convierte cada prmiso se conviente en un simpleGrandAuthority
        userSec.getRolesList().stream()
                .flatMap(role -> role.getPermissionsList().stream()) //acá recorro los permisos de los roles
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));

        //retornamos el usuario en formato Spring Security con los datos de nuestro userSec
        return new User(userSec.getUsername(),
                userSec.getPassword(),
                userSec.isEnabled(),
                userSec.isAccountNotExpired(),
                userSec.isCredentialNotExpired(),
                userSec.isAccountNotLocked(),
                authorityList);
    }

    //cheque tanto la contraseña como el usuario son correcto, si es asi crea un token authentication
    public Authentication authenticate(String username,String password){

        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails==null){
            throw new BadCredentialsException("Invalid usarname or password");
        }
        if (!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("Invalid usarname or password");
        }

        return new UsernamePasswordAuthenticationToken(username,userDetails.getPassword(),userDetails.getAuthorities());
    }

    //este metodo me sirve para chequear el login del user si la contraseña y usuario coinciden
    //si sale toodo bien entonce crea un token y lo devuelve
    public AuthResponseDTO loginUser(AuthLoginRequestDTO authLoginRequestDTO){

        //recuperamos nombre de usuario y contraseña
        String username = authLoginRequestDTO.username();
        String password = authLoginRequestDTO.password();

        Authentication authentication = this.authenticate(username,password);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.createToken(authentication);
        AuthResponseDTO authResponseDTO = new AuthResponseDTO(username,"Login succesfull",accessToken,true);
        return authResponseDTO;

    }

}
