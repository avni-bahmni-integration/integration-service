package org.bahmni_avni_integration.mapper.avni;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSVisit;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;

import java.util.Calendar;
import java.util.Date;

public class MapperUtils {
    public static String getEntityDateTime(Date avniEntityDateTime, OpenMRSVisit visit) {
        var entityDateTime = (avniEntityDateTime.before(visit.getStartDatetime()) ||
                              avniEntityDateTime.after(Calendar.getInstance().getTime()))
                ? visit.getStartDatetime()
                : avniEntityDateTime;
        return FormatAndParseUtil.toISODateStringWithTimezone(entityDateTime);
    }
}