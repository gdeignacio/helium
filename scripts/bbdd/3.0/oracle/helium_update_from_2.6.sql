ALTER TABLE HEL_USUARI_PREFS
ADD ( CABECERA_REDUCIDA NUMBER(1) NULL  ) 
ADD ( LISTADO NUMBER(1) NULL  ) 
ADD ( CONSULTA_ID NUMBER(19) NULL  ) 
ADD ( FILTRO_TAREAS_ACTIVAS NUMBER(1) NULL  ) 
ADD ( NUM_ELEMENTOS_PAGINA NUMBER(3) NULL  ) ;

ALTER TABLE HEL_USUARI_PREFS ADD ( DEFAULT_TIPUS_EXPEDIENT NUMBER(19) NULL  ) ;

CREATE TABLE HEL_EXPEDIENT_NOTIF_ELECTR (
  ID 					NUMBER 			NOT NULL ,
  DATA 					TIMESTAMP(0) 	NOT NULL ,
  EXPEDIENT_ID 			NUMBER 			NOT NULL ,
  NUMERO 				VARCHAR2(255) 	NOT NULL ,
  RDS_CODI 				VARCHAR2(255) 	NOT NULL ,
  RDS_CLAVE 			VARCHAR2(255) 	NOT NULL 
);

CREATE INDEX HEL_EXPNOTELE_EXPEDIENT_ID_I ON HEL_EXPEDIENT_NOTIF_ELECTR 
(EXPEDIENT_ID);

ALTER TABLE HEL_EXPEDIENT_NOTIF_ELECTR ADD (
  CONSTRAINT HEL_EXPEDIENT_EXPNOTELE_FK 
 FOREIGN KEY (EXPEDIENT_ID) 
 REFERENCES HEL_EXPEDIENT (ID));

ALTER TABLE HEL_REGISTRE
MODIFY ( VALOR_NOU VARCHAR2(4000 CHAR) ) 
MODIFY ( VALOR_VELL VARCHAR2(4000 CHAR) ) ;

ALTER TABLE HEL_PORTAFIRMES ADD (
    DATA_SIGNAT_REBUTJAT TIMESTAMP(6),
    DATA_CUSTODIA_INTENT TIMESTAMP(6),
    DATA_CUSTODIA_OK TIMESTAMP(6),
    DATA_SIGNAL_INTENT TIMESTAMP(6),
    DATA_SIGNAL_OK TIMESTAMP(6));

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
    '3.0.0' CODI,
    300 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 300) = 0;
