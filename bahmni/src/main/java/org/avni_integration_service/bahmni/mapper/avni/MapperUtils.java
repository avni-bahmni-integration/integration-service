package org.avni_integration_service.bahmni.mapper.avni;

import org.avni_integration_service.bahmni.contract.OpenMRSVisit;
import org.avni_integration_service.util.FormatAndParseUtil;

import java.util.Calendar;
import java.util.Date;

public class MapperUtils {
    public static String getEventDateTime(Date avniDateTime, OpenMRSVisit visit) {
        var eventDateTime = (avniDateTime.before(visit.getStartDatetime()) ||
                              avniDateTime.after(Calendar.getInstance().getTime()))
                ? visit.getStartDatetime()
                : avniDateTime;
        return FormatAndParseUtil.toISODateString(eventDateTime);
    }
}
