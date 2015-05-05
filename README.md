flexims: Angular Frontend for JPA(Java Persistence API).
=======

A system managing **Structure data** such as data in RDBMS or data in Excel.

Find more information on http://www.flexdms.com.

License
===
Apache

Folder Structure
===
Top folder is a maven project. Each folder is child maven/eclipse project
* auth: authentication module
* common: core module. Referenced by all modules.
* dbfiles: store file upload in database
* googlefiles: store file in google cloud or google drive
* ittest: integration test
* js: angularjs front end.
* module: manage type export 
* report: query and report. Both backend code and front ui code.
* s3files: store file in Amazon aws s3.
* security: instance-level security
* servicetest: end to end test for service
* testorm: predefined types for test purpose
* tomcat_web: deploy the whole application under tomcat
* urlctrl: control access by url filtering
* users: internal user management module

Build
==
Requirement
====
* Apache maven
* nodejs
* bower: npm install -g bower
* install https://github.com/jasonzhang2022/htmltemplate-maven-plugin
  *	git clone https://github.com/jasonzhang2022/htmltemplate-maven-plugin;
  *	cd htmltemplate-maven-plugin
  * mvn install

Compile and install
====
* mvn --non-recursive install 
* mvn -DskipTests install
* mvn test;

