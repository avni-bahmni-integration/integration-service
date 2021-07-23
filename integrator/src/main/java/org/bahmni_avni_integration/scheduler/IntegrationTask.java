package org.bahmni_avni_integration.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public enum IntegrationTask {
    AvniSubject, AvniEnrolment, AvniProgramEncounter, AvniGeneralEncounter,
    BahmniPatient, BahmniEncounter,
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
