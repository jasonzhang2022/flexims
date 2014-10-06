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
* module: module to manage module download. 
* report: query and report. Both backend code and front ui code.
* s3files: store file in Amazon aws s3.
* security: instance-level security
* servicetest: end to end test for service
* testorm: man predefined type for test purpose
* tomcat_web: deployment the whole application under tomcat
* urlctrl: control access by url filtering
* users: internal user management module



