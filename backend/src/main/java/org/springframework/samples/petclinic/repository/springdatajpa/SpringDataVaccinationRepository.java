package org.springframework.samples.petclinic.repository.springdatajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.model.Vaccination;
import org.springframework.samples.petclinic.repository.VaccinationRepository;

/**
 * Spring Data JPA specialization of the {@link VaccinationRepository} interface
 */
@Profile("spring-data-jpa")
public interface SpringDataVaccinationRepository extends VaccinationRepository, Repository<Vaccination, Integer>, VaccinationRepositoryOverride {
}
