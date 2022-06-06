package de.hsflensburg.dataservice.api.data;

import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@EnableAutoConfiguration
public class CategoryTestDataFactory {


    private final CategoryRepo categoryRepo;

    public CategoryTestDataFactory(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public final List<Category> categories = List.of(
            new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red")
    );

    public void up() {
        //categoryRepo.insert(categories);
    }

    public void down() {
        //categoryRepo.deleteAll(categories);
    }
}
