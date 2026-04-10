package org.springframework.samples.petclinic.repository.springdatajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Vaccination;

@Profile("spring-data-jpa")
public interface VaccinationRepositoryOverride {

    void delete(Vaccination vaccination);

}
