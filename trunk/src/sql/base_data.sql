insert into tasks (command,                                        description,                                      enabled, beanName,     name,               groupName, priority,timerSchedule,  version, errorIfRunning, stopOnError, notifyOnSuccess) 
          values ('clear_log_files 30','Cleans up Citrine log files older than N days.
By default N is 30, this can be set as the first argument to the job.', true,    'adminJob', 'Log File Cleaner', 'admin',   0,        '0 0 3 * * ?', 0,       true,           true,        false);

insert into tasks (command,          description,                                            enabled, beanName,     name,               groupName, priority,timerSchedule,  version, errorIfRunning, stopOnError, notifyOnSuccess) 
          values ('clear_task_runs 30', 'Cleans up Task Runs in the database older than N days.
By default N is 30, this can be set as the first argument to the job.', true,    'adminJob', 'Task Run Cleaner', 'admin',   0,        '0 0 3 * * ?', 0,       true,           true,        false); 
 
