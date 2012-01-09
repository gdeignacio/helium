-- Consultes avançades --
ALTER TABLE HEL_CONSULTA
 ADD (INFORME_NOM VARCHAR2(255 CHAR));
ALTER TABLE HEL_CONSULTA
 ADD (INFORME_CONTINGUT BLOB);
ALTER TABLE HEL_CONSULTA
 ADD (EXPORTAR_ACTIU NUMBER(1) DEFAULT 1);
UPDATE HEL_CONSULTA SET EXPORTAR_ACTIU = 1;

CREATE TABLE HEL_CONSULTA_SUB (
  PARE_ID  NUMBER(19) NOT NULL,
  FILL_ID  NUMBER(19) NOT NULL);
ALTER TABLE HEL_CONSULTA_SUB ADD (
  PRIMARY KEY (PARE_ID, FILL_ID));
ALTER TABLE HEL_CONSULTA_SUB ADD (
  CONSTRAINT HEL_PARE_CONSULTASUB_FK 
 FOREIGN KEY (PARE_ID) 
 REFERENCES HEL_CONSULTA (ID),
  CONSTRAINT HEL_FILL_CONSULTASUB_FK 
 FOREIGN KEY (FILL_ID) 
 REFERENCES HEL_CONSULTA (ID));

ALTER TABLE HEL_CONSULTA
 ADD (VALORS_PREDEF        VARCHAR2(1024 CHAR));

-- Actualització a la nova versió --
INSERT INTO HEL_VERSIO (
    ID,
    CODI,
    ORDRE,
    DATA_CREACIO,
    PROCES_EXECUTAT,
    SCRIPT_EXECUTAT,
    DATA_EXECUCIO_SCRIPT)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL ID,
    '2.2.1' CODI,
    221 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 221) = 0;
