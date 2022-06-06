package de.hsflensburg.dataservice.repository;

import de.hsflensburg.dataservice.domain.model.Template;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TemplateRepo extends TemplateRepoCustom, MongoRepository<Template, ObjectId> {
    List<Template> findByCategoryId(ObjectId categoryId);
    boolean existsById(ObjectId id);
    Long deleteTemplateById(ObjectId id);
}

interface TemplateRepoCustom {
}

@RequiredArgsConstructor
class TemplateRepoCustomImpl implements TemplateRepoCustom {
    private final MongoTemplate mongoTemplate;
}
