package de.hsflensburg.authservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class RsaKeysService {

    private static final Logger logger = LoggerFactory.getLogger(RsaKeysService.class);

    public void generateNewKeys(String privateKeyPath, String publicKeyPath) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(1024);

            KeyPair keyPair = keyPairGenerator.genKeyPair();
            writeKeyInFile(keyPair.getPrivate(), privateKeyPath);
            writeKeyInFile(keyPair.getPublic(), publicKeyPath);

        } catch (NoSuchAlgorithmException e) {
            logger.error("generateNewKeys: Failed to generate rsa key files");
        }
    }

    public Key[] loadKeys(String privateKeyPath, String publicKeyPath) {
        try {
            byte[] encodedPrivateKeyBytes = readBytesFromFile(privateKeyPath);
            byte[] encodedPublicKeyBytes = readBytesFromFile(publicKeyPath);

            if (encodedPrivateKeyBytes == null || encodedPublicKeyBytes == null) {
                throw new Exception("Failed to load keys from files");
            }

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKeyBytes));
            Key publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedPublicKeyBytes));
            return new Key[] {privateKey, publicKey};
        } catch (Exception e) {
            logger.error("loadKeys: Failed to load rsa key files");
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
