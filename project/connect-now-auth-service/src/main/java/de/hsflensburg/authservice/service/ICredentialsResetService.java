package de.hsflensburg.authservice.service;

import de.hsflensburg.authservice.domain.model.PasswordReset;
import de.hsflensburg.authservice.repository.PasswordResetRequestRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ICredentialsResetService {
    PasswordReset addUserPasswordResetRequest(String subject, LocalDateTime expires);
    Optional<PasswordReset> getUserPasswordResetRequest(String resetRequestId);

    void removeUserPasswordResetRequest(String resetRequestId);
}

@Service
@RequiredArgsConstructor
class CredentialsResetService implements ICredentialsResetService {
    private final PasswordResetRequestRepo passwordResetRequestRepo;
    @Override
    public PasswordReset addUserPasswordResetRequest(String subject, LocalDateTime expires) {
        PasswordReset passwordReset = new PasswordReset(subject, UUID.randomUUID().toString(), expires);
        passwordResetRequestRepo.insert(passwordReset);
        return passwordReset;
    }

    @Override
    public Optional<PasswordReset> getUserPasswordResetRequest(String resetRequestId) {
        return passwordResetRequestRepo.findByResetRequestIdAndExpiresGreaterThanEqual(resetRequestId, LocalDateTime.now());
    }

    @Override
    public void removeUserPasswordResetRequest(String resetRequestId) {
        passwordResetRequestRepo.removeByResetRequestId(resetRequestId);
    }
}
