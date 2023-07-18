restore_staging_dump:
ifndef dumpFile
	@echo "Provde the dumpFile variable"
	exit 1
else
	$(call _drop_db,avni_int_staging)
	$(call _build_db,avni_int_staging)
	psql -U avni_int -d avni_int_staging < $(dumpFile)
endif

restore_staging_dump_release_branch:
ifndef dumpFile
	@echo "Provde the dumpFile variable"
	exit 1
else
	$(call _drop_db,avni_int_staging_released)
	$(call _build_db,avni_int_staging_released)
	psql -U avni_int -d avni_int_staging_released < $(dumpFile)
endif
