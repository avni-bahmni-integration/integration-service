package org.avni_integration_service.bahmni.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public enum IntegrationTask {
//    Bahmni lab results are not present in the atom feed hence has to be handled separately
    AvniSubject, AvniEnrolment, AvniProgramEncounter, AvniGeneralEncounter,
    BahmniPatient, BahmniEncounter, BahmniLabResult,
    AvniErrorRecords, BahmniErrorRecords,
    BahmniVisitDateFix;

    public static List<IntegrationTask> getTasks(String taskNames) {
        if (taskNames.equals("all"))
            return Arrays.asList(IntegrationTask.values());

        List<IntegrationTask> tasks = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(taskNames, ",");
        while (stringTokenizer.hasMoreTokens()) {
            tasks.add(IntegrationTask.valueOf(stringTokenizer.nextToken()));
        }
        return tasks;
    }
}
