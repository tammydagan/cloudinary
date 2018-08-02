# Cloudinary Backend Developer Assignment

In this assignment I was asked to write a small servlet with a single endpoint: 
GET /thumbnail?url=<url>&width=<width>&height=<height>
The service fetches the image located at the given url and return a resized version in JPEG format.


## Prerequisites
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 
See deployment for notes on how to deploy the project on a live system.

JDK (Java 8, 8u181) -
Download - http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
Install - https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

Maven -
Download - https://maven.apache.org/download.cgi
Follow installation instructions - https://maven.apache.org/install.html

Make sure you set “JAVA_HOME” environment variable to JDK root directory add the JDK and maven bins directories to the “PATH” environment variable.

Git - 
Download Git - https://git-scm.com/downloads
Follow instructions of installing - https://www.linode.com/docs/development/version-control/how-to-install-git-on-linux-mac-and-windows/#install-git
Initial configuration - https://www.linode.com/docs/development/version-control/how-to-configure-git/

This tutorial assumes you have a GitHub account with public keys configured. If not, you can use the guidelines here to do so, under "Tell Git who you are" and "Give Github you public keys" - http://dont-be-afraid-to-commit.readthedocs.io/en/latest/git/commandlinegit.html


### Getting Started
Create a directory to contain the project.
Go into the new directory.
In your command line ,clone the repository : git clone https://github.com/tammydagan/cloudinary

### Compile and run 
Option one: build locally
  Build the project:  without tests, hit mvn clean install -DskipTests, With tests: mvn clean install
  This will create a snapshot of the application under cloudinary-0.0.1-SNAPSHOT.jar
Option two: use existing snapshot in target/cloudinary-0.0.1-SNAPSHOT.jar

To run the application: java -jar target/cloudinary-0.0.1-SNAPSHOT.jar. This 
Now, in order to test the application locally, test it against http://localhost:8080/ .

### Tests
You can run only the tests by using 'mvn test' command.
