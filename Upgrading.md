

For information on Installing Citrine from scratch see [Installation](Installation.md).

# Upgrading from 4.1.0 to 4.2.0 #
  1. There are no extra steps required other than replacing the war file.

# Upgrading from 4.0.x to 4.1.0 #
  1. Edit the **citrine.properties** configuration file and add the Quartz properties described in the [Configuration](Configuration.md) wiki page.
  1. Shut Citrine down (see [Usage#Safely\_shutdown\_Citrine](Usage#Safely_shutdown_Citrine.md)).
  1. Shut Tomcat down.
  1. Replace TOMCAT\_HOME/webapps/citrine.war with the new citrine.war file.
  1. Restart Tomcat