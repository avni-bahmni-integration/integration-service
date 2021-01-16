SU:=$(shell id -un)
DB=bahmni_avni
ADMIN_USER=bahmni_avni_admin

build_db:
	-psql -h localhost -U $(SU) -d postgres -c "create user $(ADMIN_USER) with password 'password' createrole";
	-psql -h localhost -U $(SU) -d postgres -c 'create database $(DB) with owner $(ADMIN_USER)';

drop_roles:
	-psql -h localhost -U $(SU) -d postgres -c 'drop role $(ADMIN_USER)';

clean_db:
	-psql -h localhost -U $(SU) -d postgres -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$(database)' AND pid <> pg_backend_pid()"
	-psql -h localhost -U $(SU) -d postgres -c 'drop database $(DB)';