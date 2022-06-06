package de.hsflensburg.dataservice.api;

import de.hsflensburg.dataservice.domain.dto.*;
import de.hsflensburg.dataservice.domain.mapper.TemplateMapper;
import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.Template;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.CategoryNotFoundException;
import de.hsflensburg.dataservice.service.CategoryService;
import de.hsflensburg.dataservice.service.TemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Template")
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;
    private final CategoryService categoryService;

    @RolesAllowed({"ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateTemplateRequest request) {
        try {
            String id = templateService.createTemplate(request.getTitle(), request.getCategoryId(), request.getComponents());

            return ResponseEntity.created(URI.create("/template/" + id)).build();
        } catch (BadFormatException | CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping({"{id}"})
    public ResponseEntity<TemplateResponse> get(@PathVariable String id) {
        try {
            Optional<Template> template = templateService.getById(id);

            if (template.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Optional<Category> category = categoryService.getCategory(template.get().getCategoryId());
            Map<String, String> categoryName = null;

            if (category.isPresent()) {
                categoryName = category.get().getName();
            }

            TemplateResponse resp = TemplateMapper.templateToTemplateResponse(template.get(), categoryName);

            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<TemplateResponse>> getAll(@RequestParam(required = false) String categoryId) {
        List<Template> templates;

        if (categoryId == null || categoryId.length() == 0) {
            templates = templateService.getAll();
        } else {
            try {
                templates = templateService.getAllByCategory(categoryId);
            } catch (BadFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        if (templates.size() == 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        }

        List<ObjectId> categoryIds = templates.stream().map(Template::getCategoryId).distinct().toList();
        List<Category> categories = categoryService.getCategoriesById(categoryIds);
        Map<ObjectId, Map<String, String>> categoryNames =
                categories.stream().collect(Collectors.toMap(Category::getId, Category::getName, (x, y) -> x, HashMap::new));

        List<TemplateResponse> response = templates.stream().map(template ->
                TemplateMapper.templateToTemplateResponse(template, categoryNames.get(template.getCategoryId()))
        ).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RolesAllowed({"ROLE_ADMIN"})
    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            boolean exists = templateService.existsById(id);

            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            boolean success = templateService.deleteTemplate(id);

            if (success) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RolesAllowed({"ROLE_ADMIN"})
    @PutMapping({"{id}"})
    public ResponseEntity<TemplateResponse> update(@PathVariable String id, @RequestBody @Valid UpdateTemplateRequest request) {
        try {
            Optional<Template> template = templateService.updateTemplate(id, request.getTitle(), request.getCategoryId(),
                    request.getComponents());

            if (template.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Optional<Category> category = categoryService.getCategory(template.get().getCategoryId());
            Map<String, String> categoryName = null;

            if (category.isPresent()) {
                categoryName = category.get().getName();
            }

            TemplateResponse resp = TemplateMapper.templateToTemplateResponse(template.get(), categoryName);

            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (BadFormatException | CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
