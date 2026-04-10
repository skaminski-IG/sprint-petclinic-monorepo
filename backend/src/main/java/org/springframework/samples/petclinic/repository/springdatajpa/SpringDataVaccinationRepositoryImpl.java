package org.springframework.samples.petclinic.repository.springdatajpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Vaccination;

@Profile("spring-data-jpa")
public class SpringDataVaccinationRepositoryImpl implements VaccinationRepositoryOverride {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void delete(Vaccination vaccination) throws DataAccessException {
        String vaccinationId = vaccination.getId().toString();
        this.em.createQuery("DELETE FROM Vaccination vaccination WHERE id=" + vaccinationId).executeUpdate();
        if (em.contains(vaccination)) {
            em.remove(vaccination);
        }
    }

}
