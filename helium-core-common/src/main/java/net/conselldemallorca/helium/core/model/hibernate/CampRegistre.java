/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa la pertinença d'un camp a un altre
 * camp de tipus registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_camp_registre",
		uniqueConstraints={
			@UniqueConstraint(columnNames={"registre_id", "membre_id"}),
			@UniqueConstraint(columnNames={"registre_id", "ordre"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_camp_registre",
		indexes = {
				@Index(name = "hel_campreg_registre_i", columnNames = {"registre_id"}),
				@Index(name = "hel_campreg_membre_i", columnNames = {"membre_id"})})
public class CampRegistre implements Serializable, GenericEntity<Long> {

	private Long id;
	private boolean obligatori = true;
	private boolean llistar = true;
	private int ordre;

	@NotNull
	private Camp registre;
	@NotNull
	private Camp membre;



	public CampRegistre() {}
	public CampRegistre(
			Camp registre,
			Camp membre,
			int ordre) {
		this.registre = registre;
		this.membre = membre;
		this.ordre = ordre;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_camp_regmembre")
	@TableGenerator(name="gen_camp_regmembre", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="obligatori")
	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}

	@Column(name="llistar")
	public boolean isLlistar() {
		return llistar;
	}
	public void setLlistar(boolean llistar) {
		this.llistar = llistar;
	}

	@Column(name="ordre")
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="registre_id")
	@ForeignKey(name="hel_camp_regregistre_fk")
	public Camp getRegistre() {
		return registre;
	}
	public void setRegistre(Camp registre) {
		this.registre = registre;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="membre_id")
	@ForeignKey(name="hel_camp_regmembre_fk")
	public Camp getMembre() {
		return membre;
	}
	public void setMembre(Camp membre) {
		this.membre = membre;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((membre == null) ? 0 : membre.hashCode());
		result = prime * result
				+ ((registre == null) ? 0 : registre.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampRegistre other = (CampRegistre) obj;
		if (membre == null) {
			if (other.membre != null)
				return false;
		} else if (!membre.equals(other.membre))
			return false;
		if (registre == null) {
			if (other.registre != null)
				return false;
		} else if (!registre.equals(other.registre))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
