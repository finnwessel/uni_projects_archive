package de.hsflensburg.dataservice.service;

import de.hsflensburg.dataservice.domain.model.*;
import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import de.hsflensburg.dataservice.exceptions.*;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.CategoryNotFoundException;
import de.hsflensburg.dataservice.exceptions.RequestNotFoundException;
import de.hsflensburg.dataservice.exceptions.TemplateNotFoundException;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.RequestRepo;
import de.hsflensburg.dataservice.repository.TemplateRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepo requestRepo;
    private final CategoryRepo categoryRepo;
    private final TemplateRepo templateRepo;

    public Request createRequest(String owner, String title, String templateId, List<String> participants,
                                List<BaseComp> components) throws BadFormatException, CategoryNotFoundException, TemplateNotFoundException {
        if (!ObjectId.isValid(templateId)) {
            throw new BadFormatException();
        }

        ObjectId templateObjectId = new ObjectId(templateId);
        Optional<Template> template = templateRepo.findById(templateObjectId);

        if (template.isEmpty()) {
            throw new TemplateNotFoundException();
        }

        ObjectId categoryObjectId = template.get().getCategoryId();

        boolean categoryExists = categoryRepo.existsByIdAndType(categoryObjectId, CategoryType.REQUEST.toString());

        if (!categoryExists) {
            throw new CategoryNotFoundException();
        }

        Request newRequest = new Request(owner, title, templateObjectId, categoryObjectId, participants, new ArrayList<>(), components, true);
        requestRepo.save(newRequest);

        return newRequest;
    }

    public Optional<Request> getByIdAndEntitled(String id, String subject) throws BadFormatException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId requestId = new ObjectId(id);

        return Optional.ofNullable(requestRepo.findByIdAndOwnerOrParticipantsContainsOrSharesContains(requestId, subject));
    }

    public List<Request> getRequestsByEntitled(String subject) {
        return requestRepo.findAllByOwnerOrParticipantsContainsOrSharesContains(subject);
    }

    public Page<Request> getRequestsByQueryAndEntitled(String subject, Query query, Pageable pageable) {
        return requestRepo.findByQuery(subject, query, pageable);
    }

    public boolean existsByIdAndOwner(String id, String subject) throws BadFormatException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId requestId = new ObjectId(id);

        return requestRepo.existsByIdAndOwner(requestId, subject);
    }

    public Request deactivateRequest(String id, String owner) throws BadFormatException, RequestNotFoundException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        Optional<Request> request = requestRepo.findByIdAndOwner(new ObjectId(id), owner);

        if (request.isEmpty()) {
            throw new RequestNotFoundException();
        }

        request.get().setActive(false);

        return requestRepo.save(request.get());
    }

    public Optional<Request> updateRequest(String id, String subject, String newTitle, String newTemplateId, List<String> newParticipants,
                                           List<BaseComp> newComponents) throws BadFormatException, CategoryNotFoundException, TemplateNotFoundException {
        if (!ObjectId.isValid(id) || !ObjectId.isValid(newTemplateId)) {
            throw new BadFormatException();
        }

        ObjectId templateId = new ObjectId(newTemplateId);
        Optional<Template> template = templateRepo.findById(templateId);

        if (template.isEmpty()) {
            throw new TemplateNotFoundException();
        }

        ObjectId categoryId = template.get().getCategoryId();
        boolean categoryExists = categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString());

        if (!categoryExists) {
            throw new CategoryNotFoundException();
        }

        Optional<Request> optionalRequest = requestRepo.findByIdAndOwner(new ObjectId(id), subject);

        if (optionalRequest.isEmpty()) {
            return Optional.empty();
        }

        Request request = optionalRequest.get();
        request.setTitle(newTitle);
        request.setTemplateId(templateId);
        request.setCategoryId(categoryId);
        request.setParticipants(newParticipants);
        request.setComponents(newComponents);

        requestRepo.save(request);

        return Optional.of(request);
    }

    public Request shareRequest(String id, String owner, String toShare) throws BadFormatException, RequestNotFoundException, DuplicateException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId objectId = new ObjectId(id);

        Optional<Request> request = requestRepo.findByIdAndOwner(objectId, owner);

        if (request.isEmpty()) {
            throw new RequestNotFoundException();
        }

        List<Share> shares = request.get().getShares();

        if (shares.stream().anyMatch((share) -> share.getSubject().equals(toShare))) {
            throw new DuplicateException();
        }

        shares.add(new Share(toShare, LocalDateTime.now(), ShareStatus.PENDING, true, true));
        request.get().setShares(shares);

        return requestRepo.save(request.get());
    }

    public Request updateShareStudentActive(String id, String student, String subject, boolean active) throws BadFormatException, RequestNotFoundException, ShareNotFoundException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId objectId = new ObjectId(id);
        Optional<Request> request = requestRepo.findByIdAndOwner(objectId, student);

        if (request.isEmpty()) {
            throw new RequestNotFoundException();
        }

        List<Share> shares = request.get().getShares();

        if (shares.stream().noneMatch((share) -> share.getSubject().equals(subject))) {
            throw new ShareNotFoundException();
        }

        shares = shares.stream().map((share) -> {
            if (share.getSubject().equals(subject)) {
                return new Share(share.getSubject(), share.getCreatedAt(), share.getStatus(), active, share.isTeacherActive());
            } else {
                return share;
            }
        }).toList();

        request.get().setShares(shares);

        return requestRepo.save(request.get());
    }

    public Request revokeShareTeacher(String id, String subject) throws BadFormatException, RequestNotFoundException, ShareNotFoundException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId objectId = new ObjectId(id);
        Request request = requestRepo.findBySharesContains(objectId, subject);

        if (request == null) {
            throw new RequestNotFoundException();
        }

        List<Share> shares = request.getShares();

        shares = shares.stream().map((share) -> {
            if (share.getSubject().equals(subject)) {
                return new Share(share.getSubject(), share.getCreatedAt(), share.getStatus(), share.isStudentActive(), false);
            } else {
                return share;
            }
        }).toList();

        request.setShares(shares);

        return requestRepo.save(request);
    }

    public Request updateShare(String id, String subject, ShareStatus newStatus) throws BadFormatException, RequestNotFoundException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId objectId = new ObjectId(id);

        Request request = requestRepo.findBySharesContains(objectId, subject);

        if (request == null) {
            throw new RequestNotFoundException();
        }

        List<Share> shares = request.getShares();

        shares = shares.stream().peek((share) -> {
            if (share.getSubject().equals(subject)) {
                share.setStatus(newStatus);
            }
        }).toList();

        request.setShares(shares);

        return requestRepo.save(request);
    }

}
