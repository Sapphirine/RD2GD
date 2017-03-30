create table if not exists RD2GD.taxonomy(
    CODE char(10),
    TYPE varchar(100),
    CLASSIFICATION varchar(100),
    SPECIALIZATION varchar(100),
    DEFINITION TEXT,
    NOTES TEXT,
    primary key(CODE)
)
engine=InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

load data local infile 'location_of_file\nucc_taxonomy_170.csv'
into table RD2GD.taxonomy
fields escaped by '\\' enclosed by "\"" terminated by ','
lines terminated by '\r\n' ignore 1 lines;
