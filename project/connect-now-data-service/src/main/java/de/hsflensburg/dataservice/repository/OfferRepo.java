package de.hsflensburg.dataservice.repository;

import de.hsflensburg.dataservice.domain.model.Offer;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepo extends OfferRepoCustom, MongoRepository<Offer, ObjectId> {
    boolean existsByIdAndOwner(ObjectId id, String subject);
    Long deleteOfferById(ObjectId id);
    Optional<Offer> findByIdAndOwner(ObjectId id, String subject);
}

interface OfferRepoCustom {
    List<Offer> findAllByOwnerWhereEntitled(String subject, String owner);
    List<Offer> findAllWhereEntitled(String subject);
    Optional<Offer> findByIdAndEntitled(ObjectId id, String subject);
}

@RequiredArgsConstructor
class OfferRepoCustomImpl implements OfferRepoCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Offer> findAllByOwnerWhereEntitled(String subject, String owner) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(Criteria.where("owner").is(owner), new Criteria().orOperator(Criteria.where("visible").is(true), Criteria.where("owner").is(subject))));

        return mongoTemplate.find(query, Offer.class);
    }

    @Override
    public List<Offer> findAllWhereEntitled(String subject) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("visible").is(true), Criteria.where("owner").is(subject)));

        return mongoTemplate.find(query, Offer.class);
    }

    @Override
    public Optional<Offer> findByIdAndEntitled(ObjectId id, String subject) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(Criteria.where("_id").is(id),
                new Criteria().orOperator(Criteria.where("visible").is(true), Criteria.where("owner").is(subject))));

        return Optional.ofNullable(mongoTemplate.findOne(query, Offer.class));
    }
}
