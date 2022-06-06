package de.hsflensburg.authservice.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles({"test-basic", "basic"})
@SpringBootTest
public class TestRsaKeyService {

    private static final String privateKeyPath = "privateTestKey.key";
    private static final String publicKeyPath = "publicTestKey.key";

    private final RsaKeysService rsaKeysService;

    @Autowired
    public TestRsaKeyService(RsaKeysService rsaKeysService) {
        this.rsaKeysService = rsaKeysService;
    }

    @BeforeAll @AfterAll
    static void init() throws Exception {
        removeFileIfExists(privateKeyPath);
        removeFileIfExists(publicKeyPath);
    }

    private static void removeFileIfExists(String filePath) throws Exception {
        File f = new File(filePath);
        if(f.exists() && !f.isDirectory()) {
            if (!f.delete()) {
                throw new Exception("Failed to remove file");
            }
        }
    }

    @Test
    public void testKeyService() {
        rsaKeysService.generateNewKeys(privateKeyPath, publicKeyPath);

        Key[] keys = rsaKeysService.loadKeys(privateKeyPath, publicKeyPath);

        assertEquals(2, keys.length);
        assertNotEquals(keys[0], null);
        assertNotEquals(keys[1], null);
    }
}
