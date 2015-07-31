

# Building Citrine From Source #

This page describes the steps needed to get Citrine set up for development and covers checking out the code and building it from source. Once you get Citrine to build successfully the next step is to get onto the UnitTestSetup.

## Obtaining the source ##
You have two options for obtaining the source code for Citrine, either checking it out from Subversion or by downloading a source release.

### Checkout the latest code from Subversion ###
If you want to modify the source code based on the current trunk version you should follow the instructions on the [source](http://code.google.com/p/citrine-scheduler/source/checkout) page. If you do not have Subversion commit rights you can still check out the code and create and submit a patch with your changes.

### Download a source release ###
Go to the Citrine [download page](http://code.google.com/p/citrine-scheduler/downloads/list) and select the release labelled "Source Release" that you are interested in. This contains all the source code as well as required third party jar files etc.

## Install Maven ##
Citrine is built using [Maven](http://maven.apache.org/) so you will need this set up and installed.

## Building from source ##
You should then be able to run
```
mvn package
```
to compile all the classes and build the Citrine.war file ready for deployment.