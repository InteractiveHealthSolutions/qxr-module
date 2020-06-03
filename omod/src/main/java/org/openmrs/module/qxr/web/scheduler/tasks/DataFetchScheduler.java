package org.openmrs.module.qxr.web.scheduler.tasks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.qxr.api.QXRService;
import org.openmrs.module.qxr.model.QXRModuleEncounterMapper;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataFetchScheduler extends AbstractTask {
	
	private static final Log log = LogFactory.getLog(DataFetchScheduler.class);
	
	private QXRService service = Context.getService(QXRService.class);
	
	@Override
	public void execute() {
		if (!isExecuting) {
			if (log.isDebugEnabled()) {
				log.debug("Start task");
			}
			startExecuting();
			try {
				EncounterType encounterType = Context.getEncounterService().getEncounterType("Xray Order Form");
				if (encounterType != null) {
					Statement stmt = null;
					String encounterFilterQuery = "Select e.encounter_id encounter_id ,e.patient_id patient_id, p.identifier identifier from hydra.encounter e ,hydra.patient_identifier p where e.patient_id = p.patient_id and encounter_type="
					        + encounterType.getEncounterTypeId()
					        + " and encounter_id not in (select order_encounter_id from hydra.qxrmodule_encounter_mapper where result_encounter_id is not null)";
					Connection conn = null;
					try {
						conn = establishDatabaseConnection();
						try {
							stmt = conn.createStatement();
							ResultSet data = stmt.executeQuery(encounterFilterQuery);
							while (data.next()) {
								Encounter orderEncounter = Context.getEncounterService().getEncounter(
								    data.getInt("encounter_id"));
								
								processDataForPatientID(data.getString("identifier"), orderEncounter,
								    data.getInt("patient_id"));
							}
						}
						catch (SQLException e) {
							log.error(e.getMessage() + " " + encounterFilterQuery);
						}
						// conn.close();
					}
					catch (SQLException e) {
						log.error("Error connecting to DB.", e);
					}
					finally {
						conn.close();
					}
				}
				
			}
			catch (Exception e) {
				log.error("execution stopped " + e);
			}
			finally {
				stopExecuting();
			}
		}
		
	}
	
	public void processDataForPatientID(String patientIdentifier, Encounter orderEncounter, Integer patientId) {
		Response response = null;
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(120, TimeUnit.SECONDS)
			        .writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
			String url = "https://covidapi.qure.ai/v2/cxr/batch_results/" + patientIdentifier + "/";
			// String url = "https://covidapi.qure.ai/v2/cxr/batch_results/122T3-0/";
			Request request = new Request.Builder().url(url).method("GET", null)
			        .addHeader("Authorization", "Token 04d48517b635caa928da6894133b54b9a96962cc").build();
			response = client.newCall(request).execute();
			// System.out.println(response.body().string());
			int responseCode = response.code();
			if (responseCode != 200) {
				log.error("Invalid Response " + responseCode + " " + response.message());
			} else {
				Scanner sc = new Scanner(response.body().string());
				String data = "";
				while (sc.hasNext()) {
					data += sc.nextLine();
				}
				sc.close();
				// response.close();
				
				JSONParser jsonParser = new JSONParser();
				JSONArray jsonArray = (JSONArray) jsonParser.parse(data);
				
				String covidResult = "";
				
				// considering data on index 0 because its the latest
				
				JSONObject element = (JSONObject) jsonArray.get(0);
				JSONObject metaDataObj = (JSONObject) element.get("metadata");
				covidResult = (String) metaDataObj.get("covid_score");
				
				Encounter resultEncounter = new Encounter();
				
				EncounterType encounterType = Context.getEncounterService().getEncounterType("Xray Result Form");
				resultEncounter.setEncounterType(encounterType);
				
				User user = Context.getUserService().getUserByUsername("QXR-user");
				
				resultEncounter.setCreator(user);
				
				Patient patient = Context.getPatientService().getPatient(patientId);
				
				resultEncounter.setPatient(patient);
				
				resultEncounter.setEncounterDatetime(new Date());
				// XRay Covid Result Concept
				Concept concept = Context.getConceptService().getConceptByUuid("d3d9j77a-cg7c-431n-9957-bcc0e4ac9a92");
				
				if (concept != null) {
					
					Obs obs = new Obs();
					obs.setConcept(concept);
					String conceptUUID = getCovidResultConceptUIID(covidResult);
					concept = Context.getConceptService().getConceptByUuid(conceptUUID);
					obs.setValueCoded(concept);
					obs.setDateCreated(new Date());
					obs.setCreator(user);
					resultEncounter.addObs(obs);
				}
				
				Context.getEncounterService().saveEncounter(resultEncounter);
				
				QXRModuleEncounterMapper encounterMapper = new QXRModuleEncounterMapper();
				
				encounterMapper.setOrderEncounterId(orderEncounter);
				encounterMapper.setResultEncounterId(resultEncounter);
				encounterMapper.setCreator(user);
				encounterMapper.setDateCreated(new Date());
				
				service.saveQxrmoduleEncounterMapper(encounterMapper);
				
				log.info(data);
				
			}
			
		}
		catch (MalformedURLException e) {
			log.error("Invalid url " + e);
		}
		catch (IOException e) {
			log.error("unable to create http connection " + e);
		}
		catch (ParseException e) {
			log.error(e);
		}
		finally {
			response.close();
		}
		
	}
	
	String getCovidResultConceptUIID(String name) {
		
		Map<String, String> conceptNamesAndUUIDs = new HashMap<String, String>();
		
		conceptNamesAndUUIDs.put("medium", "22c87215-2669-4c3d-9871-28f2012b0846");
		conceptNamesAndUUIDs.put("MEDIUM", "22c87215-2669-4c3d-9871-28f2012b0846");
		conceptNamesAndUUIDs.put("Medium", "22c87215-2669-4c3d-9871-28f2012b0846");
		
		conceptNamesAndUUIDs.put("HIGH", "1408AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		conceptNamesAndUUIDs.put("High", "1408AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		conceptNamesAndUUIDs.put("high", "1408AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		conceptNamesAndUUIDs.put("Low", "1407AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		conceptNamesAndUUIDs.put("LOW", "1407AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		conceptNamesAndUUIDs.put("low", "1407AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		conceptNamesAndUUIDs.put("NONE", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		conceptNamesAndUUIDs.put("None", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		conceptNamesAndUUIDs.put("none", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		
		return conceptNamesAndUUIDs.get(name);
		
	}
	
	Connection establishDatabaseConnection() throws SQLException {
		
		String url = Context.getRuntimeProperties().getProperty("connection.url", null);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			log.error("Could not find JDBC driver class.", e);
			
			throw (SQLException) e.fillInStackTrace();
		}
		
		String username = Context.getRuntimeProperties().getProperty("connection.username");
		String password = Context.getRuntimeProperties().getProperty("connection.password");
		log.debug("connecting to DATABASE: " + OpenmrsConstants.DATABASE_NAME + "USERNAME: " + username + " URL: " + url);
		return DriverManager.getConnection(url, username, password);
		
	}
}
