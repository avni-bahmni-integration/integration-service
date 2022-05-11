package org.avni_integration_service.integration_data.repository.avni;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvniIgnoredConceptsRepository {
    public List<String> getIgnoredConcepts() {
        return List.of("Reason(s) for Institutional Delivery",
                "Reason(s) for Institutional ANC");
    }
}
