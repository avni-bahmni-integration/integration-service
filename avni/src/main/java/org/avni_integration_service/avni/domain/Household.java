package org.avni_integration_service.avni.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.avni_integration_service.util.FormatAndParseUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Household {
    private static final String MembershipStartDate = "Membership start date";
    private static final String MembershipEndDate = "Membership end date";
    private static final String Role = "Role";

    protected Map<String, Object> map = new HashMap<>();
    protected Subject groupSubject = new Subject();
    protected Subject memberSubject = new Subject();

    @JsonIgnore
    public Subject getGroupSubject() {
        return groupSubject;
    }

    public void setGroupSubject(Subject groupSubject) {
        this.groupSubject = groupSubject;
    }

    @JsonIgnore
    public Subject getMemberSubject() {
        return memberSubject;
    }

    public void setMemberSubject(Subject memberSubject) {
        this.memberSubject = memberSubject;
    }

    public Object get(String name) {
        return map.get(name);
    }

    @JsonAnySetter
    public void set(final String name, final Object value) {
        map.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties(){
        return map;
    }

    @JsonIgnore
    public String getCreatedBy() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        return (String) audit.get("Created by");
    }

    @JsonIgnore
    public String getLastModifiedBy() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        return (String) audit.get("Last modified by");
    }

    @JsonIgnore
    public Date getCreateDate() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        String lastModifiedAtString = (String) audit.get("Created at");
        return FormatAndParseUtil.fromAvniDateTime(lastModifiedAtString);
    }

    @JsonIgnore
    public LocalDateTime getCreatedDateTime() {
        return FormatAndParseUtil.toLocalDateTime(getCreateDate());
    }

    @JsonIgnore
    public Date getLastModifiedDate() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        String lastModifiedAtString = (String) audit.get("Last modified at");
        return FormatAndParseUtil.fromAvniDateTime(lastModifiedAtString);
    }

    @JsonIgnore
    public LocalDateTime getLastModifiedDateTime() {
        return FormatAndParseUtil.toLocalDateTime(getLastModifiedDate());
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public void setVoided(boolean voided) {
        set("Voided", voided);
    }

    @JsonIgnore
    public Boolean getVoided() {
        return (Boolean) get("Voided");
    }

    public void setMembershipStartDate(String membershipStartDate) {
        set(MembershipStartDate, membershipStartDate);
    }

    @JsonIgnore
    public String getMembershipStartDate() {
        return (String) get(MembershipStartDate);
    }

    @JsonIgnore
    public String getMembershipEndDate() {
        return (String) get(MembershipEndDate);
    }

    public void setMembershipEndDate(String membershipEndDate) {
        set(MembershipEndDate, membershipEndDate);
    }

    @JsonIgnore
    public String getRole() {
        return (String) get(Role);
    }

    public void setRole(String role) {
        set(Role, role);
    }

}