package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSVisit;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;

import java.util.Calendar;
import java.util.Date;

public class MapperUtils {
    public static String getEventDateTime(Date avniDateTime, OpenMRSVisit visit) {
        var eventDateTime = (avniDateTime.before(visit.getStartDatetime()) ||
                              avniDateTime.after(Calendar.getInstance().getTime()))
                ? visit.getStartDatetime()
                : avniDateTime;
        return FormatAndParseUtil.toISODateStringWithTimezone(eventDateTime);
    }
}