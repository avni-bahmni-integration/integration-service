package org.avni_integration_service.amrit;

import org.assertj.core.api.Assertions;
import org.avni_integration_service.amrit.dto.AmritFetchIdentityResponse;
import org.avni_integration_service.amrit.dto.AmritUpsertBeneficiaryResponse;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.junit.jupiter.api.Test;

public class AmritResponseCastTest {

    @Test
    void convertResponseJsonToAmritFetchIdentityResponse() {
        String jsonResponse = "{\"data\":[276052406423],\"statusCode\":200,\"errorMessage\":\"Success\",\"status\":\"Success\"}";
        AmritFetchIdentityResponse response = ObjectJsonMapper.readValue(jsonResponse, AmritFetchIdentityResponse.class);
        Assertions.assertThat(response.getIds()).isNotNull().isNotEmpty().hasSize(1);
        Assertions.assertThat(response.getIds().get(0)).isEqualTo(276052406423l);
    }

    //{"data":{"response":"4 records synced"},"statusCode":200,"errorMessage":"Success","status":"Success"}
    @Test
    void convertResponseJsonToAmritUpsertBeneficiaryResponse() {
        String jsonResponse = "{\"data\":{\"response\":\"4 records synced\"},\"statusCode\":200,\"errorMessage\":\"Success\",\"status\":\"Success\"}";
        AmritUpsertBeneficiaryResponse response = ObjectJsonMapper.readValue(jsonResponse, AmritUpsertBeneficiaryResponse.class);
        Assertions.assertThat(response.getResponse()).isNotNull().isNotEmpty();
        Assertions.assertThat(response.getResponse()).containsKey("response");
        Assertions.assertThat(response.getResponse())
                .extracting("response")
                .contains("4 records synced");
    }
}
