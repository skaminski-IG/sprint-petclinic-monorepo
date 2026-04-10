package org.springframework.samples.petclinic.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.VaccinationMapper;
import org.springframework.samples.petclinic.model.Vaccination;
import org.springframework.samples.petclinic.rest.api.VaccinationsApi;
import org.springframework.samples.petclinic.rest.dto.VaccinationDto;
import org.springframework.samples.petclinic.rest.dto.VaccinationFieldsDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing vaccination records.
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class VaccinationRestController implements VaccinationsApi {

    private final ClinicService clinicService;

    private final VaccinationMapper vaccinationMapper;

    public VaccinationRestController(ClinicService clinicService, VaccinationMapper vaccinationMapper) {
        this.clinicService = clinicService;
        this.vaccinationMapper = vaccinationMapper;
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<List<VaccinationDto>> listVaccinations() {
        List<Vaccination> vaccinations = new ArrayList<>(this.clinicService.findAllVaccinations());
        if (vaccinations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ArrayList<>(vaccinationMapper.toVaccinationsDto(vaccinations)), HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VaccinationDto> getVaccination(Integer vaccinationId) {
        Vaccination vaccination = this.clinicService.findVaccinationById(vaccinationId);
        if (vaccination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vaccinationMapper.toVaccinationDto(vaccination), HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VaccinationDto> addVaccination(VaccinationDto vaccinationDto) {
        HttpHeaders headers = new HttpHeaders();
        Vaccination vaccination = vaccinationMapper.toVaccination(vaccinationDto);
        this.clinicService.saveVaccination(vaccination);
        vaccinationDto = vaccinationMapper.toVaccinationDto(vaccination);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/vaccinations/{id}").buildAndExpand(vaccination.getId()).toUri());
        return new ResponseEntity<>(vaccinationDto, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VaccinationDto> updateVaccination(Integer vaccinationId, VaccinationFieldsDto vaccinationDto) {
        Vaccination currentVaccination = this.clinicService.findVaccinationById(vaccinationId);
        if (currentVaccination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentVaccination.setVaccineName(vaccinationDto.getVaccineName());
        currentVaccination.setVaccinationDate(vaccinationDto.getVaccinationDate());
        this.clinicService.saveVaccination(currentVaccination);
        return new ResponseEntity<>(vaccinationMapper.toVaccinationDto(currentVaccination), HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<VaccinationDto> deleteVaccination(Integer vaccinationId) {
        Vaccination vaccination = this.clinicService.findVaccinationById(vaccinationId);
        if (vaccination == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteVaccination(vaccination);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
