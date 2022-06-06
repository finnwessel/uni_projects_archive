package de.hsflensburg.dataservice.unit.service;

import de.hsflensburg.dataservice.domain.model.*;
import de.hsflensburg.dataservice.exceptions.*;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.domain.model.Request;
import de.hsflensburg.dataservice.domain.model.Template;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.CategoryNotFoundException;
import de.hsflensburg.dataservice.exceptions.RequestNotFoundException;
import de.hsflensburg.dataservice.exceptions.TemplateNotFoundException;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.RequestRepo;
import de.hsflensburg.dataservice.repository.TemplateRepo;
import de.hsflensburg.dataservice.service.RequestService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class TestRequestService {

    @Mock
    private RequestRepo requestRepo;
    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private TemplateRepo templateRepo;
    private RequestService requestService;

    @BeforeEach
    private void init() {
        requestService = new RequestService(requestRepo, categoryRepo, templateRepo);
    }

    @Test
    public void testCreateRequest() throws CategoryNotFoundException, BadFormatException, TemplateNotFoundException {
        doAnswer(invocationOnMock -> {
            Request request = invocationOnMock.getArgument(0);
            request.setId(new ObjectId());

            return null;
        }).when(requestRepo).save(any());

        ObjectId templateId = new ObjectId();
        ObjectId categoryId = new ObjectId();
        Template existingTemplate = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existingTemplate));
        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(true);

        requestService.createRequest("owner", "title", templateId.toHexString(), new ArrayList<>(), new ArrayList<>());

        verify(requestRepo).save(any());
    }

    @Test
    public void testCreateRequestBadFormat() {
        assertThrows(BadFormatException.class, () ->
                requestService.createRequest("owner", "title", "123", new ArrayList<>(), new ArrayList<>()));

        verify(templateRepo, never()).findById(any());
        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testCreateRequestTemplateDoesntExist() {
        ObjectId templateId = new ObjectId();
        when(templateRepo.findById(templateId)).thenReturn(Optional.empty());

        assertThrows(TemplateNotFoundException.class, () ->
                requestService.createRequest("owner", "title", templateId.toHexString(), new ArrayList<>(), new ArrayList<>()));

        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testCreateRequestCategoryDoesntExist() {
        ObjectId templateId = new ObjectId();
        ObjectId categoryId = new ObjectId();
        Template existingTemplate = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existingTemplate));
        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () ->
                requestService.createRequest("owner", "title", templateId.toHexString(), new ArrayList<>(), new ArrayList<>()));

        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testGetByIdAndEntitled() throws BadFormatException {
        ObjectId objectId = new ObjectId();
        String subject = "subject";
        requestService.getByIdAndEntitled(objectId.toHexString(), subject);

        verify(requestRepo).findByIdAndOwnerOrParticipantsContainsOrSharesContains(objectId, subject);
    }

    @Test
    public void testGetByIdAndEntitledBadFormat() {
        assertThrows(BadFormatException.class, () ->
                requestService.getByIdAndEntitled("123", "subject"));

        verify(requestRepo, never()).findByIdAndOwnerOrParticipantsContainsOrSharesContains(any(), any());
    }

    @Test
    public void testGetRequestsByEntitled() {
        String subject = "subject";
        requestService.getRequestsByEntitled(subject);

        verify(requestRepo).findAllByOwnerOrParticipantsContainsOrSharesContains(subject);
    }

    @Test
    public void testExistsByIdAndOwner() throws BadFormatException {
        ObjectId objectId = new ObjectId();
        String subject = "subject";
        requestService.existsByIdAndOwner(objectId.toHexString(), subject);

        verify(requestRepo).existsByIdAndOwner(objectId, subject);
    }

    @Test
    public void testExistsByIdAndOwnerBadFormat() {
        assertThrows(BadFormatException.class, () ->
                requestService.existsByIdAndOwner("123", "subject"));

        verify(requestRepo, never()).existsByIdAndOwner(any(), any());
    }

    @Test
    public void testDeactivateRequest() throws BadFormatException, RequestNotFoundException {
        ObjectId requestId = new ObjectId();
        ObjectId templateId = new ObjectId();
        ObjectId categoryId = new ObjectId();
        String owner = "owner";

        Request request = new Request(owner, "title", templateId, categoryId, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true);
        request.setId(requestId);

        when(requestRepo.findByIdAndOwner(requestId, owner)).thenReturn(Optional.of(request));

        requestService.deactivateRequest(requestId.toHexString(), owner);

        verify(requestRepo).save(any());
    }

    @Test
    public void testDeactivateRequestBadFormat() {
        assertThrows(BadFormatException.class, () ->
                requestService.deactivateRequest("123", "owner"));

        verify(requestRepo, never()).findByIdAndOwner(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testDeactivateRequestRequestNotFound() {
        ObjectId requestId = new ObjectId();
        String owner = "owner";

        when(requestRepo.findByIdAndOwner(requestId, owner)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () ->
                requestService.deactivateRequest(requestId.toHexString(), owner));


        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateRequest() throws CategoryNotFoundException, BadFormatException, TemplateNotFoundException {
        ObjectId requestId = new ObjectId();
        ObjectId templateId = new ObjectId();
        ObjectId categoryId = new ObjectId();
        String subject = "owner";

        Request request = new Request(subject, "title", templateId, categoryId, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true);
        Template existingTemplate = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existingTemplate));
        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(true);
        when(requestRepo.findByIdAndOwner(requestId, subject)).thenReturn(Optional.of(request));

        String newTitle = "test";
        List<String> newParticipants = List.of("participant");
        requestService.updateRequest(requestId.toHexString(), subject, newTitle, templateId.toHexString(), newParticipants, new ArrayList<>());

        ArgumentCaptor<Request> saveArgumentCaptor = ArgumentCaptor.forClass(Request.class);

        verify(requestRepo).save(saveArgumentCaptor.capture());

        assertEquals(newTitle, saveArgumentCaptor.getValue().getTitle());
        assertEquals(categoryId, saveArgumentCaptor.getValue().getCategoryId());
        assertEquals(newParticipants, saveArgumentCaptor.getValue().getParticipants());
    }

    @Test
    public void testUpdateRequestBadId() {
        assertThrows(BadFormatException.class, () ->
                requestService.updateRequest("123", "subject", "title", new ObjectId().toHexString(), new ArrayList<>(), new ArrayList<>()));

        verify(templateRepo, never()).findById(any());
        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(requestRepo, never()).findByIdAndOwner(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateRequestTemplateId() {
        assertThrows(BadFormatException.class, () ->
                requestService.updateRequest(new ObjectId().toHexString(), "subject", "title", "123", new ArrayList<>(), new ArrayList<>()));

        verify(templateRepo, never()).findById(any());
        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(requestRepo, never()).findByIdAndOwner(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateRequestTemplateDoesntExist() {
        ObjectId templateId = new ObjectId();

        when(templateRepo.findById(templateId)).thenReturn(Optional.empty());

        assertThrows(TemplateNotFoundException.class, () ->
                requestService.updateRequest(new ObjectId().toHexString(), "subject", "title", templateId.toHexString(), new ArrayList<>(), new ArrayList<>()));

        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(requestRepo, never()).findByIdAndOwner(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateRequestCategoryDoesntExist() {
        ObjectId categoryId = new ObjectId();
        ObjectId templateId = new ObjectId();
        Template existingTemplate = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existingTemplate));
        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () ->
                requestService.updateRequest(new ObjectId().toHexString(), "subject", "title", templateId.toHexString(), new ArrayList<>(), new ArrayList<>()));

        verify(requestRepo, never()).findByIdAndOwner(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateRequestRequestDoesntExist() throws CategoryNotFoundException, BadFormatException, TemplateNotFoundException {
        String subject = "subject";

        ObjectId requestId = new ObjectId();
        ObjectId categoryId = new ObjectId();
        ObjectId templateId = new ObjectId();
        Template existingTemplate = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());

        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existingTemplate));
        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(true);
        when(requestRepo.findByIdAndOwner(requestId, subject)).thenReturn(Optional.empty());

        requestService.updateRequest(requestId.toHexString(), subject, "title", templateId.toHexString(), new ArrayList<>(), new ArrayList<>());

        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testShareRequestBadId() {
        assertThrows(BadFormatException.class, () ->
                requestService.shareRequest("123", "owner", "share"));

        verify(requestRepo, never()).findByIdAndOwner(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testShareRequestNotFound() {
        ObjectId objectId = new ObjectId();
        String owner = "owner";
        String toShare = "share";

        when(requestRepo.findByIdAndOwner(objectId, owner)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () ->
                requestService.shareRequest(objectId.toHexString(), owner, toShare));

        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testShareRequestDuplicate() {
        ObjectId requestId = new ObjectId();
        String owner = "owner";
        String toShare = "share";

        Request request = new Request(owner, "title", new ObjectId(), new ObjectId(), new ArrayList<>(),
                List.of(new Share(toShare, LocalDateTime.now(), ShareStatus.PENDING, false, false)),
                new ArrayList<>(), true);
        request.setId(requestId);

        when(requestRepo.findByIdAndOwner(requestId, owner)).thenReturn(Optional.of(request));

        assertThrows(DuplicateException.class, () ->
                requestService.shareRequest(requestId.toHexString(), owner, toShare));

        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testShareRequest() throws DuplicateException, BadFormatException, RequestNotFoundException {
        ObjectId requestId = new ObjectId();
        String owner = "owner";
        String toShare = "share";

        Request request = new Request(owner, "title", new ObjectId(), new ObjectId(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        request.setId(requestId);

        when(requestRepo.findByIdAndOwner(requestId, owner)).thenReturn(Optional.of(request));

        requestService.shareRequest(requestId.toHexString(), owner, toShare);

        verify(requestRepo).save(any());
    }

    @Test
    public void testUpdateShareStudentActive() throws BadFormatException, RequestNotFoundException, ShareNotFoundException {
        ObjectId requestId = new ObjectId();
        String owner = "owner";
        String share = "share";
        boolean studentActive = false;
        boolean teacherActive = false;

        Request request = new Request(owner, "title", new ObjectId(), new ObjectId(), new ArrayList<>(),
                List.of(new Share(share, LocalDateTime.now(), ShareStatus.PENDING, studentActive, teacherActive)),
                new ArrayList<>(), true);
        request.setId(requestId);

        when(requestRepo.findByIdAndOwner(requestId, owner)).thenReturn(Optional.of(request));

        requestService.updateShareStudentActive(requestId.toHexString(), owner, share, !studentActive);

        verify(requestRepo).save(any());
    }

    @Test
    public void testUpdateShareStudentActiveBadId() {
        assertThrows(BadFormatException.class, () ->
                requestService.updateShareStudentActive("123", "student", "subject", true));

        verify(requestRepo, never()).findByIdAndOwner(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateShareStudentActiveRequestNotFound() {
        ObjectId requestId = new ObjectId();
        String owner = "owner";
        String subject = "subject";

        when(requestRepo.findByIdAndOwner(requestId, owner)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () ->
                requestService.updateShareStudentActive(requestId.toHexString(), owner, subject, true));

        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateShareStudentActiveShareNotFound() {
        ObjectId requestId = new ObjectId();
        String owner = "owner";
        String subject = "subject";

        Request request = new Request("owner", "title", new ObjectId(), new ObjectId(), new ArrayList<>(),
                List.of(new Share("123", LocalDateTime.now(), ShareStatus.PENDING, true, true)),
                new ArrayList<>(), true);
        request.setId(requestId);

        when(requestRepo.findByIdAndOwner(requestId, owner)).thenReturn(Optional.of(request));

        assertThrows(ShareNotFoundException.class, () ->
                requestService.updateShareStudentActive(requestId.toHexString(), owner, subject, true));


        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testRevokeShareTeacher() throws BadFormatException, RequestNotFoundException, ShareNotFoundException {
        ObjectId requestId = new ObjectId();
        String share = "share";

        Request request = new Request("owner", "title", new ObjectId(), new ObjectId(), new ArrayList<>(),
                List.of(new Share(share, LocalDateTime.now(), ShareStatus.PENDING, true, true)),
                new ArrayList<>(), true);
        request.setId(requestId);

        when(requestRepo.findBySharesContains(requestId, share)).thenReturn(request);

        requestService.revokeShareTeacher(requestId.toHexString(), share);

        verify(requestRepo).save(any());
    }

    @Test
    public void testRevokeShareTeacherBadId() {
        assertThrows(BadFormatException.class, () ->
                requestService.revokeShareTeacher("123", "subject"));

        verify(requestRepo, never()).findBySharesContains(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testRevokeShareTeacherRequestNotFound() {
        ObjectId requestId = new ObjectId();
        String share = "share";

        when(requestRepo.findBySharesContains(requestId, share)).thenReturn(null);

        assertThrows(RequestNotFoundException.class, () ->
                requestService.revokeShareTeacher(requestId.toHexString(), share));

        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateShare() throws BadFormatException, RequestNotFoundException {
        ObjectId requestId = new ObjectId();
        String share = "share";

        Request request = new Request("owner", "title", new ObjectId(), new ObjectId(), new ArrayList<>(),
                List.of(new Share(share, LocalDateTime.now(), ShareStatus.PENDING, false, false)),
                new ArrayList<>(), true);
        request.setId(requestId);

        when(requestRepo.findBySharesContains(requestId, share)).thenReturn(request);

        requestService.updateShare(requestId.toHexString(), share, ShareStatus.ACCEPTED);

        verify(requestRepo).save(any());
    }

    @Test
    public void testUpdateShareBadId() {
        assertThrows(BadFormatException.class, () ->
                requestService.updateShare("123", "subject", ShareStatus.ACCEPTED));

        verify(requestRepo, never()).findBySharesContains(any(), any());
        verify(requestRepo, never()).save(any());
    }

    @Test
    public void testUpdateShareRequestNotFound() {
        ObjectId requestId = new ObjectId();
        String share = "share";

        when(requestRepo.findBySharesContains(requestId, share)).thenReturn(null);

        assertThrows(RequestNotFoundException.class, () ->
                requestService.updateShare(requestId.toHexString(), share, ShareStatus.ACCEPTED));

        verify(requestRepo, never()).save(any());
    }
}
