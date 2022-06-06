package de.hsflensburg.dataservice.repository;

import de.hsflensburg.dataservice.domain.model.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepo extends MongoRepository<Category, ObjectId> {
    List<Category> findAllByType(String type);
    Long deleteCategoryById(ObjectId id);
    boolean existsByIdAndType(ObjectId id, String type);
}
