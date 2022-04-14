package org.snowjak.devitae.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtHelper {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    @Value("${devitae.auth.jwt.timeout}")
    private int timeout = 1;
    @Value("${devitae.auth.jwt.timeoutUnit}")
    private ChronoUnit timeoutUnit = ChronoUnit.HOURS;

    public JwtHelper(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String createJwtForClaims(String subject, Map<String,String> claims) {

        final Instant now = Instant.now();
        final Instant expiry = now.plus(timeout, timeoutUnit);

        final JWTCreator.Builder jwtBuilder = JWT.create().withSubject(subject);

        // Add claims
        claims.forEach(jwtBuilder::withClaim);

        // Add expiredAt and etc
        return jwtBuilder
                .withSubject(subject)
                .withNotBefore(new Date())
                .withExpiresAt(Date.from(expiry))
                .sign(Algorithm.RSA256(publicKey, privateKey));
    }
}
