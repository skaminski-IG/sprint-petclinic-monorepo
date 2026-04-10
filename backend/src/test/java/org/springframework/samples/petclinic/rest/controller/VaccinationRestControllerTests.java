package org.springframework.samples.petclinic.rest.controller;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.VaccinationMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vaccination;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link VaccinationRestController}
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class VaccinationRestControllerTests {

    @Autowired
    private VaccinationRestController vaccinationRestController;

    @MockitoBean
    private ClinicService clinicService;

    @Autowired
    private VaccinationMapper vaccinationMapper;

    private MockMvc mockMvc;

    private List<Vaccination> vaccinations;

    @BeforeEach
    void initVaccinations() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(vaccinationRestController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        vaccinations = new ArrayList<>();

        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("Eduardo");
        owner.setLastName("Rodriquez");
        owner.setAddress("2693 Commerce St.");
        owner.setCity("McFarland");
        owner.setTelephone("6085558763");

        PetType petType = new PetType();
        petType.setId(2);
        petType.setName("dog");

        Pet pet = new Pet();
        pet.setId(8);
        pet.setName("Rosy");
        pet.setBirthDate(LocalDate.now());
        pet.setOwner(owner);
        pet.setType(petType);

        Vaccination vaccination = new Vaccination();
        vaccination.setId(1);
        vaccination.setPet(pet);
        vaccination.setVaccineName("Rabies");
        vaccination.setVaccinationDate(LocalDate.of(2013, 1, 1));
        vaccinations.add(vaccination);

        vaccination = new Vaccination();
        vaccination.setId(2);
        vaccination.setPet(pet);
        vaccination.setVaccineName("Distemper");
        vaccination.setVaccinationDate(LocalDate.of(2013, 1, 2));
        vaccinations.add(vaccination);
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetVaccinationSuccess() throws Exception {
        given(this.clinicService.findVaccinationById(1)).willReturn(vaccinations.get(0));
        this.mockMvc.perform(get("/api/vaccinations/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.vaccineName").value("Rabies"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetVaccinationNotFound() throws Exception {
        given(this.clinicService.findVaccinationById(999)).willReturn(null);
        this.mockMvc.perform(get("/api/vaccinations/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetAllVaccinationsSuccess() throws Exception {
        given(this.clinicService.findAllVaccinations()).willReturn(vaccinations);
        this.mockMvc.perform(get("/api/vaccinations")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].id").value(1))
            .andExpect(jsonPath("$.[0].vaccineName").value("Rabies"))
            .andExpect(jsonPath("$.[1].id").value(2))
            .andExpect(jsonPath("$.[1].vaccineName").value("Distemper"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetAllVaccinationsNotFound() throws Exception {
        vaccinations.clear();
        given(this.clinicService.findAllVaccinations()).willReturn(vaccinations);
        this.mockMvc.perform(get("/api/vaccinations")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreateVaccinationSuccess() throws Exception {
        Vaccination newVaccination = vaccinations.get(0);
        newVaccination.setId(999);
        ObjectMapper mapper = new ObjectMapper();
        String newVaccinationAsJSON = mapper.writeValueAsString(vaccinationMapper.toVaccinationDto(newVaccination));
        this.mockMvc.perform(post("/api/vaccinations")
                .content(newVaccinationAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreateVaccinationError() throws Exception {
        Vaccination newVaccination = vaccinations.get(0);
        newVaccination.setId(null);
        newVaccination.setVaccineName(null);
        ObjectMapper mapper = new ObjectMapper();
        String newVaccinationAsJSON = mapper.writeValueAsString(vaccinationMapper.toVaccinationDto(newVaccination));
        this.mockMvc.perform(post("/api/vaccinations")
                .content(newVaccinationAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateVaccinationSuccess() throws Exception {
        given(this.clinicService.findVaccinationById(1)).willReturn(vaccinations.get(0));
        Vaccination newVaccination = vaccinations.get(0);
        newVaccination.setVaccineName("Rabies Updated");
        ObjectMapper mapper = new ObjectMapper();
        String newVaccinationAsJSON = mapper.writeValueAsString(vaccinationMapper.toVaccinationDto(newVaccination));
        this.mockMvc.perform(put("/api/vaccinations/1")
                .content(newVaccinationAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().contentType("application/json"))
            .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/api/vaccinations/1")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.vaccineName").value("Rabies Updated"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateVaccinationError() throws Exception {
        Vaccination newVaccination = vaccinations.get(0);
        newVaccination.setVaccineName(null);
        ObjectMapper mapper = new ObjectMapper();
        String newVaccinationAsJSON = mapper.writeValueAsString(vaccinationMapper.toVaccinationDto(newVaccination));
        this.mockMvc.perform(put("/api/vaccinations/1")
                .content(newVaccinationAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testDeleteVaccinationSuccess() throws Exception {
        Vaccination newVaccination = vaccinations.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newVaccinationAsJSON = mapper.writeValueAsString(vaccinationMapper.toVaccinationDto(newVaccination));
        given(this.clinicService.findVaccinationById(1)).willReturn(vaccinations.get(0));
        this.mockMvc.perform(delete("/api/vaccinations/1")
                .content(newVaccinationAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testDeleteVaccinationError() throws Exception {
        Vaccination newVaccination = vaccinations.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newVaccinationAsJSON = mapper.writeValueAsString(vaccinationMapper.toVaccinationDto(newVaccination));
        given(this.clinicService.findVaccinationById(999)).willReturn(null);
        this.mockMvc.perform(delete("/api/vaccinations/999")
                .content(newVaccinationAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

}
