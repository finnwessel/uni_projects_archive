package de.hsflensburg.authservice.service;

import de.hsflensburg.authservice.domain.dto.AvatarUploadData;
import de.hsflensburg.authservice.domain.exception.PresignedPostFormDataException;
import io.minio.*;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;

@Service
public class MinioService {
    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);
    private final MinioClient minioClient;
    @Value("${minio.bucket.name}")
    private String bucketName;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public AvatarUploadData getPresignedPostFormData(String identifier) throws PresignedPostFormDataException {
        try {
            String key = getUserAvatarFileName(identifier);
            PostPolicy policy = new PostPolicy(bucketName, ZonedDateTime.now().plusMinutes(5));

            policy.addEqualsCondition("key", key);
            policy.addStartsWithCondition("Content-Type", "image/");
            policy.addContentLengthRangeCondition(0, 2 * 1024 * 1024);

            return AvatarUploadData.builder()
                    .formData(minioClient.getPresignedPostFormData(policy))
                    .key(key)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to generate form data");
            throw new PresignedPostFormDataException("Failed to generate form data");
        }
    }

    public boolean removeAvatar(String identifier) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(getUserAvatarFileName(identifier)).build());
            return true;
        } catch (Exception ignored){};
        return false;
    }

    private static String getUserAvatarFileName(String identifier) {
        return identifier + ".png";
    }
}
