package org.bahmni_avni_integration.migrator.domain;

public enum AvniConceptDataType {
    Numeric,
    Text,
    Notes,
    Coded,
    NA,
    Date,
    DateTime,
    Time,
    Duration,
    Image,
    Id,
    Video,
    Subject,
    Location,
    PhoneNumber,
    GroupAffiliation,
    Audio;

    public String getBahmniDataType() {
        return switch (this) {
            case Numeric -> "Numeric";
            case Text, Notes, Duration, Id, PhoneNumber -> "Text";
            case Coded -> "Coded";
            case Date -> "Date";
            case DateTime -> "Datetime";
            case Time -> "Time";
            default -> "N/A";
        };
    }
}
