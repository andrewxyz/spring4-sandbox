package com.hantsylabs.example.spring.dao;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.SessionFactory;
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

import com.hantsylabs.example.spring.config.Hibernate5Config;
import com.hantsylabs.example.spring.model.Conference;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={Hibernate5Config.class})
public class Hibernate5ConferenceDaoImplTest {

	private static final Logger log = LoggerFactory
			.getLogger(Hibernate5ConferenceDaoImplTest.class);

	@Autowired
	ConferenceDao conferenceDao;

	@Autowired
	SessionFactory sessionFactory;

	@BeforeClass
	public static void initTestClass() {
		log.debug("===================before class======================");

	}

	@Before
	@Transactional
	public void beforeTestCase() {
		log.debug("===================before test=====================");
		Long id = conferenceDao.save(newConference());
		assertTrue(id != null);
	}

	@After
	@Transactional
	public void afterTestCase() {
		log.debug("===================after test=====================");
		conferenceDao.deleteAll();
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

		log.debug("new conference object:" + conf);
		return conf;
	}

	@Test
	@Transactional
	public void retrieveConference() {

		Long id = conferenceDao.save(newConference());

		sessionFactory.getCurrentSession().flush();
		assertTrue(id != null);
		log.debug("id @=" + id);
		Conference conference = conferenceDao.findById(id);

		assertTrue(conference != null);

		assertTrue("JUD2013".equals(conference.getName()));

		// query by slug
		conference = conferenceDao.findBySlug("jud-2013");

		assertTrue(conference != null);

		assertTrue("JUD2013".equals(conference.getName()));

		// query by slug
		conference = conferenceDao.findBySlug("jud-2013-1");

		assertTrue(conference == null);
	}

	@Test
	@Transactional
	public void updateConference() {

		Long id = conferenceDao.save(newConference());

		sessionFactory.getCurrentSession().flush();

		assertTrue(id != null);
		log.debug("id @=" + id);
		Conference conference = conferenceDao.findById(id);

		assertTrue(conference != null);

		assertTrue("JUD2013".equals(conference.getName()));

		String name = "JUD2013Boston";
		conference.setName(name);

		conferenceDao.update(conference);

		sessionFactory.getCurrentSession().flush();

		conference = conferenceDao.findById(id);

		assertTrue(name.equals(conference.getName()));

	}
	
	@Test(expected=ObjectNotFoundException.class)
	@Transactional
	public void deleteConference() {

		Long id = conferenceDao.save(newConference());

		sessionFactory.getCurrentSession().flush();

		assertTrue(id != null);
		log.debug("id @=" + id);
		Conference conference = conferenceDao.findById(id);

		assertTrue(conference != null);

		assertTrue("JUD2013".equals(conference.getName()));
		
		conferenceDao.delete(conference);

		sessionFactory.getCurrentSession().flush();

		conference = conferenceDao.findById(id);
		
		log.debug("conference@"+conference);

		assertTrue(conference==null);

	}

}
