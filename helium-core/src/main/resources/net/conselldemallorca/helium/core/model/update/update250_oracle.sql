-- Tipus de dada dels camps tipus textarea
ALTER TABLE JBPM_VARIABLEINSTANCE ADD (AUX_CLOB CLOB);
UPDATE JBPM_VARIABLEINSTANCE SET AUX_CLOB = STRINGVALUE_;
--ALTER TABLE JBPM_VARIABLEINSTANCE DROP COLUMN STRINGVALUE_;
ALTER TABLE JBPM_VARIABLEINSTANCE RENAME COLUMN STRINGVALUE_ TO STRINGVALUE_OLD;
ALTER TABLE JBPM_VARIABLEINSTANCE RENAME COLUMN AUX_CLOB TO STRINGVALUE_;

-- Select de consultes per tipus
ALTER TABLE HEL_CAMP ADD CONSULTA_ID NUMBER(19);
ALTER TABLE HEL_CAMP ADD CONSULTA_CAMP_TEXT VARCHAR2(64 CHAR);
ALTER TABLE HEL_CAMP ADD CONSULTA_CAMP_VALOR VARCHAR2(64 CHAR);
ALTER TABLE HEL_CAMP ADD CONSULTA_PARAMS VARCHAR2(255 CHAR);

-- Millora missatges errors integracions
ALTER TABLE HEL_PORTASIGNATURES ADD PROCESS_INSTANCE_ID VARCHAR2(255);
ALTER TABLE HEL_PORTASIGNATURES ADD EXPEDIENT_ID NUMBER(19);
ALTER TABLE HEL_PORTASIGNATURES ADD CONSTRAINT HEL_EXPEDIENT_PSIGNA_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID);
ALTER TABLE HEL_EXPEDIENT ADD ERRORS_INTEGS NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_EXPEDIENT ADD COMENTARIANULAT VARCHAR2(255 CHAR);
ALTER TABLE HEL_EXPEDIENT_LOG ADD INI_RETROCES NUMBER(19);

-- Nou camp pel domini intern --
ALTER TABLE HEL_CAMP ADD DOMINI_INTERN NUMBER(1) NOT NULL DEFAULT 0;

-- Nou camp per les consultes. Defineix el tipus de format er exportar un report amb JasperReports
ALTER TABLE HEL_CONSULTA ADD FORMAT_EXPORTACIO VARCHAR2(4 CHAR) DEFAULT 'PDF';

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
    '2.5.0' CODI,
    250 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 250) = 0;

-- La Constraint unica de les enumeracions inclou expedient_tipus_id --
ALTER TABLE HEL_ENUMERACIO DROP CONSTRAINT SYS_C0028409;

ALTER TABLE HEL_ENUMERACIO ADD (
  CONSTRAINT SYS_C0028409 UNIQUE (CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID));
