CREATE TABLE persons (
     person_id MEDIUMINT NOT NULL AUTO_INCREMENT,
     first_name varchar(100) NOT NULL,
     last_name varchar(100) NOT NULL,
     PRIMARY KEY (person_id)
 );
 
 CREATE TABLE person_addresses (
 	 email_address varchar(100),
     person_id MEDIUMINT NOT NULL,
     city varchar(100) NOT NULL,
     country varchar(3) NOT NULL,
     PRIMARY KEY (email_address)
 );
 
 INSERT INTO PERSONS VALUES (1001,'Tom','Cat');
 INSERT INTO PERSONS VALUES (1002,'Jerry','Mouse');
 INSERT INTO PERSONS VALUES (1003,'Mickey','Mouse');
 INSERT INTO PERSONS VALUES (1004,'Donald','Duck');
 
 INSERT INTO PERSON_ADDRESSES VALUES ('tom.cat@example.com',1001,'Palo Alto','US');
 INSERT INTO PERSON_ADDRESSES VALUES ('jerry@example.com',1002,'Palo Alto','US');
 INSERT INTO PERSON_ADDRESSES VALUES ('jerry.mouse@example.com',1002,'Palo Alto','US');
  