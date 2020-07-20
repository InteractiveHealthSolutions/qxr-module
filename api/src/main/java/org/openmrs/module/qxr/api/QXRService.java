package org.openmrs.module.qxr.api;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.qxr.model.QXRModuleEncounterMapper;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface QXRService extends OpenmrsService {
	
	QXRModuleEncounterMapper saveQxrmoduleEncounterMapper(QXRModuleEncounterMapper qxrmoduleEncounterMapper)
	        throws APIException;
	
	QXRModuleEncounterMapper getQXRmoduleEncounterMapper(String uuid) throws APIException;
	
	List<QXRModuleEncounterMapper> getAllQxrmoduleEncounterMapper() throws APIException;
	
	List<QXRModuleEncounterMapper> getEncounterMapperByPatient(String patientIdentifier) throws APIException;
	
	QXRModuleEncounterMapper getEncounterMapperByImageID(String UID) throws APIException;
	
}
