package de.hsflensburg.dataservice.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.hsflensburg.dataservice.domain.dto.*;
import de.hsflensburg.dataservice.domain.mapper.ShareMapper;
import de.hsflensburg.dataservice.domain.validation.TemplateValidator;
import de.hsflensburg.dataservice.domain.mapper.RequestMapper;
import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.Request;
import de.hsflensburg.dataservice.exceptions.*;
import de.hsflensburg.dataservice.service.CategoryService;
import de.hsflensburg.dataservice.service.ChatService;
import de.hsflensburg.dataservice.service.QueryBuilderService;
import de.hsflensburg.dataservice.service.RequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;


@Tag(name = "Request")
@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;
    private final CategoryService categoryService;
    private final ChatService chatService;
    private final QueryBuilderService queryBuilderService;
    private final TemplateValidator templateValidator;

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    @RolesAllowed({"ROLE_STUDENT"})
    @PostMapping("create")
    public ResponseEntity<RequestResponse> create(@RequestBody @Valid CreateRequestRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            templateValidator.validate(request.getTemplateId(), request.getComponents());

            Request newRequest = requestService.createRequest(subject, request.getTitle(), request.getTemplateId(),
                    request.getParticipants(), request.getComponents());

            Optional<Category> category = categoryService.getCategory(newRequest.getCategoryId());

            CategoryInfo categoryInfo = new CategoryInfo();

            if (category.isPresent()) {
                categoryInfo.setName(category.get().getName());
                categoryInfo.setColor(category.get().getColor());
            }

            RequestResponse resp = RequestMapper.requestToRequestResponse(newRequest, categoryInfo);

            try {
                chatService.updateChat(newRequest.getId().toHexString(), newRequest.getOwner(), newRequest.getParticipants(),
                        ShareMapper.sharesToActiveShareList(newRequest.getShares()), newRequest.isActive());
            } catch (JsonProcessingException | AmqpConnectException e) {
                logger.error("Could not update chat information");
            }

            return ResponseEntity.created(URI.create("/request/" + newRequest.getId())).body(resp);
        } catch (BadFormatException | CategoryNotFoundException | TemplateNotFoundException | NoTemplateMatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping({"{id}"})
    public ResponseEntity<RequestResponse> get(@PathVariable String id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        boolean isTeacher = user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().contains("ROLE_TEACHER"));

        try {
            Optional<Request> request = requestService.getByIdAndEntitled(id, subject);

            if (request.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Optional<Category> category = categoryService.getCategory(request.get().getCategoryId());

            CategoryInfo categoryInfo = new CategoryInfo();

            if (category.isPresent()) {
                categoryInfo.setName(category.get().getName());
                categoryInfo.setColor(category.get().getColor());
            }

            RequestResponse resp = isTeacher ? RequestMapper.requestToFilteredRequestResponse(request.get(), subject, categoryInfo) :
                    RequestMapper.requestToRequestResponse(request.get(), categoryInfo);

            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RolesAllowed({"ROLE_TEACHER", "ROLE_STUDENT"})
    @PostMapping("filter")
    public ResponseEntity<PageResponse<RequestResponse>> filterAll(@RequestBody RequestFilter filter,
                                                        @RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "20") int size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        if (page < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Query query;

            if (user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().contains("ROLE_STUDENT"))) {
                query = queryBuilderService.buildStudentQueryByRequestFilter(subject, filter);
            } else {
                query = queryBuilderService.buildTeacherQueryByRequestFilter(subject, filter);
            }
            PageResponse<RequestResponse> pageResponse = new PageResponse<>();

            Pageable pageable = queryBuilderService.getPageable(page - 1, size,
                    filter.getSortBy() != null ? filter.getSortBy() : RequestFilter.SortOption.NEWEST);

            Page<Request> requests = requestService.getRequestsByQueryAndEntitled(subject, query, pageable);

            List<ObjectId> categoryIds = requests.stream().map(Request::getCategoryId).distinct().toList();
            List<Category> categories = categoryService.getCategoriesById(categoryIds);
            Map<ObjectId, CategoryInfo> categoryInfos =
                    categories.stream().collect(Collectors.toMap(Category::getId, (entry) -> new CategoryInfo(entry.getName(), entry.getColor()), (x, y) -> x, HashMap::new));

            List<RequestResponse> response = requests.stream().map(req ->
                    RequestMapper.requestToFilteredRequestResponse(req, subject, categoryInfos.get(req.getCategoryId()))
            ).toList();

            pageResponse.setPage(requests, response);

            return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
        } catch (BadFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<RequestResponse>> getAll() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        boolean isTeacher = user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().contains("ROLE_TEACHER"));

        List<Request> requests = requestService.getRequestsByEntitled(subject);

        if (requests.size() == 0) {
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>());
        }

        List<ObjectId> categoryIds = requests.stream().map(Request::getCategoryId).distinct().toList();
        List<Category> categories = categoryService.getCategoriesById(categoryIds);
        Map<ObjectId, CategoryInfo> categoryInfos =
                categories.stream().collect(Collectors.toMap(Category::getId, (entry) -> new CategoryInfo(entry.getName(), entry.getColor()), (x, y) -> x, HashMap::new));

        List<RequestResponse> response = requests.stream().map(req ->
            isTeacher ? RequestMapper.requestToFilteredRequestResponse(req, subject, categoryInfos.get(req.getCategoryId())) :
                    RequestMapper.requestToRequestResponse(req, categoryInfos.get(req.getCategoryId()))
        ).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @RolesAllowed({"ROLE_STUDENT"})
    @PostMapping({"{id}/deactivate"})
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            Request request = requestService.deactivateRequest(id, subject);

            try {
                chatService.updateChat(request.getId().toHexString(), request.getOwner(), request.getParticipants(),
                        ShareMapper.sharesToActiveShareList(request.getShares()), request.isActive());
            } catch (JsonProcessingException | AmqpConnectException e) {
                logger.error("Could not update chat information");
            }

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (BadFormatException | RequestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RolesAllowed({"ROLE_STUDENT"})
    @PutMapping({"{id}"})
    public ResponseEntity<RequestResponse> update(@PathVariable String id, @RequestBody @Valid UpdateRequestRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        boolean isTeacher = user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().contains("ROLE_TEACHER"));

        try {
            templateValidator.validate(request.getTemplateId(), request.getComponents());

            Optional<Request> req = requestService.updateRequest(id, subject, request.getTitle(), request.getTemplateId(),
                    request.getParticipants(), request.getComponents());

            if (req.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Optional<Category> category = categoryService.getCategory(req.get().getCategoryId());
            CategoryInfo categoryInfo = new CategoryInfo();

            if (category.isPresent()) {
                categoryInfo.setName(category.get().getName());
                categoryInfo.setColor(category.get().getColor());
            }

            RequestResponse resp = isTeacher ? RequestMapper.requestToFilteredRequestResponse(req.get(), subject, categoryInfo) :
                    RequestMapper.requestToRequestResponse(req.get(), categoryInfo);

            try {
                chatService.updateChat(req.get().getId().toHexString(), req.get().getOwner(), req.get().getParticipants(),
                        ShareMapper.sharesToActiveShareList(req.get().getShares()), req.get().isActive());
            } catch (JsonProcessingException | AmqpConnectException e) {
                logger.error("Could not update chat information");
            }

            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } catch (BadFormatException | CategoryNotFoundException | NoTemplateMatchException | TemplateNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /*
     * Shares request to teacher (student only)
     */
    @RolesAllowed({"ROLE_STUDENT"})
    @PostMapping({"share/{id}"})
    public ResponseEntity<Void> share(@PathVariable String id, @RequestBody @Valid ShareRequestRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            Request req = requestService.shareRequest(id, subject, request.getSubject());

            try {
                chatService.updateChat(req.getId().toHexString(), req.getOwner(), req.getParticipants(),
                        ShareMapper.sharesToActiveShareList(req.getShares()), req.isActive());
            } catch (JsonProcessingException | AmqpConnectException e) {
                logger.error("Could not update chat information");
            }

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RequestNotFoundException | BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DuplicateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /*
     * Revokes share (student & teacher)
     */
    @RolesAllowed({"ROLE_STUDENT", "ROLE_TEACHER"})
    @PutMapping({"share/{id}/revoke"})
    public ResponseEntity<Void> revokeShare(@PathVariable String id, @RequestBody(required = false) @Valid ShareRequestRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            Request req;

            if (user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().contains("ROLE_STUDENT"))) {
                //User has role student
                if (request == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }

                req = requestService.updateShareStudentActive(id, subject, request.getSubject(), false);
            } else if (user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().contains("ROLE_TEACHER"))) {
                //User has role teacher
                req = requestService.revokeShareTeacher(id, subject);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            try {
                chatService.updateChat(req.getId().toHexString(), req.getOwner(), req.getParticipants(),
                        ShareMapper.sharesToActiveShareList(req.getShares()), req.isActive());
            } catch (JsonProcessingException | AmqpConnectException e) {
                logger.error("Could not update chat information");
            }

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ShareNotFoundException | RequestNotFoundException | BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /*
     * Re-enact share (student only)
     */
    @RolesAllowed({"ROLE_STUDENT"})
    @PutMapping({"share/{id}/reenact"})
    public ResponseEntity<Void> reenactShare(@PathVariable String id, @RequestBody @Valid ShareRequestRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            Request req = requestService.updateShareStudentActive(id, subject, request.getSubject(), true);

            try {
                chatService.updateChat(req.getId().toHexString(), req.getOwner(), req.getParticipants(),
                        ShareMapper.sharesToActiveShareList(req.getShares()), req.isActive());
            } catch (JsonProcessingException | AmqpConnectException e) {
                System.out.println();
            }

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ShareNotFoundException | RequestNotFoundException | BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /*
     * Updates share (teacher only)
     */
    @RolesAllowed({"ROLE_TEACHER"})
    @PutMapping({"share/{id}/update"})
    public ResponseEntity<Void> updateShare(@PathVariable String id, @RequestBody @Valid UpdateShareRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String subject = user.getUsername();

        try {
            requestService.updateShare(id, subject, request.getStatus());

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RequestNotFoundException | BadFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
