package org.openmrs.module.qxr.dao;

import java.util.List;

import org.openmrs.module.qxr.model.QXRModuleEncounterMapper;

public interface QXRDao {
	
	QXRModuleEncounterMapper saveQxrmoduleEncounterMapper(QXRModuleEncounterMapper qxrmoduleEncounterMapper);
	
	QXRModuleEncounterMapper getQXRmoduleEncounterMapper(String uuid);
	
	List<QXRModuleEncounterMapper> getAllQXRmoduleEncounterMapper();
	
	List<QXRModuleEncounterMapper> getEncounterMapperByPatient(String identifier);
	
	QXRModuleEncounterMapper getQXRmoduleEncounterMapperByImageID(String UID);
	
}
