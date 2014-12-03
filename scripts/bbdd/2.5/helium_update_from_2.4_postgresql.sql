﻿-- Tipus de dada dels camps tipus textarea
ALTER TABLE JBPM_VARIABLEINSTANCE ADD AUX_CLOB TEXT;
UPDATE JBPM_VARIABLEINSTANCE SET AUX_CLOB = STRINGVALUE_;
--ALTER TABLE JBPM_VARIABLEINSTANCE DROP COLUMN STRINGVALUE_;
ALTER TABLE JBPM_VARIABLEINSTANCE RENAME COLUMN STRINGVALUE_ TO STRINGVALUE_OLD;
ALTER TABLE JBPM_VARIABLEINSTANCE RENAME COLUMN AUX_CLOB TO STRINGVALUE_;

-- Select de consultes per tipus
ALTER TABLE HEL_CAMP ADD CONSULTA_ID BIGINT;
ALTER TABLE HEL_CAMP ADD CONSULTA_CAMP_TEXT VARCHAR(64);
ALTER TABLE HEL_CAMP ADD CONSULTA_CAMP_VALOR VARCHAR(64);
ALTER TABLE HEL_CAMP ADD CONSULTA_PARAMS VARCHAR(255);

-- Millora missatges errors integracions
ALTER TABLE HEL_PORTASIGNATURES ADD PROCESS_INSTANCE_ID VARCHAR(255);
ALTER TABLE HEL_PORTASIGNATURES ADD EXPEDIENT_ID BIGINT;
ALTER TABLE HEL_PORTASIGNATURES ADD CONSTRAINT HEL_EXPEDIENT_PSIGNA_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID);
ALTER TABLE HEL_EXPEDIENT ADD ERRORS_INTEGS BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE HEL_EXPEDIENT ADD COMENTARIANULAT VARCHAR(255);
ALTER TABLE HEL_EXPEDIENT_LOG ADD INI_RETROCES BIGINT;

-- Nou camp pel domini intern --
ALTER TABLE HEL_CAMP ADD DOMINI_INTERN BOOLEAN DEFAULT FALSE NOT NULL;

-- Nou camp per les consultes. Defineix el tipus de format er exportar un report amb JasperReports
ALTER TABLE HEL_CONSULTA ADD FORMAT_EXPORTACIO VARCHAR(4) DEFAULT 'PDF';

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
    NEXTVAL('HIBERNATE_SEQUENCE') ID,
    '2.5.0' CODI,
    250 ORDRE,
    'now' DATA_CREACIO,
    false PROCES_EXECUTAT,
    true SCRIPT_EXECUTAT,
    'now' DATA_EXECUCIO_SCRIPT
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 250) = 0;

-- La Constraint unica de les enumeracions inclou expedient_tipus_id --
ALTER TABLE HEL_ENUMERACIO DROP CONSTRAINT hel_enumeracio_codi_key;

ALTER TABLE HEL_ENUMERACIO ADD CONSTRAINT hel_enumeracio_codi_key UNIQUE (CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID);
