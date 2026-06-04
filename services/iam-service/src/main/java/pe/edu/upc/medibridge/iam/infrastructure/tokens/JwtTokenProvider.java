package pe.edu.upc.medibridge.iam.infrastructure.tokens;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.upc.medibridge.iam.domain.services.TokenService;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenProvider implements TokenService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final String issuer;
    private final String keyId;
    private final long expirationDays;
    private final RSAKey publicJwk;

    public JwtTokenProvider(
            @Value("${authorization.jwt.private-key:}") String privateKey,
            @Value("${authorization.jwt.public-key:}") String publicKey,
            @Value("${authorization.jwt.issuer}") String issuer,
            @Value("${authorization.jwt.key-id}") String keyId,
            @Value("${authorization.jwt.expiration-days}") long expirationDays) {
        var keyPair = loadOrGenerateKeyPair(privateKey, publicKey);
        this.privateKey = keyPair.privateKey();
        this.publicKey = keyPair.publicKey();
        this.issuer = issuer;
        this.keyId = keyId;
        this.expirationDays = expirationDays;
        this.publicJwk = new RSAKey.Builder(this.publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(keyId)
                .build();
    }

    @Override
    public String generateToken(String username) {
        var now = Instant.now();
        var claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(username)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(expirationDays, ChronoUnit.DAYS)))
                .jwtID(UUID.randomUUID().toString())
                .build();

        var header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build();
        var signedJWT = new SignedJWT(header, claims);
        try {
            signedJWT.sign(new RSASSASigner(privateKey));
            return signedJWT.serialize();
        } catch (JOSEException exception) {
            throw new IllegalStateException("Unable to sign JWT", exception);
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        try {
            var signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(new RSASSAVerifier(publicKey))) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }
            var claims = signedJWT.getJWTClaimsSet();
            if (claims.getExpirationTime() == null || claims.getExpirationTime().before(new Date())) {
                throw new IllegalArgumentException("JWT expired");
            }
            return claims.getSubject();
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid JWT", exception);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            getUsernameFromToken(token);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public RSAKey getPublicJwk() {
        return publicJwk;
    }

    private KeyPairData loadOrGenerateKeyPair(String privateKeyValue, String publicKeyValue) {
        if (!privateKeyValue.isBlank() && !publicKeyValue.isBlank()) {
            return loadKeyPair(privateKeyValue, publicKeyValue);
        }
        LOGGER.warn("IAM_JWT_PRIVATE_KEY/IAM_JWT_PUBLIC_KEY are not configured. Generating an ephemeral RSA key pair.");
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            var keyPair = generator.generateKeyPair();
            return new KeyPairData((RSAPrivateKey) keyPair.getPrivate(), (RSAPublicKey) keyPair.getPublic());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("RSA algorithm is not available", exception);
        }
    }

    private KeyPairData loadKeyPair(String privateKeyValue, String publicKeyValue) {
        try {
            var keyFactory = KeyFactory.getInstance("RSA");
            var privateSpec = new PKCS8EncodedKeySpec(decodeKey(privateKeyValue));
            var publicSpec = new X509EncodedKeySpec(decodeKey(publicKeyValue));
            return new KeyPairData(
                    (RSAPrivateKey) keyFactory.generatePrivate(privateSpec),
                    (RSAPublicKey) keyFactory.generatePublic(publicSpec));
        } catch (Exception exception) {
            throw new IllegalStateException("Invalid RSA key configuration", exception);
        }
    }

    private byte[] decodeKey(String value) {
        var normalized = value
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(normalized);
    }

    private record KeyPairData(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
    }
}
