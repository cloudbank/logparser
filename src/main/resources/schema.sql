
		CREATE TABLE IF NOT EXISTS `logentry`  (
    		id MEDIUMINT NOT NULL AUTO_INCREMENT,
    		logtime DATETIME(3),
    		ip VARCHAR(20),
    		request VARCHAR(20),
    		code VARCHAR(20),
    		agent VARCHAR(255),
    		PRIMARY KEY (id),
    		UNIQUE unique_index(logtime,ip)
		);
		
				
		CREATE TABLE IF NOT EXISTS `blocked`  (
  		    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    		logtime DATETIME(3),
    		ip VARCHAR(20),
    		blockComment VARCHAR(255),
    		PRIMARY KEY (id),
    		UNIQUE unique_index(logtime,ip,blockComment)
		);

		SET GLOBAL local_infile = 1 ;

	    LOAD DATA LOCAL INFILE '/Users/sgv/Downloads/logparse-2/src/main/resources/access.log' IGNORE INTO TABLE logentry FIELDS TERMINATED BY '|' LINES TERMINATED BY '\n' (logtime,ip,request,code,agent) 
  
  
  		