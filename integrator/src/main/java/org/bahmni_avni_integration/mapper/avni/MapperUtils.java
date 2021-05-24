package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSVisit;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;

import java.util.Calendar;
import java.util.Date;

public class MapperUtils {

    public static String getEntityDateTime(Date avniEntityDateTime, OpenMRSVisit visit) {
        var encounterDateTime = avniEntityDateTime;
        var visitStartDateTime = visit.getStartDatetime();
        if (encounterDateTime.before(visitStartDateTime) || encounterDateTime.after(Calendar.getInstance().getTime())) {
            encounterDateTime = FormatAndParseUtil.addSeconds(visitStartDateTime, 1);
        }
        return FormatAndParseUtil.toISODateStringWithTimezone(encounterDateTime);
    }
}