package org.openmrs.module.qxr.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.qxr.model.QXRModuleEncounterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("qxr.QXRDao")
@Transactional
public class QXRDaoImpl {
	
	@Autowired
	DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public QXRModuleEncounterMapper saveQxrmoduleEncounterMapper(QXRModuleEncounterMapper qxrmoduleEncounterMapper) {
		getSession().saveOrUpdate(qxrmoduleEncounterMapper);
		getSession().flush();
		return qxrmoduleEncounterMapper;
	}
	
	public QXRModuleEncounterMapper getQXRmoduleEncounterMapper(String uuid) {
		DbSession session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(QXRModuleEncounterMapper.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (QXRModuleEncounterMapper) criteria.uniqueResult();
	}
	
	public List<QXRModuleEncounterMapper> getAllQXRmoduleEncounterMapper() {
		DbSession session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(QXRModuleEncounterMapper.class);
		criteria.addOrder(Order.asc("encounterMapperId"));
		return criteria.list();
	}
	
	public List<QXRModuleEncounterMapper> getEncounterMapperByPatient(String identifier) {
		DbSession session = sessionFactory.getCurrentSession();
		System.out.println("in dao " + identifier);
		List<Patient> patient = Context.getPatientService().getPatients(null, identifier, null, true);
		if (patient != null) {
			System.out.println("in patient not null");
			List<QXRModuleEncounterMapper> list = (List<QXRModuleEncounterMapper>) session
			        .createQuery(
			            "from QXRModuleEncounterMapper where orderEncounterId.patient.patientId = "
			                    + patient.get(0).getPatientId()
			                    + " and orderEncounterId.encounterId=(select max(orderEncounterId.encounterId) from QXRModuleEncounterMapper)")
			        .list();
			return list;
		}
		return null;
		
	}
	
}
