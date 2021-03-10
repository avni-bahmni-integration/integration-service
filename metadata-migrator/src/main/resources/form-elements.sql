select c.uuid, cn.name, foo.concept_set form_id
from concept c
       join concept_datatype cd on cd.concept_datatype_id = c.datatype_id
       join (select cs_1.concept_id, cs_1.concept_set concept_set from concept_set cs_1 where cs_1.concept_set = ?

             union all

             select cs_2.concept_id, cs_1.concept_set concept_set
             from concept_set cs_1
                    join concept_set cs_2 on cs_1.concept_id = cs_2.concept_set
             where cs_1.concept_set = ?

             union all

             select cs_3.concept_id, cs_3.concept_set concept_set
             from concept_set cs_1
                    join concept_set cs_2 on cs_1.concept_id = cs_2.concept_set
                    join concept_set cs_3 on cs_2.concept_id = cs_3.concept_set
             where cs_1.concept_set = ?

             union all

             select cs_4.concept_id, cs_4.concept_set concept_set
             from concept_set cs_1
                    join concept_set cs_2 on cs_1.concept_id = cs_2.concept_set
                    join concept_set cs_3 on cs_2.concept_id = cs_3.concept_set
                    join concept_set cs_4 on cs_3.concept_id = cs_4.concept_set
             where cs_1.concept_set = ?) foo on c.concept_id = foo.concept_id
       left outer join concept_name cn on cn.concept_id = c.concept_id
where c.is_set = 0
  and cn.concept_name_type = 'SHORT'