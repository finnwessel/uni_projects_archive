package de.hsflensburg.dataservice.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestFilter {
    public Status status;
    public SortOption sortBy;
    public SearchOption search;
    public String query;
    public String categoryId;
    public Range range;

    public enum SortOption {
        NEWEST,
        OLDEST
    }

    public enum SearchOption {
        MEMBER,
        TITLE
    }

    @Data
    public class Range {
        LocalDateTime start;
        LocalDateTime end;
    }

    @Data
    public class Status {
        boolean accepted;
        boolean declined;
        boolean pending;
    }

    public Boolean active;
}
