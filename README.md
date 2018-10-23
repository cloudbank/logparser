
LogParser v 1.0.0

A Spring Boot POC for parsing log files and finding multiple requests.
Although the project could be implemented in many ways, I chose Spring not as a requirement but
as an example as is used in many Java based projects.
I did not use any ORM or JPA layers so that it would be as light as possible.


The application takes command line args:
--startDate=   --duration=   --threshold=
as a date String, "daily or "hourly", and an integer for the max number of requests, respectively.
    
    --startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200

The jar file built with maven (in /target) is called as such:

     java -jar logparser-1.0.0.jar --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 


 
On startup the access.log file is inserted into a MySQL table `logentry` efficiently and there is a print out of the results of the query given by the command line args. The results are also inserted into another table `blocked`. 
I used the LOAD DATA LOCAL INFILE SQL command for the logfile insert which benchmarked as fastest on file based inserts versus both Spring Batch and JDBCTemplate batch update.

Tables
=======

* logentry
* blocked

StoredProcedures
================

* getLogEntries
* insertBlockedEntries

Stored procedures were chosen because they are considered a best practice. They are precompiled, secure, extensible, and reduce network traffic.

	

Issues
-------
*  I did not use package, although I would in production, for stored procedures.

* The id for both tables is auto incremented and will have gaps when INSERT IGNORE does not include a duplicate row but updates the id. innodb_autoinc_lock_mode = 0 would solve this but also would incur a performance hit so I did not include it. I could also have written different insert SQL but felt it would be a hack to use the well known workaround.  This could be solved in any case, or ids could have gaps as they do when transactions roll back on failed inserts.

* Caching or 'canning' queries could also be employed for faster lookups.

--