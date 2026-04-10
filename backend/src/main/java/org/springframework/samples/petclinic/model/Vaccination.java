package org.springframework.samples.petclinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 * Simple JavaBean domain object representing a vaccination record.
 */
@Entity
@Table(name = "vaccinations")
public class Vaccination extends BaseEntity {

    @NotEmpty
    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "vaccination_date", columnDefinition = "DATE")
    private LocalDate vaccinationDate;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public Vaccination() {
        this.vaccinationDate = LocalDate.now();
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public LocalDate getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(LocalDate vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

}
