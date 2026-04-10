package org.springframework.samples.petclinic.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Vaccination;
import org.springframework.samples.petclinic.repository.VaccinationRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * JPA implementation of the {@link VaccinationRepository} interface.
 */
@Repository
@Profile("jpa")
public class JpaVaccinationRepositoryImpl implements VaccinationRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Vaccination vaccination) {
        if (vaccination.getId() == null) {
            this.em.persist(vaccination);
        } else {
            this.em.merge(vaccination);
        }
    }

    @Override
    public Vaccination findById(int id) throws DataAccessException {
        return this.em.find(Vaccination.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Vaccination> findAll() throws DataAccessException {
        return this.em.createQuery("SELECT v FROM Vaccination v").getResultList();
    }

    @Override
    public void delete(Vaccination vaccination) throws DataAccessException {
        this.em.remove(this.em.contains(vaccination) ? vaccination : this.em.merge(vaccination));
    }

}
