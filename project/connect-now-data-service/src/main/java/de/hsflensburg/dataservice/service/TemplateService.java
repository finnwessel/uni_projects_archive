package de.hsflensburg.dataservice.service;

import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.domain.model.Template;
import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.CategoryNotFoundException;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.TemplateRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepo templateRepo;
    private final CategoryRepo categoryRepo;

    public String createTemplate(Map<String, String> title, String category, List<BaseComp> components)
            throws BadFormatException, CategoryNotFoundException {
        if (!ObjectId.isValid(category)) {
            throw new BadFormatException();
        }

        ObjectId categoryId = new ObjectId(category);

        boolean categoryExists = categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString());

        if (!categoryExists) {
            throw new CategoryNotFoundException();
        }

        Template newTemplate = new Template(title, categoryId, components);
        templateRepo.save(newTemplate);

        return newTemplate.getId().toHexString();
    }

    public Optional<Template> getById(String id) throws BadFormatException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId templateId = new ObjectId(id);

        return templateRepo.findById(templateId);
    }

    public List<Template> getAll() {
        return templateRepo.findAll();
    }

    public List<Template> getAllByCategory(String categoryId) throws BadFormatException {
        if (!ObjectId.isValid(categoryId)) {
            throw new BadFormatException();
        }

        ObjectId objectId = new ObjectId(categoryId);

        return templateRepo.findByCategoryId(objectId);
    }

    public boolean existsById(String id) throws BadFormatException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId templateId = new ObjectId(id);

        return templateRepo.existsById(templateId);
    }

    public boolean deleteTemplate(String id) throws BadFormatException {
        if (ObjectId.isValid(id)) {
            return templateRepo.deleteTemplateById(new ObjectId(id)) == 1;
        } else {
            throw new BadFormatException();
        }
    }

    public Optional<Template> updateTemplate(String id, Map<String, String> newTitle, String newCategoryId, List<BaseComp> newComponents)
            throws BadFormatException, CategoryNotFoundException {
        if (!ObjectId.isValid(id) || !ObjectId.isValid(newCategoryId)) {
            throw new BadFormatException();
        }

        ObjectId categoryId = new ObjectId(newCategoryId);
        boolean categoryExists = categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString());

        if (!categoryExists) {
            throw new CategoryNotFoundException();
        }

        Optional<Template> optionalTemplate = templateRepo.findById(new ObjectId(id));

        if (optionalTemplate.isEmpty()) {
            return Optional.empty();
        }

        Template template = optionalTemplate.get();
        template.setTitle(newTitle);
        template.setCategoryId(categoryId);
        template.setComponents(newComponents);

        templateRepo.save(template);

        return Optional.of(template);
    }

}
