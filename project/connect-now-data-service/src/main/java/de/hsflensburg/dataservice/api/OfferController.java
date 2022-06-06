package de.hsflensburg.dataservice.api;

import de.hsflensburg.dataservice.domain.dto.*;
import de.hsflensburg.dataservice.domain.mapper.OfferMapper;
import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.Offer;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.CategoryNotFoundException;
import de.hsflensburg.dataservice.service.CategoryService;
import de.hsflensburg.dataservice.service.OfferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Offer")
@RestController
@RequestMapping("/offer")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final CategoryService categoryService;

    @RolesAllowed({"ROLE_TEACHER"})
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateOfferRequest offer) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            String id = offerService.createOffer(subject, offer.getTitle(), offer.getCategoryId(),
                    offer.isVisible(), offer.getComponents());

            return ResponseEntity.created(URI.create("/offer/" + id)).build();
        } catch (BadFormatException | CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping({"{id}"})
    public ResponseEntity<OfferResponse> get(@PathVariable String id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            Optional<Offer> offer = offerService.getByIdAndEntitled(id, subject);

            if (offer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Optional<Category> category = categoryService.getCategory(offer.get().getCategoryId());
            Map<String, String> categoryName = null;

            if (category.isPresent()) {
                categoryName = category.get().getName();
            }

            OfferResponse resp = OfferMapper.offerToOfferResponse(offer.get(), categoryName);

            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<OfferResponse>> getAllWhereEntitled(@RequestParam(required = false) String owner) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        List<Offer> offers;

        if (owner == null || owner.length() == 0) {
            offers = offerService.getAllOffersWhereEntitled(subject);
        } else {
            offers = offerService.getAllOffersByOwnerWhereEntitled(subject, owner);
        }

        if (offers.size() == 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        }

        List<ObjectId> categoryIds = offers.stream().map(Offer::getCategoryId).distinct().toList();
        List<Category> categories = categoryService.getCategoriesById(categoryIds);
        Map<ObjectId, Map<String, String>> categoryNames =
                categories.stream().collect(Collectors.toMap(Category::getId, Category::getName, (x, y) -> x, HashMap::new));

        List<OfferResponse> response = offers.stream().map(offer ->
                OfferMapper.offerToOfferResponse(offer, categoryNames.get(offer.getCategoryId()))
        ).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RolesAllowed({"ROLE_TEACHER"})
    @DeleteMapping({"{id}"})
    public ResponseEntity<Void> delete(@PathVariable String id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            boolean exists = offerService.existsByIdAndOwner(id, subject);

            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            boolean success = offerService.deleteOffer(id);

            if (success) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RolesAllowed({"ROLE_TEACHER"})
    @PutMapping({"{id}"})
    public ResponseEntity<OfferResponse> update(@PathVariable String id, @RequestBody @Valid UpdateOfferRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            Optional<Offer> offer = offerService.updateOffer(id, subject, request.getTitle(), request.getCategoryId(),
                    request.isVisible(), request.getComponents());

            if (offer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Optional<Category> category = categoryService.getCategory(offer.get().getCategoryId());
            Map<String, String> categoryName = null;

            if (category.isPresent()) {
                categoryName = category.get().getName();
            }

            OfferResponse resp = OfferMapper.offerToOfferResponse(offer.get(), categoryName);

            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (BadFormatException | CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
