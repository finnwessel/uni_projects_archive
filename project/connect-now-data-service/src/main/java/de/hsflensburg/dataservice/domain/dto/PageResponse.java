package de.hsflensburg.dataservice.domain.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse<T> {
    private int totalPages;
    private long totalItems;
    private int currentPage;
    private boolean first;
    private boolean last;
    private int itemsPerPage;
    private int pageSize;

    private List<T> items;

    public void setPage(Page page, List<T> items) {
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
        this.currentPage = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.itemsPerPage = page.getNumberOfElements();
        this.pageSize = page.getSize();
        this.items = items;
    }
}
