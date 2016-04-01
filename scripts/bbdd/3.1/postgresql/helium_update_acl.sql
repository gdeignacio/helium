﻿--DROP TABLE HEL_ACL_ENTRY;
--DROP TABLE HEL_ACL_OBJECT_IDENTITY;
--DROP TABLE HEL_ACL_SID;
--DROP TABLE HEL_ACL_CLASS;
--DROP SEQUENCE HEL_ACL_CLASS_SEQ;
--DROP SEQUENCE HEL_ACL_ENTRY_SEQ;
--DROP SEQUENCE HEL_ACL_OBJECT_IDENTITY_SEQ;
--DROP SEQUENCE HEL_ACL_SID_SEQ;

ALTER TABLE HEL_ACL_CLASS RENAME TO OLD_ACL_CLASS;
ALTER TABLE HEL_ACL_ENTRY RENAME TO OLD_ACL_ENTRY;
ALTER TABLE HEL_ACL_OBJECT_IDENTITY RENAME TO OLD_ACL_OBJECT_IDENTITY;
ALTER TABLE HEL_ACL_SID RENAME TO OLD_ACL_SID;

--------------------------------------------------------
-- ACL_CLASS Table
--------------------------------------------------------
CREATE TABLE "HEL_ACL_CLASS" (
  "ID" BIGINT NOT NULL,
  "CLASS" VARCHAR(100) NOT NULL,
  PRIMARY KEY ("ID"),
  CONSTRAINT "ACL_CLASS_CLASS_UQ" UNIQUE ("CLASS")
);
 
--------------------------------------------------------
-- ACL_ENTRY Table
--------------------------------------------------------
CREATE TABLE "HEL_ACL_ENTRY" (
  "ID" BIGINT NOT NULL,
  "ACL_OBJECT_IDENTITY" BIGINT NOT NULL,
  "ACE_ORDER" BIGINT NOT NULL,
  "SID" BIGINT NOT NULL,
  "MASK" BIGINT NOT NULL,
  "GRANTING" BOOLEAN NOT NULL,
  "AUDIT_SUCCESS" BOOLEAN NOT NULL,
  "AUDIT_FAILURE" BOOLEAN NOT NULL,
  PRIMARY KEY ("ID"),
  CONSTRAINT "HEL_ACL_ENTRY_IDENT_ORDER_UQ" UNIQUE ("ACL_OBJECT_IDENTITY", "ACE_ORDER")
);


 
--------------------------------------------------------
-- ACL_OBJECT_IDENTITY Table
--------------------------------------------------------
CREATE TABLE "HEL_ACL_OBJECT_IDENTITY" (
  "ID" BIGINT NOT NULL,
  "OBJECT_ID_CLASS" BIGINT NOT NULL,
  "OBJECT_ID_IDENTITY" BIGINT NOT NULL,
  "PARENT_OBJECT" BIGINT,
  "OWNER_SID" BIGINT NOT NULL,
  "ENTRIES_INHERITING" BOOLEAN NOT NULL,
  PRIMARY KEY ("ID"),
  CONSTRAINT "HEL_ACL_OBJ_ID_CLASS_IDENT_UQ" UNIQUE ("OBJECT_ID_CLASS", "OBJECT_ID_IDENTITY")
);

 
--------------------------------------------------------
-- ACL_SID Table
--------------------------------------------------------
CREATE TABLE "HEL_ACL_SID" (
  "ID" BIGINT NOT NULL,
  "PRINCIPAL" BOOLEAN NOT NULL,
  "SID" VARCHAR(100) NOT NULL,
  PRIMARY KEY ("ID"),
  CONSTRAINT "HEL_ACL_SID_PRINCIPAL_SID_UQ" UNIQUE ("SID", "PRINCIPAL")
);


 
--------------------------------------------------------
-- Relationships
--------------------------------------------------------
 
ALTER TABLE "HEL_ACL_ENTRY" ADD CONSTRAINT "FK_ACL_ENTRY_ACL_OBJECT_ID"
  FOREIGN KEY ("ACL_OBJECT_IDENTITY")
  REFERENCES "HEL_ACL_OBJECT_IDENTITY" ("ID");
ALTER TABLE "HEL_ACL_ENTRY" ADD CONSTRAINT "FK_ACL_ENTRY_SID"
  FOREIGN KEY ("SID")
  REFERENCES "HEL_ACL_SID" ("ID");
 
ALTER TABLE "HEL_ACL_OBJECT_IDENTITY" ADD CONSTRAINT "FK_ACL_OBJ_ID_CLASS"
  FOREIGN KEY ("OBJECT_ID_CLASS")
  REFERENCES "HEL_ACL_CLASS" ("ID");
ALTER TABLE "HEL_ACL_OBJECT_IDENTITY" ADD CONSTRAINT "FK_ACL_OBJ_ID_PARENT"
  FOREIGN KEY ("PARENT_OBJECT")
  REFERENCES "HEL_ACL_OBJECT_IDENTITY" ("ID");
ALTER TABLE "HEL_ACL_OBJECT_IDENTITY" ADD CONSTRAINT "FK_ACL_OBJ_ID_SID"
  FOREIGN KEY ("OWNER_SID")
  REFERENCES "HEL_ACL_SID" ("ID");

