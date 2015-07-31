

# Installing Citrine #
These instructions cover installing Citrine for production use from a Binary Release downloaded from [here](http://code.google.com/p/citrine-scheduler/downloads/list). Unpack the .tgz file which should contain the following files:
  * citrine.war - the Citrine web application.
  * src/main/sql/citrine-ddl-mysql.sql - SQL DDL for creating the Citrine database.
  * src/main/sql/base\_data.sql - SQL which loads various default values into Citrine.
  * src/main/conf/citrine.properties.example - Example Citrine configuration file.
The steps below will describe how to use these files to get up and running with Citrine.

## Prerequisites ##
  1. Java 6.x installed.
  1. Tomcat 6.x or 7.x installed to $TOMCAT\_HOME.
  1. MySql 5.x installed.

## Setting up the Citrine database ##
The steps below are for MySql (currently the only supported DB).
  1. Create a database, this example will use the name "citrine" but you can call it whatever you want.
```
$ mysql -u USERNAME
mysql> create database citrine;
mysql> exit;
```
  1. Load the SQL DDL file that will create the necessary tables. Change directory to the folder that the Citrene files are in.
```
$ mysql -u USERNAME citrine < citrine-ddl-mysql.sql
```
  1. Load the base SQL file that will create the default admin tasks.
```
$ mysql -u USERNAME citrine < base_data.sql
```
  1. If necessary create a user that can read and write from all the tables in the citrine database that will be used in production. We will assume the user is called "produser" and their password is "prodpassword".

## Configuring Citrine ##

  1. Copy citrine.properties.example to somewhere on Tomcat's classpath (e.g. TOMCAT\_HOME/lib/ for versions 6.02x)
  1. Rename citrine.properties.example to citrine.properties.
  1. Edit citrine.properties.
  1. Set the property "hibernate.connection.url" and set the host, port and database name that you added above.
```
jdbc:mysql://127.0.0.1:3306/citrine?jdbcCompliantTruncation=false
```
  1. Set the property "hibernate.connection.username" to the username you added above.
  1. Set the property "hibernate.connection.password" to the password you added above.
  1. Set the property "sysexec.logpath" to TOMCAT\_HOME/webapps/citrine/logs (replace TOMCAT\_HOME with the **full** path to wherever you installed Tomcat).

Above is the bare minimum configuration changes needed to get up and running with Citrine. For more information see [Configuration](Configuration.md).

## Starting Citrine ##
  1. Start Tomcat.
  1. Copy citrine.war to TOMCAT\_HOME/webapps
  1. Go to http://localhost:8080/citrine/ in a browser and you should see the Citrine home page.

## Troubleshooting ##
The first place to look for errors is in the log files under TOMCAT\_HOME/logs/. Usually errors will be in a file called catalina.out, but depending on where in the process things went wrong various other files in the logs folder might contain clues.
  * If the logs contain a stack trace complaining about "hibernate dialect must be explicitly set" look higher up in trace for the actual cause which 9 times out of 10 is  a database authentication issue (e.g. wrong user name or password).