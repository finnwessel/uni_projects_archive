package de.hsflensburg.authservice.repository;

import de.hsflensburg.authservice.domain.model.Token;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepo extends MongoRepository<Token, ObjectId> {
    void deleteTokenByTokenId(String tokenId);
    boolean existsTokenByTokenId(String tokenId);
}
