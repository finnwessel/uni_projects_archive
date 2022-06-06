package de.hsflensburg.authservice.repository;

import de.hsflensburg.authservice.domain.model.PasswordReset;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetRequestRepo extends PasswordResetRequestRepoCustom, MongoRepository<PasswordReset, ObjectId> {
    Optional<PasswordReset> findByResetRequestIdAndExpiresGreaterThanEqual(String resetRequestId, LocalDateTime expires);
    void removeByResetRequestId(String resetRequestId);
}

interface PasswordResetRequestRepoCustom {

}