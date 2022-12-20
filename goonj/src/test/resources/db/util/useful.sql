set role goonj;

delete
from individual
where id in (
    select individual.id
    from individual
             join users on individual.created_by_id = users.id
    where users.name = 'External API Client');

delete
from encounter
where encounter.individual_id in (
    select individual.id
    from individual
             join users on individual.created_by_id = users.id
    where users.name = 'External API Client');
