INSERT INTO HEL_PERMIS ( CODI, DESCRIPCIO ) VALUES ( 
'HEL_ADMIN', 'Administrador');

INSERT INTO HEL_PERMIS ( CODI, DESCRIPCIO ) VALUES ( 
'HEL_USER', 'Usuari'); 
commit;
INSERT INTO HEL_PERSONA ( ID, AVIS_CORREU, CODI, DATA_NAIXEMENT, EMAIL, FONT, LLINATGE1, LLINATGE2,
LLINATGES, NOM, NOM_SENCER, SEXE, RELLEU_ID ) VALUES ( 
1, 0, 'admin', NULL, 'admin@helium.org', NULL, 'Administrador', NULL, 'Administrador'
, 'Usuari', 'Usuari Administrador', 0, NULL); 
commit;
INSERT INTO HEL_USUARI ( CODI, ACTIU, CONTRASENYA ) VALUES ( 
'admin', 1, '21232f297a57a5a743894a0e4a801fc3'); 
commit;
INSERT INTO HEL_USUARI_PERMIS ( CODI, PERMIS ) VALUES ( 
'admin', 'HEL_ADMIN'); 
commit;
INSERT INTO HEL_VERSIO ( ID, CODI, DESCRIPCIO, ORDRE ) VALUES ( 
1, 'inicial', NULL, 0); 
commit;
INSERT INTO HEL_IDGEN ( TAULA, VALOR ) VALUES ( 
'hel_persona', 1); 
INSERT INTO HEL_IDGEN ( TAULA, VALOR ) VALUES ( 
'hel_versio', 1); 

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


commit;