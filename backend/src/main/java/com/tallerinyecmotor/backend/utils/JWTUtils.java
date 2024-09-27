package com.tallerinyecmotor.backend.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

//cuando se carga el contexto de spring tambien tiene que cargar este componente
@Component
public class JWTUtils {

    @Value("${security.jwt.private.key}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    //creacion del token
    //esa iterfaze Authentication tiene metodo para guardar token y para tener get de grandAuthoriti y
    //lo que esta relacionado con la seguridad
    public String createToken(Authentication authentication){

        Algorithm algorithm = Algorithm.HMAC256(privateKey);

        //nos devuelve el principal, que seria el usuario autenticado, en este caso su nombre
        //de usuario
        String username = authentication.getPrincipal().toString();

        //nos trae all authorities de ese usuario
        //transformamos toodos los authorities en un stream para poder mapearlos y luego
        //los mapeamos uniendolos con un collect
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //604800000 es una semana
        String JWTToken = JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities",authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+ 604800000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);

        return JWTToken;

    }

    //decodificar y validar nuestros tokens
    public DecodedJWT validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            //si toodo ok, no genera niguna excepcion y nos devuelve el JWT decodificado

            DecodedJWT decodedJWT = verifier.verify(token);

            return decodedJWT;

        }catch (JWTVerificationException exception){

            throw new JWTVerificationException("Invalid token. Not authorized");
        }
    }

    //metodo para obtener el usuario/username
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }

    //obtener un claim en particular
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    //obtener los claims
    public Map<String,Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
