help:
	@IFS=$$'\n' ; \
	help_lines=(`fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//'`); \
	for help_line in $${help_lines[@]}; do \
	    IFS=$$'#' ; \
	    help_split=($$help_line) ; \
	    help_command=`echo $${help_split[0]} | sed -e 's/^ *//' -e 's/ *$$//'` ; \
	    help_info=`echo $${help_split[2]} | sed -e 's/^ *//' -e 's/ *$$//'` ; \
	    printf "%-30s %s\n" $$help_command $$help_info ; \
	done

SU:=$(shell id -un)
DB=bahmni_avni
ADMIN_USER=bahmni_avni_admin
postgres_user := $(shell id -un)

define _build_db
	-psql -h localhost -U $(SU) -d postgres -c "create user $(ADMIN_USER) with password 'password' createrole";
	-psql -h localhost -U $(SU) -d postgres -c 'create database $1 with owner $(ADMIN_USER)';
endef

define _drop_db
    -psql postgres -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$1' AND pid <> pg_backend_pid()"
    -psql postgres -c 'drop database $1';
endef

define _run_server
	java -jar build/libs/bahmni_avni_integration-0.0.1-SNAPSHOT.jar --app.cron.main="0/3 * * * * ?" --avni.api.url=https://staging.avniproject.org/ --avni.impl.username=test-user@bahmni_ashwini --avni.impl.password=password
endef

define _run_migrator
    . ./conf/local-test.conf
	java -jar --enable-preview metadata-migrator/build/libs/metadata-migrator-0.0.1-SNAPSHOT.jar run
endef

######## DATABASE LOCAL
# hashed password when password is password = $2a$10$RipvsoEJg4PtXOExTjg7Eu2WzHH1SBntIkuR.bzmZeU2TrbQoFtMW
# kept here for emergency purposes as we are not developing the entire login functionality
rebuild-db: drop-db build-db

build-db:
	$(call _build_db,bahmni_avni)
	./gradlew migrateDb

drop-db:
	$(call _drop_db,bahmni_avni)

create-test-db:
	$(call _build_db,bahmni_avni_test)

build-test-db: create-test-db
	./gradlew migrateTestDb

drop-test-db:
	$(call _drop_db,bahmni_avni_test)

rebuild-test-db: drop-test-db build-test-db

drop-roles:
	-psql -h localhost -U $(SU) -d postgres -c 'drop role $(ADMIN_USER)';
#######

####### BUILD, TEST, LOCAL RUN
build-server: ## Builds the jar file
	./gradlew clean build -x test

run-server: build-db build-server
	$(call _run_server)

run-migrator: build-server
	$(call _run_migrator)

test-server: drop-test-db build-test-db build-server
	./gradlew unitTest

setup-external-test-db: drop-test-db create-test-db
	sudo -u ${postgres_user} psql bahmni_avni_test -f dump.sql

test-server-external: drop-test-db setup-external-test-db
	./gradlew clean build

open-unit-test-results-integrator:
	open integrator/build/reports/tests/unitTest/index.html

open-unit-test-results-migrator:
	open metadata-migrator/build/reports/tests/unitTest/index.html

open-test-results: ## To be used when test-server-all is run
	open integrator/build/reports/tests/test/index.html
#######


####### RUN IN ENVIRONMENT
tunnel-server-debug-vagrant:
	ssh -p 2222 -i ~/.vagrant.d/insecure_private_key vagrant@127.0.0.1 -L 6031:localhost:6031
#######


####### SOURCE CONTROL
tag-release:
ifndef version
	$(error ERROR: version not provided.)
endif
	git tag -a v$(version) -m "version $(version)"
	git push origin --tags
#######


####### Deployment
deploy-to-vagrant-only:
	scp -P 2222 -i ~/.vagrant.d/insecure_private_key integrator/build/libs/integrator-0.0.1-SNAPSHOT.jar root@127.0.0.1:/root/source/abi-host/

deploy-to-vagrant: build-server deploy-to-vagrant-only

deploy-to-ashwini-prod: build-server
	scp integrator/build/libs/integrator-0.0.1-SNAPSHOT.jar dspace-auto:/tmp/
	ssh dspace-auto "scp /tmp/integrator-0.0.1-SNAPSHOT.jar ashwini:/root/source/abi-host/"
#######


####### DATABASE ENVIRONMENT
download-ashwini-backup:
	ssh dspace-auto "scp ashwini:/root/source/abi-host/backup/backup.sql /tmp/"
	scp dspace-auto:/tmp/backup.sql /tmp/abi-backup.sql

copy-backup-to-vagrant:
	scp -P 2222 -i ~/.vagrant.d/insecure_private_key /tmp/abi-backup.sql root@127.0.0.1:/tmp/
#######
