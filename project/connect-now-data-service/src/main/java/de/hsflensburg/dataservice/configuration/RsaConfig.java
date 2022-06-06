package de.hsflensburg.dataservice.configuration;

import de.hsflensburg.dataservice.service.RsaKeysService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.Key;

@Configuration
public class RsaConfig {

    private final RsaKeysService rsaKeysService;

    private Key publicKey;

    @Value("${jwt.publicKeyPath}")
    private String publicKeyPath;

    public RsaConfig(RsaKeysService rsaKeysService) {
        this.rsaKeysService = rsaKeysService;
    }

    @PostConstruct
    private void setup() throws Exception {
        Key key = this.rsaKeysService.loadPublicKey(publicKeyPath);

        if (key == null) {
            this.rsaKeysService.generateNewPublicKey(publicKeyPath);
            key = this.rsaKeysService.loadPublicKey(publicKeyPath);
            if (key == null) {
                // ToDo: Stop server
                throw new Exception("Failed to load and generate keys");
            }
        }
        publicKey = key;
    }

    public Key getPublicKey() {
        return publicKey;
    }
}
