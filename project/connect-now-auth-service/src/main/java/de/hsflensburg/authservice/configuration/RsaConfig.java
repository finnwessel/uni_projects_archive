package de.hsflensburg.authservice.configuration;

import de.hsflensburg.authservice.service.RsaKeysService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.Key;

@Configuration
public class RsaConfig {
    private final RsaKeysService rsaKeysService;

    private Key privateKey;
    private Key publicKey;

    @Value("${jwt.privateKeyPath}")
    private String privateKeyPath;
    @Value("${jwt.publicKeyPath}")
    private String publicKeyPath;

    public RsaConfig(RsaKeysService rsaKeysService) {
        this.rsaKeysService = rsaKeysService;
    }

    @PostConstruct
    private void setup() throws Exception {
        Key[] keys = this.rsaKeysService.loadKeys(privateKeyPath, publicKeyPath);
        if (keys == null) {
            this.rsaKeysService.generateNewKeys(privateKeyPath, publicKeyPath);
            keys = this.rsaKeysService.loadKeys(privateKeyPath, publicKeyPath);
            if (keys == null) {
                // ToDo: Stop server
                throw new Exception("Failed to load and generate keys");
            }
        }
        privateKey = keys[0];
        publicKey = keys[1];
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public Key getPublicKey() {
        return publicKey;
    }
}
