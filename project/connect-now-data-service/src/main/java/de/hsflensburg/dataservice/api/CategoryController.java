package de.hsflensburg.dataservice.api;

import de.hsflensburg.dataservice.domain.dto.CategoryResponse;
import de.hsflensburg.dataservice.domain.dto.CreateCategoryRequest;
import de.hsflensburg.dataservice.domain.dto.UpdateCategoryRequest;
import de.hsflensburg.dataservice.domain.mapper.CategoryMapper;
import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name = "Category")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @RolesAllowed({"ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateCategoryRequest request) {
        String id = categoryService.createCategory(request.getType(), request.getName(), request.getColor());

        return ResponseEntity.created(URI.create("/category/" + id)).build();
    }

    @RolesAllowed({"ROLE_ADMIN"})
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            boolean success = categoryService.deleteCategory(id);

            if (success) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryResponse> get(@PathVariable String id) {
        try {
            Optional<Category> category = categoryService.getCategory(id);

            if (category.isPresent()) {
                CategoryResponse categoryResponse = CategoryMapper.INSTANCE.categoryToCategoryResponse(category.get());

                return ResponseEntity.status(HttpStatus.OK).body(categoryResponse);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch(BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll(@RequestParam CategoryType type) {
        List<Category> categories = categoryService.getAllCategories(type);
        List<CategoryResponse> categoryResponses = categories.stream().map(CategoryMapper.INSTANCE::categoryToCategoryResponse).toList();

        return ResponseEntity.status(HttpStatus.OK).body(categoryResponses);
    }

    @RolesAllowed({"ROLE_ADMIN"})
    @PutMapping("{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable String id, @RequestBody @Valid UpdateCategoryRequest request) {
        try {
            Optional<Category> category = categoryService.updateCategory(id, request.getName(), request.getColor());

            if (category.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(CategoryMapper.INSTANCE.categoryToCategoryResponse(category.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
