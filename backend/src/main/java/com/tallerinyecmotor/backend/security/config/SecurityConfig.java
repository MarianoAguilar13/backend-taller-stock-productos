package com.tallerinyecmotor.backend.security.config;

import com.tallerinyecmotor.backend.security.config.filter.JwtTokenValidator;
import com.tallerinyecmotor.backend.utils.JWTUtils;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;


//estas etiquetas sirven para la configuracion de la seguridad del spring security
//@EnableMethodSecurity en cada metodo de la rest api voy a colocar los permisos, para que solo puedan llamarlos usuarios con esos permisos
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JWTUtils jwtUtils;

    /* csrf */
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    //creamos authentication provider
    //Agregamos el user Details Service como parámetro
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        //sacamos el anterior, el lógico y agregamos el nuevo
        //  provider.setUserDetailsService(userDetailsService());
        provider.setUserDetailsService(userDetailsService);


        return provider;
    }

    /* El passwordEncoder nos permite crear un algoritmo para encriptar y desencriptar las contraseñas*/
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /*El userDetailsService me sitve para crear un usuario con el rol y los permisos que va a tener */
    /*
    @Bean
    public UserDetailsService userDetailsService(){
        List userDetailsList = new ArrayList<>();

        userDetailsList.add(User.withUsername("admin")
                .password("1234")
                .roles("ADMIN")
                .authorities("CREATE","READ","UPDATE","DELETE")
                .build());

        userDetailsList.add(User.withUsername("empleado")
                .password("1234")
                .roles("EMPLEADO")
                .authorities("READ")
                .build());

        return new InMemoryUserDetailsManager(userDetailsList);
    }*/

}
