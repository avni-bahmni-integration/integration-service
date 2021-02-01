package org.bahmni_avni_integration.contract.avni;

public class SubjectsResponse {
    private String totalElements;
    private String totalPages;
    private String pageSize;
    private Subject[] content;

    public String getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(String totalElements) {
        this.totalElements = totalElements;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public Subject[] getContent() {
        return content;
    }

    public void setContent(Subject[] content) {
        this.content = content;
    }
}