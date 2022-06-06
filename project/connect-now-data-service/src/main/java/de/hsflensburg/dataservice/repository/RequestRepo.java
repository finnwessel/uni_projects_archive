package de.hsflensburg.dataservice.repository;

import de.hsflensburg.dataservice.domain.model.Request;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepo extends RequestRepoCustom, MongoRepository<Request, ObjectId> {
    boolean existsByIdAndOwner(ObjectId id, String subject);
    Long deleteRequestById(ObjectId id);
    Optional<Request> findByIdAndOwner(ObjectId id, String subject);
}

interface RequestRepoCustom {
    List<Request> findAllByOwnerOrParticipantsContainsOrSharesContains(String subject);
    Request findByIdAndOwnerOrParticipantsContainsOrSharesContains(ObjectId id, String subject);

    Request findBySharesContains(ObjectId id, String subject);

    Page<Request> findByQuery(String subject, Query query, Pageable pageable);
}

@RequiredArgsConstructor
class RequestRepoCustomImpl implements RequestRepoCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Request> findAllByOwnerOrParticipantsContainsOrSharesContains(String subject) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("owner").is(subject),
                Criteria.where("participants").in(subject), Criteria.where("shares.subject").is(subject)));

        return mongoTemplate.find(query, Request.class);
    }

    @Override
    public Request findByIdAndOwnerOrParticipantsContainsOrSharesContains(ObjectId id, String subject) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(Criteria.where("_id").is(id),
                new Criteria().orOperator(Criteria.where("owner").is(subject),
                        Criteria.where("participants").in(subject), Criteria.where("shares.subject").is(subject))));

        return mongoTemplate.findOne(query, Request.class);
    }

    @Override
    public Request findBySharesContains(ObjectId id, String subject) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(Criteria.where("_id").is(id),
                Criteria.where("shares.subject").is(subject)));

        return mongoTemplate.findOne(query, Request.class);
    }

    @Override
    public Page<Request> findByQuery(String subject, Query query, Pageable pageable) {
        //TODO: Projection

        long total = mongoTemplate.count(query, Request.class);
        List<Request> result = mongoTemplate.find(query.with(pageable), Request.class);

        return new PageImpl<>(result, pageable, total);
    }
}