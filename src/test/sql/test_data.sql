-- first clean up
delete from task_runs;
delete from tasks;
delete from task_child_task;

-- INSERTING into tasks
insert into tasks (command,                                        description,                                      enabled, beanName,     name,               groupName, priority,timerSchedule,  version, errorIfRunning, stopOnError, notifyOnSuccess) 
          values ('sh webapps/citrine/WEB-INF/bin/cleanup-logs.sh','Cleans up Citrine log files older than N days.
By default N is 30, this can be set as the first argument to the cleanup-logs script.', true,    'sysExecJob', 'Log File Cleaner', 'admin',   0,        '0 0 3 * * ?', 0,       true,           true,        false); 

insert into tasks (command,          description,                                            enabled, beanName,     name,               groupName, priority,timerSchedule,  version, errorIfRunning, stopOnError, notifyOnSuccess) 
          values ('clear_task_runs 30', 'Cleans up Task Runs in the database older than N days.
By default N is 30, this can be set as the first argument to the job.', true,    'adminJob', 'Task Run Cleaner', 'admin',   0,        '0 0 3 * * ?', 0,       true,           true,        false); 

Insert into tasks (id,command,description,enabled,beanName,name,groupName,priority,timerSchedule,version) values (1,'ssh localhost ls','description',true,'sysExecJob','enabledtask','testGroup',5,'0 1 * * * ?',0);
Insert into tasks (id,command,description,enabled,beanName,name,groupName,priority,timerSchedule,version) values (2,'ssh localhost ls','description2',false,'sysExecJob','disabledtask','testGroup',15,'0 * * * * ?',0);
Insert into tasks (id,command,description,enabled,beanName,name,groupName,priority,timerSchedule,version) values (3,'ssh localhost ls','taskWithRuns',false,'sysExecJob','taskWithRuns','someGroup',15,'0 * * * * ?',0);

-- tasks which have parent/child relationship
INSERT INTO `tasks` (id, beanName, command, description, enabled, groupName, name, recipients,priority,timerSchedule, version)  VALUES (26,'beanName26','command26','description26','false','childTestGroup','name26',NULL,26,'',1),(27,'beanName27','command27','description27','false','childTestGroup','name27',NULL,27,'',1),(28,'beanName28','command28','description28','false','childTestGroup','name28',NULL,28,'',1),(29,'beanName29','command29','description29','false','childTestGroup','name29',NULL,29,'',1);
INSERT INTO `task_child_task` VALUES (26,27),(26,28),(27,29),(28,29);

-- parent/child tasks which use the wait bean
INSERT INTO `tasks` (id, beanName, command, description, enabled, groupName, name, recipients,priority,timerSchedule, version) VALUES (89,'waitJob','3000','task3','true','test','task3','',0,'',3);
INSERT INTO `tasks` (id, beanName, command, description, enabled, groupName, name, recipients,priority,timerSchedule, version) VALUES (88,'waitJob','30000','task2','true','test','task2','',0,'',6);
INSERT INTO `tasks` (id, beanName, command, description, enabled, groupName, name, recipients,priority,timerSchedule, version) VALUES (87,'waitJob','1000','task1','true','test','task1','',0,'',4);
INSERT INTO `tasks` (id, beanName, command, description, enabled, groupName, name, recipients,priority,timerSchedule, version) VALUES (90,'waitJob','1000','task4','true','test','task4','',0,'',0);
INSERT INTO `task_child_task` VALUES (87,89);
INSERT INTO `task_child_task` VALUES (87,88);
INSERT INTO `task_child_task` VALUES (88,90);
INSERT INTO `task_child_task` VALUES (89,90);

-- INSERTING into task_runs
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (12,'2008-01-09 10:52:02.0',1,'2008-01-09 10:52:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (13,'2008-01-09 10:52:02.0',1,'2008-01-09 10:52:02.0','FAILED','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (14,'2008-01-09 10:52:02.0',2,'2008-01-09 10:52:02.0','CANCELLED','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (15,'2008-01-09 10:52:02.0',2,'2008-01-09 10:52:02.0','INITIALISING','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (16,'2008-01-09 10:52:02.0',2,'2008-01-09 10:52:02.0','INTERRUPTED','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (17,'2008-01-09 10:52:02.0',2,'2008-01-09 10:52:02.0','RUNNING','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (18,'2008-01-09 10:52:02.0',2,'2008-01-09 10:52:02.0','ABORTED','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (19,'2008-01-09 10:52:02.0',2,'2008-01-09 10:52:02.0','FAILED','err','out',0);

Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (20,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (21,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (22,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (23,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (24,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (25,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (26,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (27,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (28,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (29,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (30,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (31,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
Insert into task_runs (id,endDate,taskId,startDate,status,sysErr,sysOut,version) values (32,'2008-06-09 10:52:02.0',3,'2008-06-09 10:53:02.0','SUCCESS','err','out',0);
