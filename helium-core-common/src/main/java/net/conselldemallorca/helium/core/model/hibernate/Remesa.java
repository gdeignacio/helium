/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;

import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;

/**
 * Objecte de domini que representa una notificació electronica de un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_remesa",
uniqueConstraints = {
		@UniqueConstraint(columnNames = {
				"codi",
				"expedient_tipus_id"})})
public class Remesa implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_remesa")
	@TableGenerator(name="gen_remesa", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	@Column(name = "codi", nullable = false)
	private String codi;
	@Column(name = "estat", nullable = false)
	@Enumerated(EnumType.STRING)
	private DocumentEnviamentEstatEnumDto estat;
	@Column(name = "data_creacio", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCreacio;
	@Column(name = "data_enviament")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataEnviament;
	@Column(name = "data_emisio")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataEmisio;
	@Column(name = "data_prevista_deposit")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataPrevistaDeposit;
	@ManyToOne(optional=false)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptipus_remesa_fk")
	private ExpedientTipus expedientTipus;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	public DocumentEnviamentEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(DocumentEnviamentEstatEnumDto estat) {
		this.estat = estat;
	}

	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	public Date getDataEnviament() {
		return dataEnviament;
	}
	public void setDataEnviament(Date dataEnviament) {
		this.dataEnviament = dataEnviament;
	}

	public Date getDataEmisio() {
		return dataEmisio;
	}
	public void setDataEmisio(Date dataEmisio) {
		this.dataEmisio = dataEmisio;
	}

	public Date getDataPrevistaDeposit() {
		return dataPrevistaDeposit;
	}
	public void setDataPrevistaDeposit(Date dataPrevistaDeposit) {
		this.dataPrevistaDeposit = dataPrevistaDeposit;
	}

	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	private static final long serialVersionUID = 1L;
}
