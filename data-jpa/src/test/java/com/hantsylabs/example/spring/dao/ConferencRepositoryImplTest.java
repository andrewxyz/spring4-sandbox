package com.hantsylabs.example.spring.dao;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.hantsylabs.example.spring.config.JpaConfig;
import com.hantsylabs.example.spring.jpa.ConferenceRepository;
import com.hantsylabs.example.spring.model.Address;
import com.hantsylabs.example.spring.model.Conference;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={JpaConfig.class})
public class ConferencRepositoryImplTest {
	private static final Logger log = LoggerFactory
			.getLogger(ConferencRepositoryImplTest.class);

	@Autowired
	private ConferenceRepository conferenceRepository;

	@PersistenceContext
	EntityManager em;
	
	
	private Address newAddress() {
		Address address = new Address();
		address.setAddressLine1("address line 1");
		address.setAddressLine2("address line 2");
		address.setCity("NY");
		address.setCountry("CN");
		address.setZipCode("510000");

		return address;
	}

	private Conference newConference() {
		Conference conf = new Conference();
		conf.setName("JUD2013");
		conf.setSlug("jud-2013");
		conf.setDescription("JBoss User Developer Conference 2013 Boston");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 30);

		Date startedDate = cal.getTime();

		conf.setStartedDate(startedDate);

		cal.add(Calendar.DAY_OF_YEAR, 7);

		Date endedDate = cal.getTime();
		conf.setEndedDate(endedDate);

		conf.setAddress(newAddress());
		log.debug("new conference object:" + conf);
		return conf;
	}

	@BeforeClass
	public static void init() {
		log.debug("==================before class=========================");
		
	}
	
	
	@Before
	@Transactional
	public void beforeTestCase(){
		log.debug("==================before test case=========================");
		conferenceRepository.save(newConference());
	}
	
	
	@After
	@Transactional
	public void afterTestCase(){
		log.debug("==================after test case=========================");
		conferenceRepository.deleteAll();
	}
	
	
	@Test 
	@Transactional
	public void retrieveConference(){
		Conference conference=newConference();
		conference.setSlug("test-jud");
		conference.setName("Test JUD");
		conference.getAddress().setCountry("US");
		conference =conferenceRepository.save(conference);
		em.flush();	
		assertTrue(null!=conference.getId());
		em.clear();
		
		conference=conferenceRepository.findBySlug("test-jud");
		assertTrue(null!=conference);
		em.clear();
		
		List<Conference> confs=conferenceRepository.findByAddressCountry("US");
		assertTrue(!confs.isEmpty());
		em.clear();
		
		
		confs=conferenceRepository.searchByConferenceName("Test JUD");
		assertTrue(!confs.isEmpty());
		em.clear();
		
		confs=conferenceRepository.searchByNamedConferenceName("Test JUD");
		assertTrue(!confs.isEmpty());
		em.clear();
		
		confs=conferenceRepository.searchByMyNamedQuery("Test JUD");
		assertTrue(!confs.isEmpty());
		em.clear();
			
	} 

}
