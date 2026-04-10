package org.springframework.samples.petclinic.repository.jdbc;

import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vaccination;
import org.springframework.samples.petclinic.repository.VaccinationRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple JDBC-based implementation of the {@link VaccinationRepository} interface.
 */
@DependsOnDatabaseInitialization
@Repository
@Profile("jdbc")
public class JdbcVaccinationRepositoryImpl implements VaccinationRepository {

    protected SimpleJdbcInsert insertVaccination;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcVaccinationRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertVaccination = new SimpleJdbcInsert(dataSource)
            .withTableName("vaccinations")
            .usingGeneratedKeyColumns("id");
    }

    protected MapSqlParameterSource createVaccinationParameterSource(Vaccination vaccination) {
        return new MapSqlParameterSource()
            .addValue("id", vaccination.getId())
            .addValue("vaccine_name", vaccination.getVaccineName())
            .addValue("vaccination_date", vaccination.getVaccinationDate())
            .addValue("pet_id", vaccination.getPet().getId());
    }

    @Override
    public Vaccination findById(int id) throws DataAccessException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            return this.namedParameterJdbcTemplate.queryForObject(
                "SELECT vaccinations.id as vaccination_id, vaccinations.pet_id as pets_id, vaccine_name, vaccination_date FROM vaccinations WHERE id= :id",
                params,
                new JdbcVaccinationRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Vaccination.class, id);
        }
    }

    @Override
    public Collection<Vaccination> findAll() throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        return this.namedParameterJdbcTemplate.query(
            "SELECT vaccinations.id as vaccination_id, pets.id as pets_id, vaccine_name, vaccination_date FROM vaccinations LEFT JOIN pets ON vaccinations.pet_id = pets.id",
            params, new JdbcVaccinationRowMapper());
    }

    @Override
    public void save(Vaccination vaccination) throws DataAccessException {
        if (vaccination.isNew()) {
            Number newKey = this.insertVaccination.executeAndReturnKey(createVaccinationParameterSource(vaccination));
            vaccination.setId(newKey.intValue());
        } else {
            this.namedParameterJdbcTemplate.update(
                "UPDATE vaccinations SET vaccine_name=:vaccine_name, vaccination_date=:vaccination_date, pet_id=:pet_id WHERE id=:id",
                createVaccinationParameterSource(vaccination));
        }
    }

    @Override
    public void delete(Vaccination vaccination) throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", vaccination.getId());
        this.namedParameterJdbcTemplate.update("DELETE FROM vaccinations WHERE id=:id", params);
    }

    protected class JdbcVaccinationRowMapper implements RowMapper<Vaccination> {

        @Override
        public Vaccination mapRow(ResultSet rs, int rowNum) throws SQLException {
            Vaccination vaccination = new Vaccination();
            vaccination.setId(rs.getInt("vaccination_id"));
            vaccination.setVaccineName(rs.getString("vaccine_name"));
            Date vaccinationDate = rs.getDate("vaccination_date");
            if (vaccinationDate != null) {
                vaccination.setVaccinationDate(new java.sql.Date(vaccinationDate.getTime()).toLocalDate());
            }
            Map<String, Object> params = new HashMap<>();
            params.put("id", rs.getInt("pets_id"));
            JdbcPet pet = JdbcVaccinationRepositoryImpl.this.namedParameterJdbcTemplate.queryForObject(
                "SELECT pets.id as pets_id, name, birth_date, type_id, owner_id FROM pets WHERE pets.id=:id",
                params,
                new JdbcPetRowMapper());
            params.put("type_id", pet.getTypeId());
            PetType petType = JdbcVaccinationRepositoryImpl.this.namedParameterJdbcTemplate.queryForObject(
                "SELECT id, name FROM types WHERE id= :type_id",
                params,
                BeanPropertyRowMapper.newInstance(PetType.class));
            pet.setType(petType);
            params.put("owner_id", pet.getOwnerId());
            Owner owner = JdbcVaccinationRepositoryImpl.this.namedParameterJdbcTemplate.queryForObject(
                "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE id= :owner_id",
                params,
                BeanPropertyRowMapper.newInstance(Owner.class));
            pet.setOwner(owner);
            vaccination.setPet(pet);
            return vaccination;
        }
    }

}
