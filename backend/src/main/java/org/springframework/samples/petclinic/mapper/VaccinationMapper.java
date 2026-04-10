package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.model.Vaccination;
import org.springframework.samples.petclinic.rest.dto.VaccinationDto;
import org.springframework.samples.petclinic.rest.dto.VaccinationFieldsDto;

import java.util.Collection;

/**
 * Map Vaccination & VaccinationDto using mapstruct
 */
@Mapper(uses = PetMapper.class)
public interface VaccinationMapper {
    @Mapping(source = "petId", target = "pet.id")
    Vaccination toVaccination(VaccinationDto vaccinationDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    Vaccination toVaccination(VaccinationFieldsDto vaccinationFieldsDto);

    @Mapping(source = "pet.id", target = "petId")
    VaccinationDto toVaccinationDto(Vaccination vaccination);

    Collection<VaccinationDto> toVaccinationsDto(Collection<Vaccination> vaccinations);

}
