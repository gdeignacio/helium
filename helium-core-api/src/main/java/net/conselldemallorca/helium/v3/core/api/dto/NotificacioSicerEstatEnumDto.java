/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Enumeració amb els possibles estats d'un enviament d'un
 * document a un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioSicerEstatEnumDto {
	PENDENT,
	ENVIAT,
	VALIDAT,
	PROCESSADA,
	ERROR
}
