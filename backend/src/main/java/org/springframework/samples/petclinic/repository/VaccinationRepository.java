package org.springframework.samples.petclinic.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.Vaccination;

import java.util.Collection;

/**
 * Repository class for {@code Vaccination} domain objects.
 */
public interface VaccinationRepository {

    /**
     * Save a {@code Vaccination} to the data store, either inserting or updating it.
     *
     * @param vaccination the {@code Vaccination} to save
     * @see BaseEntity#isNew
     */
    void save(Vaccination vaccination) throws DataAccessException;

    Vaccination findById(int id) throws DataAccessException;

    Collection<Vaccination> findAll() throws DataAccessException;

    void delete(Vaccination vaccination) throws DataAccessException;

}
