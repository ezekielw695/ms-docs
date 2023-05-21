package com.ezekielwong.ms.docs.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;

/**
 * Utility class for JWT generation
 */
@Slf4j
@Component
public class JwtUtils {

    // Use BouncyCastle cryptographic provider instead of JVM default as BouncyCastle is able to load PCKS#1 keys
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Integration key of the application
     */
    @Value("${jwt.iss}")
    private String iss;

    /**
     * User id of the user to be impersonated
     */
    @Value("${jwt.sub}")
    private String sub;

    /**
     * URI of the authentication service instance to be used
     */
    @Value("${jwt.aud}")
    private String aud;

    /**
     * Scopes to request
     */
    @Value("${jwt.scope}")
    private String scope;

    /**
     * Number of seconds before access token expires
     */
    @Value("${jwt.duration}")
    private String duration;

    /**
     * Third party app public key
     */
    @Value("${jwt.public-key}")
    private String publicKeyPath;

    /**
     * Third party app RSA private key
     */
    @Value("${jwt.rsa-private-key}")
    private String rsaPrivateKeyPath;

    /**
     * Instant when the JWT assertion will expire, in Unix epoch format
     */
    private Instant exp;

    /**
     * Check if access token previously used to call the third party app has expired
     *
     * @param now Instant when the JWT was issued, in Unix epoch format
     * @param savedAccessToken Access token previously used to call the third party app
     * @return <code>true</code> (access token has not expired or <br>
     *          <code>false</code> (access token has expired)
     */
    public Boolean accessTokenIsNotExpired(Instant now, String savedAccessToken) {

        if (savedAccessToken == null) {

            log.debug("No access token available");
            return false;

        } else if (exp != null && now.isBefore(exp)) {

            log.debug("Unexpired access token available");
            return true;

        } else {

            log.debug("Access token expired");
            return false;
        }
    }

    /**
     * Generate a JSON web token for API calls to the third party app
     *
     * @param iat Current long epoch-seconds
     * @param now Instant when JWT was issued, in Unix epoch format
     * @return The JSON web token
     * @throws NoSuchAlgorithmException Requested cryptographic algorithm is not available
     * @throws IOException I/O operation interrupted or failed
     * @throws InvalidKeySpecException Key specification is invalid
     */
    public String generateWebToken(long iat, Instant now) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        log.debug("Generating new JSON web token");
        KeyFactory kf = KeyFactory.getInstance("RSA");

        String publicKey = Files.readString(Path.of(publicKeyPath));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKey)));

        String privateKey = Files.readString(Path.of(rsaPrivateKeyPath));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);

        // Save expiry instant of token for future use
        exp = Instant.ofEpochSecond(iat + Integer.parseInt(duration));

        return JWT.create()
                .withIssuer(iss)
                .withSubject(sub)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withAudience(aud)
                .withClaim("scope", scope)
                .sign(Algorithm.RSA256(pubKey, privKey));
    }
}
