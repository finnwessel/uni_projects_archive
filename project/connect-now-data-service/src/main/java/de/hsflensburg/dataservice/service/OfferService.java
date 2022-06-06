package de.hsflensburg.dataservice.service;

import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.domain.model.Offer;
import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.CategoryNotFoundException;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.OfferRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepo offerRepo;
    private final CategoryRepo categoryRepo;

    public String createOffer(String owner, String title, String category, boolean visible, List<BaseComp> components)
            throws BadFormatException, CategoryNotFoundException {
        if (!ObjectId.isValid(category)) {
            throw new BadFormatException();
        }

        ObjectId categoryId = new ObjectId(category);

        boolean categoryExists = categoryRepo.existsByIdAndType(categoryId, CategoryType.OFFER.toString());

        if (!categoryExists) {
            throw new CategoryNotFoundException();
        }

        Offer newOffer = new Offer(owner, title, categoryId, visible, components);
        offerRepo.save(newOffer);

        return newOffer.getId().toHexString();
    }

    public Optional<Offer> getByIdAndEntitled(String id, String subject) throws BadFormatException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId requestId = new ObjectId(id);

        return offerRepo.findByIdAndEntitled(requestId, subject);
    }

    public List<Offer> getAllOffersByOwnerWhereEntitled(String subject, String owner) {
        return offerRepo.findAllByOwnerWhereEntitled(subject, owner);
    }

    public List<Offer> getAllOffersWhereEntitled(String subject) {
        return offerRepo.findAllWhereEntitled(subject);
    }

    public boolean existsByIdAndOwner(String id, String subject) throws BadFormatException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId requestId = new ObjectId(id);

        return offerRepo.existsByIdAndOwner(requestId, subject);
    }

    public boolean deleteOffer(String id) throws BadFormatException {
        if (ObjectId.isValid(id)) {
            return offerRepo.deleteOfferById(new ObjectId(id)) == 1;
        } else {
            throw new BadFormatException();
        }
    }

    public Optional<Offer> updateOffer(String id, String subject, String newTitle, String newCategoryId, boolean newVisible,
                                           List<BaseComp> newComponents) throws BadFormatException, CategoryNotFoundException {
        if (!ObjectId.isValid(id) || !ObjectId.isValid(newCategoryId)) {
            throw new BadFormatException();
        }

        ObjectId categoryId = new ObjectId(newCategoryId);
        boolean categoryExists = categoryRepo.existsByIdAndType(categoryId, CategoryType.OFFER.toString());

        if (!categoryExists) {
            throw new CategoryNotFoundException();
        }

        Optional<Offer> optionalOffer = offerRepo.findByIdAndOwner(new ObjectId(id), subject);

        if (optionalOffer.isEmpty()) {
            return Optional.empty();
        }

        Offer offer = optionalOffer.get();
        offer.setTitle(newTitle);
        offer.setCategoryId(categoryId);
        offer.setVisible(newVisible);
        offer.setComponents(newComponents);

        offerRepo.save(offer);

        return Optional.of(offer);
    }
}
