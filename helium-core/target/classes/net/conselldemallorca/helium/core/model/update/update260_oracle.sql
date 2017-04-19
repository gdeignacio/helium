-- Taules per a execució massiva
CREATE TABLE HEL_EXEC_MASSIVA (
        ID NUMBER(19) NOT NULL,
        DATA_INICI DATE NOT NULL,
        DATA_FI DATE,
        PARAM1 VARCHAR2(255),
        TIPUS NUMBER(10) NOT NULL,
        USUARI VARCHAR2(64) NOT NULL,
        EXPEDIENT_TIPUS_ID NUMBER(19),
        ENV_CORREU NUMBER(1),
        PARAM2 BLOB,
        ENTORN NUMBER(19),
        PRIMARY KEY (ID),
        CONSTRAINT HEL_EXPTIPUS_EXEMAS_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);
    
CREATE TABLE HEL_EXEC_MASEXP (
        ID NUMBER(19) NOT NULL,
        DATA_INICI DATE,
        DATA_FI DATE,
        ORDRE NUMBER(10) NOT NULL,
        EXECMAS_ID NUMBER(19) NOT NULL,
        EXPEDIENT_ID NUMBER(19),
        ESTAT NUMBER(10),
        ERROR CLOB,
        TASCA_ID VARCHAR2(255),
        PROCINST_ID VARCHAR2(255),
        PRIMARY KEY (ID),
        CONSTRAINT HEL_EXPEDIENT_EXEMASEX_FK FOREIGN KEY (EXPEDIENT_ID) REFERENCES HEL_EXPEDIENT (ID),
        CONSTRAINT HEL_EXECMAS_EXEMASEX_FK FOREIGN KEY (EXECMAS_ID) REFERENCES HEL_EXEC_MASSIVA (ID)
);

--Nous indexos pel millorar el rendiment a la consulta dels camps de la definició de procés
CREATE INDEX HEL_CAMP_CODI_TIP ON HEL_CAMP (CODI, TIPUS);
CREATE INDEX HEL_CAMP_COD_TIP_DP ON HEL_CAMP (CODI, TIPUS, DEFINICIO_PROCES_ID);

-- Per a permetre triar l'any en la generació del número d'expedient
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD SELECCIONAR_ANY NUMBER(1) DEFAULT 0 NOT NULL;

-- Nou índex per la taula d'instàncies de tasca
CREATE INDEX IDX_TASKINST_PROC ON JBPM_TASKINSTANCE (PROCINST_);
CREATE INDEX IDX_TASKINST_TSK ON JBPM_TASKINSTANCE(TASK_);

-- Seqüències del tipus d'expedient
CREATE TABLE HEL_EXPEDIENT_TIPUS_SEQANY (
  ID                NUMBER(19) NOT NULL,
  ANY_              NUMBER(10),
  SEQUENCIA         NUMBER(19),
  EXPEDIENT_TIPUS   NUMBER(19),
  PRIMARY KEY (ID),
  CONSTRAINT HEL_EXPTIPUS_SEQANY_FK FOREIGN KEY (EXPEDIENT_TIPUS) REFERENCES HEL_EXPEDIENT_TIPUS(ID)
);

-- Incrementar llargària camp params dels logs de l'expedient
ALTER TABLE HEL_EXPEDIENT_LOG MODIFY ACCIO_PARAMS VARCHAR2(2048 CHAR);

CREATE INDEX HEL_EXPLOG_EXPID_I ON HEL_EXPEDIENT_LOG (EXPEDIENT_ID);
CREATE INDEX HEL_EXPLOG_TARGETID_I ON HEL_EXPEDIENT_LOG (TARGET_ID);
    
CREATE TABLE
    HEL_EXPEDIENT_TIPUS_SEQDEFANY
    (
        ID NUMBER(19) NOT NULL,
        ANY_ NUMBER(10) NOT NULL,
        SEQUENCIADEFAULT NUMBER(19) NOT NULL,
        EXPEDIENT_TIPUS NUMBER(19),
        PRIMARY KEY (ID),
        CONSTRAINT HEL_EXPTIPUS_SEQDEFANY_FK FOREIGN KEY (EXPEDIENT_TIPUS) REFERENCES
        HEL_EXPEDIENT_TIPUS (ID)
    );

/*
CREATE SEQUENCE SEQDEFANY START WITH 1000000000000000;

-- SELECT per a l'expresió (app.numexp.expression) ${seq}-${any}. Si l'expresió és diferent, s'ha d'adaptar la select. 
-- INSERT INTO HEL_EXPEDIENT_TIPUS_SEQDEFANY (ID, ANY_, SEQUENCIADEFAULT, EXPEDIENT_TIPUS)
-- SELECT SEQDEFANY.NEXTVAL, ANYO, SEQ, TIPUS_ID 
-- FROM
-- (SELECT  TO_NUMBER(SUBSTR(NUMERO_DEFAULT, INSTR(NUMERO_DEFAULT, '-') + 1)) AS ANYO, MAX(TO_NUMBER(SUBSTR(NUMERO_DEFAULT, 0, INSTR(NUMERO_DEFAULT, '-') - 1))) AS SEQ, TIPUS_ID 
-- FROM    HEL_EXPEDIENT 
-- GROUP BY TIPUS_ID, TO_NUMBER(SUBSTR(NUMERO_DEFAULT, INSTR(NUMERO_DEFAULT, '-') + 1))
-- WHERE SEQ IS NOT NULL;

DROP SEQUENCE SEQDEFANY;
*/
    
ALTER TABLE HEL_EXPEDIENT ADD COLUMN error_desc VARCHAR2(255);
ALTER TABLE HEL_EXPEDIENT ADD COLUMN error_full CLOB;

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
    '2.6.0' CODI,
    260 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 260) = 0;
