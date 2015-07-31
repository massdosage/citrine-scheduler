

Citrine is configured via the use of a properties file called **citrine.properties** that needs to be located somewhere on Tomcat's classpath (e.g. $TOMCAT\_HOME/lib/citrine.properties). This contains a number of configuration options which are discussed in more detail below by section. An example of the latest properties file is kept in Subversion here: http://code.google.com/p/citrine-scheduler/source/browse/trunk/src/conf/citrine.properties.example.

# Hibernate Database Connection Settings #
Refer to http://www.hibernate.org/ for more information on these settings. Replace USERNAME and PASSWORD as appropriate.
```
##HIBERNATE DATABASE CONNECTION SETTINGS
hibernate.connection.driver_class=com.mysql.jdbc.Driver
hibernate.dialect=org.hibernate.dialect.MySQLDialect
hibernate.connection.url=jdbc:mysql://127.0.0.1:3306/citrine?jdbcCompliantTruncation=false
hibernate.connection.username=USERNAME
hibernate.connection.password=PASSWORD
hibernate.show_sql=false
#hibernate.hbm2ddl.auto=auto
```

# Commons DBCP #
Refer to http://commons.apache.org/dbcp/ for more information.
```
##COMMONS DBCP
#Maximum number of checked-out database connections
hibernate.dbcp.maxActive=8
#Maximum number of idle database connections for connection pool
hibernate.dbcp.maxIdle=8
#Maximum idle time for connections in connection pool (expressed in ms).Set to -1 to turn off
hibernate.dbcp.maxWait=-1
#Validate connection when borrowing connection from pool (defaults to true)
hibernate.dbcp.testOnBorrow=true 
#Validate connection when returning connection to pool (optional, true, or false)
hibernate.dbcp.testOnReturn=true
#Query to execute for connection validation (optional, requires either hibernate.dbcp.testOn Borrow or hibernate.dbcp.testOnReturn)
hibernate.dbcp.validationQuery=SELECT 1+1
```

# Mail Settings #
These properties must be set in order to use e-mail notifications for tasks. A SMTP mail server is obviously a prerequisite.
```
##MAIL SETTINGS
#hostname or IP address of the mail server
smtp.host=smtp.yourhost.com
#password needed to log onto the server to send email
smtp.password=
#email sender for all email notifications
mail.from=noreplay@yourhost.com
#default address to which job notifications will get sent if no value is set on the job itself
mail.to=default@yourhost.com
#base url to citrine web interface. Optional. Only needs to be set if you want full paths to log files in notification e-mails.
#should be set to something like http://yourhost:port/citrine/
base.citrine.url=http://yourhost.com:8080/citrine/
```

# Quartz Scheduler Settings #
Citrine uses [Quartz](http://www.quartz-scheduler.org) to perform the scheduling of tasks. Quartz is highly configurable and its configurations properties may be added to directly to citrine.properties. A full reference of Quartz's properties is here: http://www.quartz-scheduler.org/docs/configuration/
```
###QUARTZ SCHEDULER SETTINGS
org.quartz.scheduler.instanceName = DefaultQuartzScheduler
org.quartz.scheduler.rmi.export = false
org.quartz.scheduler.rmi.proxy = false
org.quartz.scheduler.wrapJobExecutionInUserTransaction = false
org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount = 100
org.quartz.threadPool.threadPriority = 5
org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread = true
org.quartz.jobStore.misfireThreshold = 60000
org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore
```

# SysExec Settings #
Most Citrine tasks involve firing off a command line call (e.g. to a script), the handling of which is done by Citrine's "SysExecBean". This logs its output to files which can be viewed in Citrine. The following properties are available to configure various aspects of the logging.
```
##SYSEXEC SETTINGS
#This MUST be the FULL_PATH_TO/TOMCAT_HOME/webapps/citrine/logs/
sysexec.logpath=/user/local/tomcat/webapps/citrine/logs/
#The pattern to use for logging. This uses the log4j format. For more information, see http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html.
sysexec.logpattern=%d{ISO8601} %m%n
#Controls how many bytes of the log are displayed when the log is viewed in HTML view.
sysexec.tailbytes=5000
```