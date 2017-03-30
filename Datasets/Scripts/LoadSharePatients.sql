CREATE TABLE RD2GD.medicare_share_patients (
  `NPI1` char(10) NOT NULL DEFAULT '',
  `NPI2` char(10) NOT NULL DEFAULT '',
  `SHARED_TRANSACTION_COUNT` int(11) DEFAULT NULL COMMENT 'Shared Transaction Count Between Providers',
  `PATIENT_TOTAL` int(11) DEFAULT NULL COMMENT 'Total Number of Individual Patients Shared',
  `SAME_DAY_TOTAL` int(11) DEFAULT NULL COMMENT 'Number of Patients Shared Same Day',
  PRIMARY KEY (`NPI1`,`NPI2`),
  KEY `idx_docgraph_npi1` (`NPI1`),
  KEY `idx_docgraph_npi2` (`NPI2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOAD DATA LOCAL INFILE 'location_of_file/physician-shared-patient-patterns-2015-days180.txt'
into table RD2GD.medicare_share_patients
fields escaped by '{' enclosed by '"' terminated by ','
lines terminated by '\n'
