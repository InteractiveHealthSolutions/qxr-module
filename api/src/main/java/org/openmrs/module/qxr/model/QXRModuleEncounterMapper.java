package org.openmrs.module.qxr.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;

@Entity
@Table(name = "qxrmodule_encounter_mapper", catalog = "hydra")
public class QXRModuleEncounterMapper extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 8725939628685522359L;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "encounter_mapper_id", unique = true, nullable = false)
	private Integer encounterMapperId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_encounter_id", referencedColumnName = "encounter_id")
	private Encounter orderEncounterId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "result_encounter_id", referencedColumnName = "encounter_id")
	private Encounter resultEncounterId;
	
	@Column(name = "image_id", unique = true)
	private String imageId;
	
	public Encounter getResultEncounterId() {
		return resultEncounterId;
	}
	
	public void setResultEncounterId(Encounter resultEncounterId) {
		this.resultEncounterId = resultEncounterId;
	}
	
	public Integer getEncounterMapperId() {
		return encounterMapperId;
	}
	
	public void setEncounterMapperId(Integer encounterMapperId) {
		this.encounterMapperId = encounterMapperId;
	}
	
	public Encounter getOrderEncounterId() {
		return orderEncounterId;
	}
	
	public void setOrderEncounterId(Encounter orderEncounterId) {
		this.orderEncounterId = orderEncounterId;
	}
	
	public String getImageId() {
		return imageId;
	}
	
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	
	@Override
	public Integer getId() {
		return encounterMapperId;
	}
	
	@Override
	public void setId(Integer id) {
		this.encounterMapperId = id;
	}
	
}
