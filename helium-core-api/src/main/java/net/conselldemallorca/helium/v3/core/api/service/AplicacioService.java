/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;

/**
 * Servei amb funcionalitat a nivell d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	/**
	 * Retorna les preferències de l'usuari actual.
	 * 
	 * @return Les preferències.
	 */
	public UsuariPreferenciesDto getUsuariPreferencies() throws NoTrobatException;

	/**
	 * Retorna informació d'una persona donat el seu codi.
	 * 
	 * @param codi el codi de la persona
	 * @return la informació de la persona
	 */
	public PersonaDto findPersonaAmbCodi(String codi) throws NoTrobatException, SistemaExternException;

	/**
	 * Retorna una llista de persones que tenen una part del nom que
	 * coincideix amb el text especificat.
	 * 
	 * @param text el text per a fer la consulta
	 * @return la llista de persones
	 */
	public List<PersonaDto> findPersonaLikeNomSencer(String text) throws SistemaExternException;

}
