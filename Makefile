SU:=$(shell id -un)
DB=bahmni_avni
ADMIN_USER=bahmni_avni_admin

define _build_db
	-psql -h localhost -U $(SU) -d postgres -c "create user $(ADMIN_USER) with password 'password' createrole";
	-psql -h localhost -U $(SU) -d postgres -c 'create database $1 with owner $(ADMIN_USER)';
endef

define _drop_db
    -psql postgres -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$1' AND pid <> pg_backend_pid()"
    -psql postgres -c 'drop database $1';
endef

define _run_server
	java -jar build/libs/bahmni_avni_integration-0.0.1-SNAPSHOT.jar --cron.main="0/3 * * * * ?" --avni.api.url=https://staging.avniproject.org/ --avni.impl.username=test-user@bahmni_ashwini --avni.impl.password=password
endef

build-db:
	$(call _build_db,bahmni_avni)

drop-db:
	$(call _drop_db,bahmni_avni)

build-test-db:
	$(call _build_db,bahmni_avni_test)

drop-test-db:
	$(call _drop_db,bahmni_avni_test)

drop-roles:
	-psql -h localhost -U $(SU) -d postgres -c 'drop role $(ADMIN_USER)';

clean-db:
	-psql -h localhost -U $(SU) -d postgres -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$(database)' AND pid <> pg_backend_pid()"
	-psql -h localhost -U $(SU) -d postgres -c 'drop database $(DB)';

build-server: ## Builds the jar file
	./gradlew clean build -x test

run-server: build-db build-server
	$(call _run_server)

test-server: drop-test-db build-test-db build-server
	./gradlew unitTest

test-server-all: drop-test-db build-test-db
	./gradlew clean build

open-unit-test-results: ## To be used when test-server is run
	open build/reports/tests/unitTest/index.html

open-test-results: ## To be used when test-server-all is run
	open build/reports/tests/test/index.html