package de.hsflensburg.dataservice.unit.mapper;

import de.hsflensburg.dataservice.domain.dto.CategoryInfo;
import de.hsflensburg.dataservice.domain.dto.OfferResponse;
import de.hsflensburg.dataservice.domain.dto.RequestResponse;
import de.hsflensburg.dataservice.domain.dto.TemplateResponse;
import de.hsflensburg.dataservice.domain.mapper.OfferMapper;
import de.hsflensburg.dataservice.domain.mapper.RequestMapper;
import de.hsflensburg.dataservice.domain.mapper.TemplateMapper;
import de.hsflensburg.dataservice.domain.model.*;
import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.RequestInfo;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class TestMapper {

    @Test
    public void testOfferMapper() {
        ObjectId objectId = new ObjectId();
        String owner = "owner";
        String title = "title";
        ObjectId categoryId = new ObjectId();
        boolean visible = false;
        List<BaseComp> comps = new ArrayList<>();
        LocalDateTime modifiedAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();
        Map<String, String> categoryNames = Map.of("de", "Projekt", "en", "project");

        Offer offer = new Offer(owner, title, categoryId, visible, comps);
        offer.setId(objectId);
        offer.setModifiedAt(modifiedAt);
        offer.setCreatedAt(createdAt);

        OfferResponse response = OfferMapper.offerToOfferResponse(offer, categoryNames);

        assertEquals(objectId.toHexString(), response.getId());
        assertEquals(owner, response.getOwner());
        assertEquals(title, response.getTitle());
        assertEquals(visible, response.isVisible());
        assertEquals(comps, response.getComponents());
        assertEquals(modifiedAt, response.getModifiedAt());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(categoryNames, response.getCategory());
    }

    @Test
    public void testRequestMapper() {
        ObjectId objectId = new ObjectId();
        String owner = "owner";
        String title = "title";
        ObjectId templateId = new ObjectId();
        ObjectId categoryId = new ObjectId();
        List<String> participants = List.of("participant1", "participant2");
        List<Share> shares = List.of(new Share("share1", LocalDateTime.now(), ShareStatus.PENDING, true, true));
        List<BaseComp> comps = new ArrayList<>();
        LocalDateTime modifiedAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();
        Map<String, String> categoryNames = Map.of("de", "Projekt", "en", "project");
        String color = "red";
        CategoryInfo categoryInfo = new CategoryInfo(categoryNames, color);
        boolean active = true;

        Request request = new Request(owner, title, templateId, categoryId, participants, shares, comps, active);
        request.setId(objectId);
        request.setModifiedAt(modifiedAt);
        request.setCreatedAt(createdAt);

        RequestResponse response = RequestMapper.requestToRequestResponse(request, categoryInfo);

        assertEquals(objectId.toHexString(), response.getId());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(modifiedAt, response.getModifiedAt());
        assertEquals(owner, response.getOwner());
        assertEquals(title, response.getTitle());
        assertEquals(categoryInfo, response.getCategory());
        assertEquals(participants, response.getParticipants());
        assertEquals(shares.size(), response.getShares().size());
        assertEquals(comps, response.getComponents());
        assertEquals(active, response.isActive());
    }

    @Test
    public void testRequestMapperFiltered() {
        ObjectId objectId = new ObjectId();
        String owner = "owner";
        String title = "title";
        ObjectId templateId = new ObjectId();
        ObjectId categoryId = new ObjectId();
        List<String> participants = List.of("participant1", "participant2");
        List<Share> shares = List.of(new Share("share1", LocalDateTime.now(), ShareStatus.PENDING, true, true),
                new Share("share2", LocalDateTime.now(), ShareStatus.PENDING, true, true));
        List<BaseComp> comps = new ArrayList<>();
        LocalDateTime modifiedAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();
        Map<String, String> categoryNames = Map.of("de", "Projekt", "en", "project");
        String color = "red";
        CategoryInfo categoryInfo = new CategoryInfo(categoryNames, color);
        boolean active = true;

        Request request = new Request(owner, title, templateId, categoryId, participants, shares, comps, active);
        request.setId(objectId);
        request.setModifiedAt(modifiedAt);
        request.setCreatedAt(createdAt);

        RequestResponse response = RequestMapper.requestToFilteredRequestResponse(request, "share1", categoryInfo);

        assertEquals(objectId.toHexString(), response.getId());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(modifiedAt, response.getModifiedAt());
        assertEquals(owner, response.getOwner());
        assertEquals(title, response.getTitle());
        assertEquals(categoryInfo, response.getCategory());
        assertEquals(participants, response.getParticipants());
        assertEquals(1, response.getShares().size());
        assertEquals("share1", response.getShares().get(0).getSubject());
        assertEquals(comps, response.getComponents());
        assertEquals(active, response.isActive());
    }

    @Test
    public void testTemplateMapper() {
        ObjectId objectId = new ObjectId();
        Map<String, String> title = Map.of("de", "Titel", "en", "Title");
        ObjectId categoryId = new ObjectId();
        List<BaseComp> comps = new ArrayList<>();
        LocalDateTime modifiedAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();
        Map<String, String> categoryNames = Map.of("de", "Projekt", "en", "project");

        Template template = new Template(title, categoryId, comps);
        template.setId(objectId);
        template.setModifiedAt(modifiedAt);
        template.setCreatedAt(createdAt);

        TemplateResponse response = TemplateMapper.templateToTemplateResponse(template, categoryNames);

        assertEquals(objectId.toHexString(), response.getId());
        assertEquals(categoryId, template.getCategoryId());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(modifiedAt, response.getModifiedAt());
        assertEquals(title, response.getTitle());
        assertEquals(categoryNames, response.getCategoryName());
        assertEquals(comps, response.getComponents());
    }
}
