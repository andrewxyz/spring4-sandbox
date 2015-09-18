package com.hantsylabs.example.spring.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import com.hantsylabs.example.spring.model.Conference;
import com.hantsylabs.example.spring.model.Signup;

@Repository
public interface SignupRepository extends RevisionRepository<Signup, Long, Integer>,
		JpaRepository<Signup, Long>{

	Signup findByConference(Conference conference);

	Signup findById(Long id);

}
