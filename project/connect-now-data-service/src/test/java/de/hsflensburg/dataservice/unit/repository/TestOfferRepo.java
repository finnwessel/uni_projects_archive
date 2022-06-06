package de.hsflensburg.dataservice.unit.repository;

import de.hsflensburg.dataservice.domain.model.Offer;
import de.hsflensburg.dataservice.repository.OfferRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
public class TestOfferRepo {

    @Autowired
    private OfferRepo offerRepo;

    @BeforeEach
    private void init() {
        offerRepo.deleteAll();
    }

    @Test
    public void testExistsByIdAndOwner() {
        Offer offer = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        offerRepo.insert(offer);

        boolean exists = offerRepo.existsByIdAndOwner(offer.getId(), "owner");

        assertTrue(exists);
    }

    @Test
    public void testExistsByIdAndOwnerWrongId() {
        Offer offer = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        offerRepo.insert(offer);

        boolean exists = offerRepo.existsByIdAndOwner(new ObjectId(), "owner");

        assertFalse(exists);
    }

    @Test
    public void testExistsByIdAndOwnerWrongOwner() {
        Offer offer = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        offerRepo.insert(offer);

        boolean exists = offerRepo.existsByIdAndOwner(offer.getId(), "wrongOwner");

        assertFalse(exists);
    }


    @Test
    public void testDeleteOfferById() {
        Offer offer = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        offerRepo.insert(offer);

        long success = offerRepo.deleteOfferById(offer.getId());

        assertEquals(1L, success);
        assertEquals(0, offerRepo.findAll().size());
    }

    @Test
    public void testDeleteOfferByIdWrongId() {
        Offer offer = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        offerRepo.insert(offer);

        long success = offerRepo.deleteOfferById(new ObjectId());

        assertEquals(0L, success);
        assertEquals(1, offerRepo.findAll().size());
    }

    @Test
    public void testFindByIdAndOwner() {
        Offer offer = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        offerRepo.insert(offer);

        Optional<Offer> optionalOffer = offerRepo.findByIdAndOwner(offer.getId(), "owner");

        assertTrue(optionalOffer.isPresent());

        assertEquals(offer.getId(), optionalOffer.get().getId());
        assertEquals(offer.getOwner(), optionalOffer.get().getOwner());
        assertEquals(offer.getTitle(), optionalOffer.get().getTitle());
        assertEquals(offer.getCategoryId(), optionalOffer.get().getCategoryId());
        assertEquals(offer.getComponents(), optionalOffer.get().getComponents());

        long now = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        long createdAtDiff = now - optionalOffer.get().getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        long modifiedAtDiff = now - optionalOffer.get().getModifiedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        assertEquals(0, createdAtDiff, 200L);
        assertEquals(0, modifiedAtDiff, 200L);
    }

    @Test
    public void testFindAllByOwnerWhereEntitled() {
        Offer offer1 = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        Offer offer2 = new Offer("owner", "title", new ObjectId(), false, new ArrayList<>());
        offerRepo.insert(List.of(offer1, offer2));

        List<Offer> offers = offerRepo.findAllByOwnerWhereEntitled("owner", "owner");

        assertEquals(2, offers.size());
    }

    @Test
    public void testFindAllByOwnerWhereEntitledNotEntitled() {
        Offer offer = new Offer("owner", "title", new ObjectId(), false, new ArrayList<>());
        offerRepo.insert(offer);

        List<Offer> offers = offerRepo.findAllByOwnerWhereEntitled("owner", "other");

        assertEquals(0, offers.size());
    }

    @Test
    public void testFindAllWhereEntitled() {
        Offer offer1 = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        Offer offer2 = new Offer("owner", "title", new ObjectId(), false, new ArrayList<>());
        Offer offer3 = new Offer("other", "title", new ObjectId(), false, new ArrayList<>());
        offerRepo.insert(List.of(offer1, offer2, offer3));

        List<Offer> offers = offerRepo.findAllWhereEntitled("owner");

        assertEquals(2, offers.size());
    }

    @Test
    public void testFindByIdAndEntitled() {
        Offer offer = new Offer("owner", "title", new ObjectId(), true, new ArrayList<>());
        offerRepo.insert(offer);

        Optional<Offer> optionalOffer = offerRepo.findByIdAndEntitled(offer.getId(), "owner");

        assertTrue(optionalOffer.isPresent());
    }

    @Test
    public void testFindByIdAndEntitledNotEntitled() {
        Offer offer = new Offer("other", "title", new ObjectId(), false, new ArrayList<>());
        offerRepo.insert(offer);

        Optional<Offer> optionalOffer = offerRepo.findByIdAndEntitled(offer.getId(), "owner");

        assertTrue(optionalOffer.isEmpty());
    }

}
