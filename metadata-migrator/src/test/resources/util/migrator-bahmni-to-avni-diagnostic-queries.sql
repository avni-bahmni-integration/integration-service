-- List of concepts which are marked as concept set but they have observations but no set members in them
-- OpenMRS
select cd.name, concept_class.name, concept.is_set, cn.name
from concept
         join concept_datatype cd on concept.datatype_id = cd.concept_datatype_id
         join concept_name cn on concept.concept_id = cn.concept_id
         join obs on concept.concept_id = obs.concept_id
         left outer join concept_set on concept.concept_id = concept_set.concept_set
where concept.is_set = true
  and cn.concept_name_type = 'FULLY_SPECIFIED'
  and concept_set.concept_set_id is null
group by cn.name
union
select cd.name, concept_class.name, concept.is_set, cn.name
from concept
         join concept_name cn on concept.concept_id = cn.concept_id
         join concept_datatype cd on concept.datatype_id = cd.concept_datatype_id
         join concept_class on concept.class_id = concept_class.concept_class_id
         join obs on concept.concept_id = obs.value_coded
where concept.is_set = true and cn.concept_name_type = 'FULLY_SPECIFIED'
group by cn.name;

-- Concepts in avni which are of type N/A but they are associated to a form element
-- Avni
select form_element.name, form.name
from form_element
         join concept c on form_element.concept_id = c.id
         join form_element_group feg on form_element.form_element_group_id = feg.id
         join form on feg.form_id = form.id
where c.data_type = 'N/A'
  and form_element.is_voided = false;

-- Concepts which are of type Coded but without answers. They should match between Bahmni and Avni.
-- Avni
select concept.name
from concept
         join audit on concept.audit_id = audit.id
         left outer join concept_answer ca on concept.id = ca.concept_id
         left outer join concept ac on ac.id = ca.answer_concept_id
where concept.data_type = 'Coded'
  and ca.id is null
  and audit.created_by_id = 360;
-- Bahmni
select concept.uuid, cn.name
from concept
         join concept_datatype cd on concept.datatype_id = cd.concept_datatype_id
         join concept_name cn on concept.concept_id = cn.concept_id
         left outer join concept_answer on concept.concept_id = concept_answer.concept_id
         left outer join concept ac on concept_answer.concept_answer_id = ac.concept_id
where concept_answer.concept_answer_id is null
  and cd.name = 'Coded'
  and concept.retired = false
  and cn.concept_name_type = 'FULLY_SPECIFIED'
  and cn.name not like '% [Avni]';

-- Concepts of type boolean should be created with answers on avni side.
-- OpenMRS
select count(*)
from concept
         join concept_name on concept.concept_id = concept_name.concept_id
         join concept_datatype cd on concept.datatype_id = cd.concept_datatype_id
where cd.name = 'Boolean'
  and concept_name_type = 'FULLY_SPECIFIED' and concept.retired = false;
-- Avni (difference of two should match)
select count(*) from concept
         join concept_answer ca on concept.id = ca.concept_id
         join concept ac on ac.id = ca.answer_concept_id
         join audit on concept.audit_id = audit.id
where ac.name = 'True [H]'  and audit.created_by_id = 360 and concept.is_voided = false and concept.name not like '%[HP]'
union
select count(*) from concept
         join concept_answer ca on concept.id = ca.concept_id
         join concept ac on ac.id = ca.answer_concept_id
         join audit on concept.audit_id = audit.id
where ac.name = 'True [H]'  and audit.created_by_id = 360 and concept.is_voided = false and concept.name like '%[HP]';

-- No concept should be created that is not a supported data type (since Avni database doesn't have this check)
-- Avni
select count(*) from concept
         join audit on concept.audit_id = audit.id
    where data_type not in ('Numeric', 'Text', 'Coded', 'NA', 'Date', 'DateTime', 'Time')
  and audit.created_by_id = 360;

-- Ignored concepts should match
-- OpenMRS
select count(*)
from concept c
         join concept_name cn on cn.concept_id = c.concept_id
         join concept_datatype cdt on cdt.concept_datatype_id = c.datatype_id
         join concept_class cc on cc.concept_class_id = c.class_id
where c.is_set = false
  and cn.concept_name_type = 'FULLY_SPECIFIED'
  and cc.name in ('Concept Attribute', 'Image', 'URL', 'Video')
  and cn.name not like '%[Avni]';
-- Integration db
select count(*) from ignored_bahmni_concept;

-- All new forms should have only one form_element_group and more than one form element in each group
-- Avni
select form.name, count(*) from form
    join audit on form.audit_id = audit.id
    left outer join form_element_group feg on form.id = feg.form_id
where audit.created_by_id = 360 and feg.id is null
group by form.name;
--
select feg.name, count(*)
from form
         join form_element_group feg on form.id = feg.form_id
         join audit on form.audit_id = audit.id
         left outer join form_element fe on feg.id = fe.form_element_group_id
where audit.created_by_id = 360 and fe.id is null
group by feg.name;
--
select form_mapping.id from form_mapping
    join audit on form_mapping.audit_id = audit.id
    left outer join form f on form_mapping.form_id = f.id
where audit.created_by_id = 360 and f.id is null;
