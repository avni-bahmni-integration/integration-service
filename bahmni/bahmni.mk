####### Deployment
deploy-all-to-ashwini-prod: deploy-integrator-to-ashwini-prod deploy-migrator-to-ashwini-prod
	$(call _alert_success)

deploy-integrator-to-ashwini-prod: build-server
	scp integrator/build/libs/$(application_jar) dspace-auto:/tmp/
	ssh dspace-auto "scp /tmp/$(application_jar) ashwini:/root/source/abi-host/"

deploy-migrator-to-ashwini-prod: build-server
	scp metadata-migrator/build/libs/metadata-migrator-0.0.2-SNAPSHOT.jar dspace-auto:/tmp/
	ssh dspace-auto "scp /tmp/metadata-migrator-0.0.2-SNAPSHOT.jar ashwini:/root/source/abi-host/"

# SERVICE MANAGEMENT
restart-ashwini-service:
	ssh dspace-auto "ssh ashwini \"systemctl restart abi.service\""

tail-ashwini-service:
	ssh dspace-auto "ssh ashwini \"tail -f /var/log/abi/integration-service.log\""

####### DATABASE ENVIRONMENT
download-ashwini-backup:
	ssh dspace-auto "scp ashwini:/root/source/abi-host/backup/backup.sql /tmp/"
	scp dspace-auto:/tmp/backup.sql /tmp/abi-backup.sql

copy-backup-to-vagrant:
	scp -P 2222 -i ~/.vagrant.d/insecure_private_key /tmp/abi-backup.sql root@127.0.0.1:/tmp/
