package de.hsflensburg.dataservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

@Service
public class RsaKeysService {

    private static final Logger logger = LoggerFactory.getLogger(RsaKeysService.class);

    public void generateNewPublicKey(String publicKeyPath) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(1024);

            KeyPair keyPair = keyPairGenerator.genKeyPair();
            writeKeyInFile(keyPair.getPublic(), publicKeyPath);

        } catch (NoSuchAlgorithmException e) {
            logger.error("generateNewKeys: Failed to generate rsa key files");
        }
    }

    public Key loadPublicKey(String publicKeyPath) {
        try {
            byte[] encodedPublicKeyBytes = readBytesFromFile(publicKeyPath);

            if (encodedPublicKeyBytes == null) {
                throw new Exception("Failed to load key from file");
            }

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(encodedPublicKeyBytes));
        } catch (Exception e) {
            logger.error("loadKeys: Failed to load rsa key file");
            return null;
        }
    }

    private byte[] readBytesFromFile(String fileName) {
        File keyFile = new File(fileName);
        try {
            return Files.readAllBytes(keyFile.toPath());
        } catch (NoSuchFileException e) {
            logger.error("readBytesFromFile: File does not exists: " + fileName);
        }
        catch (Exception e) {
            logger.error("readBytesFromFile: Failed to read bytes from file: " + fileName);
        }
        return null;
    }

    private void writeKeyInFile(Key key, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(key.getEncoded());
        } catch (IOException e) {
            logger.error("readBytesFromFile: Failed to write key in file");
        }
    }
}
