package com.tallerinyecmotor.backend.security.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.tallerinyecmotor.backend.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;

public class JwtTokenValidator extends OncePerRequestFilter {

    private JWTUtils jwtUtils;

    //cuando inicial el JwtTokenValidator se le pasa el JWTUtils que esta creado en el contexto
    //de spring
    public JwtTokenValidator(JWTUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(jwtToken!=null){
            //bearer
            //nos traemos lo que esta detras del bearer
            //extraer el token
            jwtToken = jwtToken.substring(7);
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            //extraer el usuario y los athorities del token
            String username = jwtUtils.extractUsername(decodedJWT);
            String authorities = jwtUtils.getSpecificClaim(decodedJWT,"authorities").asString();
            //transformar los permisos de authorithies con el gantedAuthority
            Collection<? extends GrantedAuthority> authoritiesList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

            //creamos un contexto y un authentication para pasarle el authentication al contexto
            // y asi setear este contexto al de la aplicacion
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username,null,authoritiesList);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request,response);

    }

}
