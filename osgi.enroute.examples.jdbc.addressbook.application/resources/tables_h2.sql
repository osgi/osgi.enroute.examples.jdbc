ALTER TABLE PERSON_ADDRESSES DROP CONSTRAINT IF EXISTS fk_persons_person_id;

-- PERSON TABLE
DROP TABLE IF EXISTS PERSONS;

CREATE TABLE persons (
  person_id  mediumint(9) NOT NULL AUTO_INCREMENT,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  PRIMARY KEY (person_id)q
) ;

-- PERSON ADDRESS TABLE
DROP TABLE IF EXISTS person_addresses;

CREATE TABLE person_addresses (
  email_address varchar(255) NOT NULL,
  person_id  mediumint(9) NOT NULL,
  city varchar(100) NOT NULL,
  country varchar(2) NOT NULL,
  PRIMARY KEY (email_address)
) ;

CREATE INDEX IDX_PERSON_ADDRESS_EMAIL ON PERSON_ADDRESSES(email_address);
CREATE INDEX IDX_PERSON_ADDRESS_CITY ON PERSON_ADDRESSES(city);
CREATE INDEX IDX_PERSON_ADDRESS_COUNTRY ON PERSON_ADDRESSES(country);

ALTER TABLE PERSON_ADDRESSES ADD CONSTRAINT fk_persons_person_id FOREIGN KEY (person_id) REFERENCES PERSONS( PERSON_ID) ;
