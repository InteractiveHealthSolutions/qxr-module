package org.openmrs.module.qxr.api.impl;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.qxr.api.QXRService;
import org.openmrs.module.qxr.dao.QXRDao;
import org.openmrs.module.qxr.model.QXRModuleEncounterMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class QXRServiceImpl extends BaseOpenmrsService implements QXRService {
	
	@Autowired
	private QXRDao qxrDao;
	
	public void setqxrDao(QXRDao qxrDao) {
		this.qxrDao = qxrDao;
	}
	
	@Override
	public QXRModuleEncounterMapper saveQxrmoduleEncounterMapper(QXRModuleEncounterMapper qxrmoduleEncounterMapper)
	        throws APIException {
		return qxrDao.saveQxrmoduleEncounterMapper(qxrmoduleEncounterMapper);
	}
	
	@Override
	public List<QXRModuleEncounterMapper> getAllQxrmoduleEncounterMapper() throws APIException {
		return qxrDao.getAllQXRmoduleEncounterMapper();
	}
	
	@Override
	public QXRModuleEncounterMapper getQXRmoduleEncounterMapper(String uuid) throws APIException {
		return qxrDao.getQXRmoduleEncounterMapper(uuid);
	}
	
	@Override
	public List<QXRModuleEncounterMapper> getEncounterMapperByPatient(String patientIdentifier) throws APIException {
		return qxrDao.getEncounterMapperByPatient(patientIdentifier);
	}
	
	@Override
	public QXRModuleEncounterMapper getEncounterMapperByImageID(String UID) throws APIException {
		return qxrDao.getQXRmoduleEncounterMapperByImageID(UID);
	}
	
}
