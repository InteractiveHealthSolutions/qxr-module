package org.openmrs.module.qxr.api.impl;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.qxr.api.QXRService;
import org.openmrs.module.qxr.dao.QXRDaoImpl;
import org.openmrs.module.qxr.model.QXRModuleEncounterMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class QXRServiceImpl extends BaseOpenmrsService implements QXRService {
	
	@Autowired
	private QXRDaoImpl dao;
	
	public void setDao(QXRDaoImpl dao) {
		this.dao = dao;
	}
	
	@Override
	public QXRModuleEncounterMapper saveQxrmoduleEncounterMapper(QXRModuleEncounterMapper qxrmoduleEncounterMapper)
	        throws APIException {
		return dao.saveQxrmoduleEncounterMapper(qxrmoduleEncounterMapper);
	}
	
	@Override
	public List<QXRModuleEncounterMapper> getAllQxrmoduleEncounterMapper() throws APIException {
		return dao.getAllQXRmoduleEncounterMapper();
	}
	
	@Override
	public QXRModuleEncounterMapper getQXRmoduleEncounterMapper(String uuid) throws APIException {
		return dao.getQXRmoduleEncounterMapper(uuid);
	}
	
	@Override
	public List<QXRModuleEncounterMapper> getEncounterMapperByPatient(String patientIdentifier) throws APIException {
		return dao.getEncounterMapperByPatient(patientIdentifier);
	}
}
