package de.hsflensburg.authservice.service;

import de.hsflensburg.authservice.domain.model.Token;
import de.hsflensburg.authservice.repository.TokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepo tokenRepo;

    public void insertToken(String subject, String tokenId, Date expires) {
        tokenRepo.insert(new Token(subject, tokenId, expires));
    }

    public void deleteToken(String tokenId) {
        tokenRepo.deleteTokenByTokenId(tokenId);
    }

    public boolean existsToken(String tokenId) {
        return tokenRepo.existsTokenByTokenId(tokenId);
    }
}

