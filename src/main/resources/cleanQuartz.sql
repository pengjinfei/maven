TRUNCATE table qrtz_calendars;
TRUNCATE table qrtz_fired_triggers;
TRUNCATE table qrtz_blob_triggers;
TRUNCATE table qrtz_cron_triggers;
TRUNCATE table qrtz_simple_triggers;
TRUNCATE table qrtz_simprop_triggers;
TRUNCATE table qrtz_locks;
TRUNCATE table qrtz_paused_trigger_grps;
TRUNCATE table qrtz_scheduler_state;
ALTER TABLE QRTZ_TRIGGERS DROP CONSTRAINT QRTZ_TRIGGER_TO_JOBS_FK;
TRUNCATE table qrtz_job_details;
TRUNCATE table qrtz_triggers;
ALTER TABLE QRTZ_TRIGGERS ADD CONSTRAINT  QRTZ_TRIGGER_TO_JOBS_FK FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP) ;
DELETE FROM QRTZ_TRIGGERS;
