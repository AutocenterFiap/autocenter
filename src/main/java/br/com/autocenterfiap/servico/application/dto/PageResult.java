package br.com.autocenterfiap.servico.application.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageResult<T> {
    
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    public PageResult(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNextPage() {
        return pageNumber < totalPages - 1;
    }

    public boolean hasPreviousPage() {
        return pageNumber > 0;
    }

    public int getNumberOfElements() {
        return content.size();
    }

    public <R> PageResult<R> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream()
            .map(mapper)
            .collect(Collectors.toList());
        
        return new PageResult<>(mappedContent, pageNumber, pageSize, totalElements);
    }
}
