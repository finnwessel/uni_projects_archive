package de.hsflensburg.dataservice.unit.service;

import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.domain.model.Offer;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.CategoryNotFoundException;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.OfferRepo;
import de.hsflensburg.dataservice.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class TestOfferService {

    @Mock
    private OfferRepo offerRepo;
    @Mock
    private CategoryRepo categoryRepo;
    private OfferService offerService;

    @BeforeEach
    private void init() {
        offerService = new OfferService(offerRepo, categoryRepo);
    }

    @Test
    public void testCreateOffer() throws CategoryNotFoundException, BadFormatException {
        doAnswer(invocationOnMock -> {
            Offer offer = invocationOnMock.getArgument(0);
            offer.setId(new ObjectId());

            return null;
        }).when(offerRepo).save(any());

        ObjectId categoryId = new ObjectId();
        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.OFFER.toString())).thenReturn(true);

        offerService.createOffer("owner", "title", categoryId.toHexString(), false, new ArrayList<>());

        verify(offerRepo).save(any());
    }

    @Test
    public void testCreateOfferBadFormat() {
        assertThrows(BadFormatException.class, () ->
                offerService.createOffer("owner", "title", "123", false, new ArrayList<>()));

        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(offerRepo, never()).save(any());
    }

    @Test
    public void testCreateOfferCategoryDoesntExist() {
        when(categoryRepo.existsByIdAndType(any(), any())).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () ->
                offerService.createOffer("owner", "title", new ObjectId().toHexString(), false, new ArrayList<>()));

        verify(offerRepo, never()).save(any());
    }

    @Test
    public void testGetByIdAndEntitled() throws BadFormatException {
        ObjectId requestId = new ObjectId();
        String subject = "subject";
        offerService.getByIdAndEntitled(requestId.toHexString(), subject);

        verify(offerRepo).findByIdAndEntitled(requestId, subject);
    }

    @Test
    public void testGetByIdAndEntitledBadFormat() {
        assertThrows(BadFormatException.class, () ->
                offerService.getByIdAndEntitled("123", "subject"));

        verify(offerRepo, never()).findByIdAndEntitled(any(), any());
    }

    @Test
    public void testGetAllOffersByOwnerWhereEntitled() {
        String subject = "subject";
        String owner = "owner";
        offerService.getAllOffersByOwnerWhereEntitled(subject, owner);

        verify(offerRepo).findAllByOwnerWhereEntitled(subject, owner);
    }

    @Test
    public void testGetAllOffersWhereEntitled() {
        String subject = "subject";
        offerService.getAllOffersWhereEntitled(subject);

        verify(offerRepo).findAllWhereEntitled(subject);
    }

    @Test
    public void testExistsByIdAndOwner() throws BadFormatException {
        ObjectId objectId = new ObjectId();
        String subject = "subject";
        offerService.existsByIdAndOwner(objectId.toHexString(), "subject");

        verify(offerRepo).existsByIdAndOwner(objectId, subject);
    }

    @Test
    public void testExistsByIdAndOwnerBadFormat() {
        assertThrows(BadFormatException.class, () ->
                offerService.existsByIdAndOwner("123", "subject"));

        verify(offerRepo, never()).existsByIdAndOwner(any(), any());
    }

    @Test
    public void testDeleteOffer() throws BadFormatException {
        ObjectId objectId = new ObjectId();

        offerService.deleteOffer(objectId.toHexString());

        verify(offerRepo).deleteOfferById(objectId);
    }

    @Test
    public void testDeleteOfferBadFormat() {
        assertThrows(BadFormatException.class, () ->
                offerService.deleteOffer("123"));

        verify(offerRepo, never()).deleteOfferById(any());
    }

    @Test
    public void testUpdateOffer() throws CategoryNotFoundException, BadFormatException {
        String owner = "owner";
        Offer offer = new Offer(owner, "title", new ObjectId(), true, new ArrayList<>());

        ObjectId id = new ObjectId();
        ObjectId categoryId = new ObjectId();

        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.OFFER.toString())).thenReturn(true);
        when(offerRepo.findByIdAndOwner(id, owner)).thenReturn(Optional.of(offer));

        String newTitle = "test";
        boolean newVisible = false;
        offerService.updateOffer(id.toHexString(), owner, newTitle, categoryId.toHexString(), newVisible, new ArrayList<>());

        ArgumentCaptor<Offer> saveArgumentCaptor = ArgumentCaptor.forClass(Offer.class);

        verify(offerRepo).save(saveArgumentCaptor.capture());

        assertEquals(newTitle, saveArgumentCaptor.getValue().getTitle());
        assertEquals(newVisible, saveArgumentCaptor.getValue().isVisible());
        assertEquals(categoryId, saveArgumentCaptor.getValue().getCategoryId());
    }

    @Test
    public void testUpdateOfferBadId() {
        assertThrows(BadFormatException.class, () ->
                offerService.updateOffer("123", "subject", "title", new ObjectId().toHexString(), false, new ArrayList<>()));

        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(offerRepo, never()).findByIdAndOwner(any(), any());
    }

    @Test
    public void testUpdateOfferBadCategoryId() {
        assertThrows(BadFormatException.class, () ->
                offerService.updateOffer(new ObjectId().toHexString(), "subject", "title", "123", false, new ArrayList<>()));

        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(offerRepo, never()).findByIdAndOwner(any(), any());
    }

    @Test
    public void testUpdateOfferCategoryDoesntExist() {
        ObjectId id = new ObjectId();
        ObjectId categoryId = new ObjectId();

        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.OFFER.toString())).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () ->
                offerService.updateOffer(id.toHexString(), "subject", "title", categoryId.toHexString(), false, new ArrayList<>()));

        verify(offerRepo, never()).save(any());
    }

    @Test
    public void testUpdateOfferOfferDoesntExist() throws CategoryNotFoundException, BadFormatException {
        String subject = "subject";

        ObjectId id = new ObjectId();
        ObjectId categoryId = new ObjectId();

        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.OFFER.toString())).thenReturn(true);
        when(offerRepo.findByIdAndOwner(id, "subject")).thenReturn(Optional.empty());

        offerService.updateOffer(id.toHexString(), subject, "title", categoryId.toHexString(), false, new ArrayList<>());

        verify(offerRepo, never()).save(any());
    }
}
