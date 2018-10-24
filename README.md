
LogParser v 1.0.0

A Spring Boot POC for parsing log files and finding multiple requests.
Although the project could be implemented in many ways, I chose Spring Boot purely as an example as it is used widely in Java based projects.
I did not implement any ORM so that it would be as light as possible.  Using the spring-boot-starter-jdbc module allows one to inject a JDBCTemplate and a Repository easily.


The application takes command line args:
--startDate=   --duration=   --threshold=
as a date String, "daily or "hourly", and an integer for the max number of requests, respectively.
    
    --startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200

The jar file built with maven (in /target) is called as such:

     java -jar logparser-1.0.0.jar --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 


Summary
------- 
On startup the access.log file is inserted into a MySQL table `logentry` efficiently with the LOAD DATA LOCAL INFILE command, there is a print out of the results of the query, and a batch insert of the results into another table `blocked`. 
The LOAD DATA LOCAL INFILE command benchmarked as fastest on file based inserts versus both Spring Batch and JDBC batch update, without any intermediate/temporary table creation or configuration overhead.  JDBCTemplate.batchUpdate with raw SQL performed 8 times better than batch insert with a stored procedure.
A stored procedure was chosen for the query because it is considered a best practice. They are pre-compiled,fast,secure, extensible, and reduce network traffic. 

Tables
=======

* logentry
* blocked

StoredProcedures
================

* getLogEntries


A single example test was added as an example in the /test directory

* LogParserTest


	

Issues
-------
*  I did not use package, although I would in production, for stored procedures.

* The id for both tables is auto incremented and will have gaps when INSERT IGNORE does not include a duplicate row but updates the id. innodb_autoinc_lock_mode = 0 would solve this but would incur a performance hit, so I did not include it. I could also have written different insert SQL to avoid the gaps but felt it would be a hack to use the well known workaround.  This could be solved in any case, or ids could have gaps as they do when transactions roll back on failed inserts.

* Caching or 'canning' large queries could also be employed for faster lookups.



--