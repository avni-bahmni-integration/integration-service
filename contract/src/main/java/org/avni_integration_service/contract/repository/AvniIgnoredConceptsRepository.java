package org.avni_integration_service.contract.repository;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvniIgnoredConceptsRepository {
    public List<String> getIgnoredConcepts() {
        return List.of("Reason(s) for Institutional Delivery",
                "Reason(s) for Institutional ANC");
    }
}
