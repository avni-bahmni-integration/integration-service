package org.bahmni_avni_integration.contract.avni;

public class EncountersResponse {
    private String totalElements;
    private String totalPages;
    private String pageSize;
    private Encounter[] content;

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

    public Encounter[] getContent() {
        return content;
    }

    public void setContent(Encounter[] content) {
        this.content = content;
    }
}