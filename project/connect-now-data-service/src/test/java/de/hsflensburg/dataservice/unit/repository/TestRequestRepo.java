package de.hsflensburg.dataservice.unit.repository;

import de.hsflensburg.dataservice.domain.model.Request;
import de.hsflensburg.dataservice.domain.model.Share;
import de.hsflensburg.dataservice.domain.model.ShareStatus;
import de.hsflensburg.dataservice.repository.RequestRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
public class TestRequestRepo {

    @Autowired
    private RequestRepo requestRepo;

    @BeforeEach
    private void init() {
        requestRepo.deleteAll();
    }

    @Test
    public void testExistsByIdAndOwner() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        boolean exists = requestRepo.existsByIdAndOwner(request.getId(), "owner");

        assertTrue(exists);
    }

    @Test
    public void testExistsByIdAndOwnerWrongId() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        boolean exists = requestRepo.existsByIdAndOwner(new ObjectId(), "owner");

        assertFalse(exists);
    }

    @Test
    public void testExistsByIdAndOwnerWrongOwner() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        boolean exists = requestRepo.existsByIdAndOwner(request.getId(), "other");

        assertFalse(exists);
    }

    @Test
    public void testDeleteRequestById() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        long success = requestRepo.deleteRequestById(request.getId());

        assertEquals(1L, success);
        assertEquals(0, requestRepo.findAll().size());
    }

    @Test
    public void testDeleteRequestByIdWrongId() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        long success = requestRepo.deleteRequestById(new ObjectId());

        assertEquals(0L, success);
        assertEquals(1, requestRepo.findAll().size());
    }

    @Test
    public void testFindByIdAndOwner() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        Optional<Request> optionalRequest = requestRepo.findByIdAndOwner(request.getId(), "owner");

        assertTrue(optionalRequest.isPresent());
    }

    @Test
    public void testFindByIdAndOwnerWrongId() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        Optional<Request> optionalRequest = requestRepo.findByIdAndOwner(new ObjectId(), "owner");

        assertTrue(optionalRequest.isEmpty());
    }

    @Test
    public void testFindByIdAndOwnerWrongOwner() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        Optional<Request> optionalRequest = requestRepo.findByIdAndOwner(request.getId(), "other");

        assertTrue(optionalRequest.isEmpty());
    }

    @Test
    public void testFindAllWithOwner() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        List<Request> requests = requestRepo.findAllByOwnerOrParticipantsContainsOrSharesContains("owner");

        assertEquals(1, requests.size());
    }

    @Test
    public void testFindAllWithParticipant() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        List<Request> requests = requestRepo.findAllByOwnerOrParticipantsContainsOrSharesContains("participant");

        assertEquals(1, requests.size());
    }

    @Test
    public void testFindAllWithShare() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        List<Request> requests = requestRepo.findAllByOwnerOrParticipantsContainsOrSharesContains("share");

        assertEquals(1, requests.size());
    }

    @Test
    public void testFindAllNotEligible() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        List<Request> requests = requestRepo.findAllByOwnerOrParticipantsContainsOrSharesContains("other");

        assertEquals(0, requests.size());
    }

    @Test
    public void testFindByIdWithOwner() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        Request response = requestRepo.findByIdAndOwnerOrParticipantsContainsOrSharesContains(request.getId(), "owner");

        assertNotNull(response);
    }

    @Test
    public void testFindByIdWithParticipant() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        Request response = requestRepo.findByIdAndOwnerOrParticipantsContainsOrSharesContains(request.getId(), "participant");

        assertNotNull(response);
    }

    @Test
    public void testFindByIdWithShare() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        Request response = requestRepo.findByIdAndOwnerOrParticipantsContainsOrSharesContains(request.getId(), "share");

        assertNotNull(response);
    }

    @Test
    public void testFindBySharesContains() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        Request response = requestRepo.findBySharesContains(request.getId(), "share");

        assertNotNull(response);
    }

    @Test
    public void testFindBySharesNotEligible() {
        Request request = new Request("owner", "title", new ObjectId(),
                new ObjectId(), List.of("participant"), List.of(new Share("share", LocalDateTime.now(),
                ShareStatus.PENDING, true, true)), new ArrayList<>(), true);
        requestRepo.insert(request);

        Request response = requestRepo.findBySharesContains(request.getId(), "owner");

        assertNull(response);
    }

}
