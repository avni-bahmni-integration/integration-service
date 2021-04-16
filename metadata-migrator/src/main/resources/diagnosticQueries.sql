-- List of concepts which are marked as concept set but they have observations but no set members in them
select cn.name, concept.uuid, concept_set.* from concept
             join concept_datatype cd on concept.datatype_id = cd.concept_datatype_id
             join concept_name cn on concept.concept_id = cn.concept_id
             join obs on concept.concept_id = obs.concept_id
             left outer join concept_set on concept.concept_id = concept_set.concept_set
where concept.is_set = true and cn.concept_name_type = 'FULLY_SPECIFIED' and concept_set.concept_set_id is null
group by cn.name
order by 1;

-- Concepts in avni which are of type N/A but they are associated to a form element
select form_element.name, form.name from form_element
     join concept c on form_element.concept_id = c.id
     join form_element_group feg on form_element.form_element_group_id = feg.id
     join form on feg.form_id = form.id
where c.data_type = 'N/A' and form_element.is_voided = false;