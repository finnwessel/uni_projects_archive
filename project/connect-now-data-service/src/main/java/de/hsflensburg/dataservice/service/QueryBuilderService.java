package de.hsflensburg.dataservice.service;

import de.hsflensburg.dataservice.domain.dto.RequestFilter;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueryBuilderService {

    private List<Criteria> buildGeneralCriteriaByRequestFilter(RequestFilter filter) throws BadFormatException {
        List<Criteria> criteria = new ArrayList<>();

        if (filter.getQuery() != null && filter.getSearch() != null) {
            switch (filter.getSearch()) {
                case MEMBER -> criteria.add(new Criteria().orOperator(Criteria.where("owner").regex(filter.getQuery(), "i"),
                        Criteria.where("participants").regex(filter.getQuery(), "i")));

                case TITLE -> criteria.add(Criteria.where("title").regex(filter.getQuery(), "i"));
            }
        }

        if (filter.getCategoryId() != null) {
            if (!ObjectId.isValid(filter.getCategoryId())) {
                throw new BadFormatException();
            }

            criteria.add(Criteria.where("categoryId").is(new ObjectId(filter.getCategoryId())));
        }

        if (filter.getRange() != null) {
            LocalDateTime start = filter.getRange().getStart();
            LocalDateTime end = filter.getRange().getEnd();

            criteria.add(Criteria.where("createdAt").gte(start).lte(end));
        }

        if (filter.active != null) {
            criteria.add(Criteria.where("active").is(filter.active));
        }

        return criteria;
    }

    public Query buildStudentQueryByRequestFilter(String subject, RequestFilter filter) throws BadFormatException {
        List<Criteria> criteria = new ArrayList<>();

        if (filter.getStatus() != null) {
            List<Criteria> statusCriteria = new ArrayList<>();

            if (filter.getStatus().isAccepted()) {
                statusCriteria.add(Criteria.where("shares").elemMatch(Criteria.where("status").is("ACCEPTED")));
            }

            if (filter.getStatus().isDeclined()) {
                statusCriteria.add(Criteria.where("shares").elemMatch(Criteria.where("status").is("DECLINED")));
            }

            if (filter.getStatus().isPending()) {
                statusCriteria.add(Criteria.where("shares").elemMatch(Criteria.where("status").is("PENDING")));
            }

            if (statusCriteria.size() > 0) {
                criteria.add(new Criteria().orOperator(statusCriteria.toArray(new Criteria[0])));
            }
        }

        List<Criteria> generalCriteria = buildGeneralCriteriaByRequestFilter(filter);
        criteria.addAll(generalCriteria);

        criteria.add(new Criteria().orOperator(Criteria.where("owner").is(subject),
                Criteria.where("participants").in(subject)));

        return new Query(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
    }

    public Query buildTeacherQueryByRequestFilter(String teacher, RequestFilter filter) throws BadFormatException {
        List<Criteria> criteria = new ArrayList<>();

        if (filter.getStatus() != null) {
            List<Criteria> statusCriteria = new ArrayList<>();

            Criteria acceptedCriteria = new Criteria().andOperator(Criteria.where("subject").is(teacher),
                    Criteria.where("status").is("ACCEPTED"));
            if (filter.getStatus().isAccepted()) {
                statusCriteria.add(Criteria.where("shares").elemMatch(acceptedCriteria));
            }

            Criteria declinedCriteria = new Criteria().andOperator(Criteria.where("subject").is(teacher),
                    Criteria.where("status").is("DECLINED"));
            if (filter.getStatus().isDeclined()) {
                statusCriteria.add(Criteria.where("shares").elemMatch(declinedCriteria));
            }

            Criteria pendingCriteria = new Criteria().andOperator(Criteria.where("subject").is(teacher),
                    Criteria.where("status").is("PENDING"));
            if (filter.getStatus().isPending()) {
                statusCriteria.add(Criteria.where("shares").elemMatch(pendingCriteria));
            }

            if (statusCriteria.size() > 0) {
                criteria.add(new Criteria().orOperator(statusCriteria.toArray(new Criteria[0])));
            }
        }

        List<Criteria> generalCriteria = buildGeneralCriteriaByRequestFilter(filter);
        criteria.addAll(generalCriteria);

        criteria.add(Criteria.where("shares.subject").in(teacher));

        return new Query(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
    }

    public PageRequest getPageable(int page, int size, RequestFilter.SortOption dateSort) {
        return switch (dateSort) {
            case OLDEST -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
            case NEWEST -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        };
    }
}