--------------------------------------------------------
-- Copy from old tables
--------------------------------------------------------

INSERT INTO "HEL_ACL_CLASS" ("ID", "CLASS") SELECT ID, CLASS FROM OLD_ACL_CLASS;
INSERT INTO "HEL_ACL_SID" ("ID", "PRINCIPAL", "SID") SELECT ID, PRINCIPAL, SID FROM OLD_ACL_SID;
INSERT INTO "HEL_ACL_OBJECT_IDENTITY" ("ID","OBJECT_ID_CLASS","OBJECT_ID_IDENTITY","OWNER_SID","ENTRIES_INHERITING","PARENT_OBJECT") SELECT ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,OWNER_SID,ENTRIES_INHERITING,PARENT_OBJECT FROM OLD_ACL_OBJECT_IDENTITY;
INSERT INTO "HEL_ACL_ENTRY" ("ID", "ACL_OBJECT_IDENTITY", "ACE_ORDER", "SID", "MASK", "GRANTING", "AUDIT_SUCCESS", "AUDIT_FAILURE") SELECT ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE FROM OLD_ACL_ENTRY;

--------------------------------------------------------
-- Create sequences
--------------------------------------------------------

/*
DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM HEL_ACL_CLASS;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "HEL_ACL_CLASS_SEQ" '||
    'INCREMENT BY 1 '||
	'MAXVALUE 9999999999999999999999999999 '||
    'START WITH '|| TO_CHAR(VRESULT) ||
    ' CACHE 20 '||
    'NOORDER '||
    'NOCYCLE';
  EXECUTE IMMEDIATE STMT;
END;
/

DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM HEL_ACL_ENTRY;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "HEL_ACL_ENTRY_SEQ" '||
    'INCREMENT BY 1 '||
	'MAXVALUE 9999999999999999999999999999 '||
    'START WITH '|| TO_CHAR(VRESULT) ||
    ' CACHE 20 '||
    'NOORDER '||
    'NOCYCLE';
  EXECUTE IMMEDIATE STMT;
END;
/

DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM HEL_ACL_OBJECT_IDENTITY;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "HEL_ACL_OBJECT_IDENTITY_SEQ" '||
    'INCREMENT BY 1 '||
	'MAXVALUE 9999999999999999999999999999 '||
    'START WITH '|| TO_CHAR(VRESULT) ||
    ' CACHE 20 '||
    'NOORDER '||
    'NOCYCLE';
  EXECUTE IMMEDIATE STMT;
END;
/

DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM HEL_ACL_SID;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "HEL_ACL_SID_SEQ" '||
    'INCREMENT BY 1 '||
	'MAXVALUE 9999999999999999999999999999 '||
    'START WITH '|| TO_CHAR(VRESULT) ||
    ' CACHE 20 '||
    'NOORDER '||
    'NOCYCLE';
  EXECUTE IMMEDIATE STMT;
END;
/

*/

--------------------------------------------------------
-- Triggers
--------------------------------------------------------

/*

CREATE OR REPLACE TRIGGER "HEL_ACL_CLASS_ID"
BEFORE INSERT ON HEL_ACL_CLASS
FOR EACH ROW
  BEGIN
    SELECT HEL_ACL_CLASS_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "HEL_ACL_ENTRY_ID"
BEFORE INSERT ON HEL_ACL_ENTRY
FOR EACH ROW
  BEGIN
    SELECT HEL_ACL_ENTRY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "HEL_ACL_OBJECT_IDENTITY_ID"
BEFORE INSERT ON HEL_ACL_OBJECT_IDENTITY
FOR EACH ROW
  BEGIN
    SELECT HEL_ACL_OBJECT_IDENTITY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "HEL_ACL_SID_ID"
BEFORE INSERT ON HEL_ACL_SID
FOR EACH ROW
  BEGIN
    SELECT HEL_ACL_SID_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
*/

--------------------------------------------------------
-- Permesos
--------------------------------------------------------

-- CREATE PUBLIC SYNONYM HEL_ACL_CLASS FOR HEL_ACL_CLASS;
-- CREATE PUBLIC SYNONYM HEL_ACL_ENTRY FOR HEL_ACL_ENTRY;
-- CREATE PUBLIC SYNONYM HEL_ACL_OBJECT_IDENTITY FOR HEL_ACL_OBJECT_IDENTITY;
-- CREATE PUBLIC SYNONYM HEL_ACL_SID FOR HEL_ACL_SID;

/*

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACL_CLASS TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACL_ENTRY TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACL_OBJECT_IDENTITY TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACL_SID TO WWW_HELIUM;

GRANT SELECT ON HEL_ACL_CLASS_SEQ TO WWW_HELIUM;
GRANT SELECT ON HEL_ACL_ENTRY_SEQ TO WWW_HELIUM;
GRANT SELECT ON HEL_ACL_OBJECT_IDENTITY_SEQ TO WWW_HELIUM;
GRANT SELECT ON HEL_ACL_SID_SEQ TO WWW_HELIUM;

*/