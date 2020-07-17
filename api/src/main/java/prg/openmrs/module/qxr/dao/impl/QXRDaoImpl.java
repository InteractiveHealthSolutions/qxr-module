package prg.openmrs.module.qxr.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.qxr.dao.QXRDao;
import org.openmrs.module.qxr.model.QXRModuleEncounterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("qxr.QXRDao")
@Transactional
public class QXRDaoImpl implements QXRDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public QXRModuleEncounterMapper saveQxrmoduleEncounterMapper(QXRModuleEncounterMapper qxrmoduleEncounterMapper) {
		getSession().saveOrUpdate(qxrmoduleEncounterMapper);
		getSession().flush();
		return qxrmoduleEncounterMapper;
	}
	
	@Override
	public QXRModuleEncounterMapper getQXRmoduleEncounterMapper(String uuid) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(QXRModuleEncounterMapper.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (QXRModuleEncounterMapper) criteria.uniqueResult();
	}
	
	@Override
	public List<QXRModuleEncounterMapper> getAllQXRmoduleEncounterMapper() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(QXRModuleEncounterMapper.class);
		criteria.addOrder(Order.asc("encounterMapperId"));
		return criteria.list();
	}
	
	@Override
	public List<QXRModuleEncounterMapper> getEncounterMapperByPatient(String identifier) {
		Session session = sessionFactory.getCurrentSession();
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
	
	@Override
	public QXRModuleEncounterMapper getQXRmoduleEncounterMapperByImageID(String UID) {
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(QXRModuleEncounterMapper.class);
		criteria.add(Restrictions.eq("instanceUID", UID));
		
		QXRModuleEncounterMapper encounterMapper = (QXRModuleEncounterMapper) criteria.uniqueResult();
		return encounterMapper;
		
	}
	
}
