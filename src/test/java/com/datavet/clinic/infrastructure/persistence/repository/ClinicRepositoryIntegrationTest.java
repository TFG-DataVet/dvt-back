package com.datavet.clinic.infrastructure.persistence.repository;

import com.datavet.clinic.domain.model.ClinicStatus;
import com.datavet.clinic.domain.model.LegalType;
import com.datavet.clinic.infrastructure.persistence.document.ClinicDocument;
import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.Email;
import com.datavet.shared.domain.valueobject.Phone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@DisplayName("MongoClinicRepository Integration Tests")
class ClinicRepositoryIntegrationTest {

    @Autowired
    private MongoClinicRepositoryAdapter repository;

    private Address address;
    private Email   email;
    private Phone   phone;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        address = new Address("Calle Test 1", "Madrid", "28001");
        email   = new Email("clinica@test.com");
        phone   = new Phone("+34912345678");
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    // =========================================================================
    // save / findById
    // =========================================================================

    @Test
    @DisplayName("save: should persist and retrieve clinic with all fields")
    void save_ShouldPersistAndRetrieveAllFields() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        assertThat(saved.getId()).isNotNull().isNotBlank();

        Optional<ClinicDocument> found = repository.findById(saved.getId());
        assertThat(found).isPresent();

        ClinicDocument doc = found.get();
        assertThat(doc.getName()).isEqualTo("Clínica Test");
        assertThat(doc.getLegalName()).isEqualTo("Clínica Test S.L.");
        assertThat(doc.getLegalNumber()).isEqualTo("12345678A");
        assertThat(doc.getLegalType()).isEqualTo(LegalType.AUTONOMO);
        assertThat(doc.getStatus()).isEqualTo(ClinicStatus.ACTIVE);
        assertThat(doc.getLogoUrl()).isEqualTo("https://example.com/logo.png");
    }

    @Test
    @DisplayName("save: should persist and retrieve Address value object correctly")
    void save_ShouldPersistAddressValueObject() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        ClinicDocument found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found.getAddress()).isNotNull();
        assertThat(found.getAddress().getStreet()).isEqualTo("Calle Test 1");
        assertThat(found.getAddress().getCity()).isEqualTo("Madrid");
        assertThat(found.getAddress().getPostalCode()).isEqualTo("28001");
    }

    @Test
    @DisplayName("save: should persist and retrieve Email value object correctly")
    void save_ShouldPersistEmailValueObject() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        ClinicDocument found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found.getEmail()).isNotNull();
        assertThat(found.getEmail()).isEqualTo("clinica@test.com");
    }

    @Test
    @DisplayName("save: should persist and retrieve Phone value object correctly")
    void save_ShouldPersistPhoneValueObject() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        ClinicDocument found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found.getPhone()).isNotNull();
        assertThat(found.getPhone().getValue()).isEqualTo("+34912345678");
    }

    @Test
    @DisplayName("save: should persist and retrieve schedule fields correctly")
    void save_ShouldPersistScheduleFields() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        ClinicDocument found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found.getScheduleOpenDays()).isEqualTo("Lunes - Viernes");
        assertThat(found.getScheduleOpenTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(found.getScheduleCloseTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(found.getScheduleNotes()).isEqualTo("Cierra fines de semana");
    }

    // =========================================================================
    // findAll
    // =========================================================================

    @Test
    @DisplayName("findAll: should return all persisted clinics")
    void findAll_ShouldReturnAllClinics() {
        repository.save(buildDocument("clinica1@test.com", "12345678A"));
        repository.save(buildDocument("clinica2@test.com", "87654321B"));

        List<ClinicDocument> all = repository.findAll();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(c -> c.getEmail())
                .containsExactlyInAnyOrder("clinica1@test.com", "clinica2@test.com");
    }

    @Test
    @DisplayName("findAll: should return empty list when no clinics exist")
    void findAll_WhenNoClinics_ShouldReturnEmptyList() {
        List<ClinicDocument> all = repository.findAll();

        assertThat(all).isEmpty();
    }

    // =========================================================================
    // existsById / deleteById
    // =========================================================================

    @Test
    @DisplayName("existsById: should return true for existing clinic")
    void existsById_WhenClinicExists_ShouldReturnTrue() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        assertThat(repository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("existsById: should return false for non-existing clinic")
    void existsById_WhenClinicNotExists_ShouldReturnFalse() {
        assertThat(repository.existsById("id-que-no-existe")).isFalse();
    }

    @Test
    @DisplayName("deleteById: should remove clinic from repository")
    void deleteById_ShouldRemoveClinic() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));
        String id = saved.getId();

        assertThat(repository.existsById(id)).isTrue();

        repository.deleteById(id);

        assertThat(repository.existsById(id)).isFalse();
        assertThat(repository.findById(id)).isEmpty();
    }

    // =========================================================================
    // existsByEmail
    // =========================================================================

    @Test
    @DisplayName("existsByEmail: should return true when email is registered")
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        repository.save(buildDocument("clinica@test.com", "12345678A"));

        assertThat(repository.existsByEmail("clinica@test.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail: should return false when email is not registered")
    void existsByEmail_WhenEmailNotExists_ShouldReturnFalse() {
        assertThat(repository.existsByEmail("noexiste@test.com")).isFalse();
    }

    // =========================================================================
    // existsByLegalNumber
    // =========================================================================

    @Test
    @DisplayName("existsByLegalNumber: should return true when legalNumber is registered")
    void existsByLegalNumber_WhenExists_ShouldReturnTrue() {
        repository.save(buildDocument("clinica@test.com", "12345678A"));

        assertThat(repository.existsByLegalNumber("12345678A")).isTrue();
    }

    @Test
    @DisplayName("existsByLegalNumber: should return false when legalNumber is not registered")
    void existsByLegalNumber_WhenNotExists_ShouldReturnFalse() {
        assertThat(repository.existsByLegalNumber("NO-EXISTE")).isFalse();
    }

    // =========================================================================
    // existsByEmailAndIdNot
    // =========================================================================

    @Test
    @DisplayName("existsByEmailAndIdNot: should return false when email belongs to the same clinic")
    void existsByEmailAndIdNot_WhenEmailBelongsToSameClinic_ShouldReturnFalse() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        assertThat(repository.existsByEmailAndIdNot("clinica@test.com", saved.getId()))
                .isFalse();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot: should return true when email belongs to a different clinic")
    void existsByEmailAndIdNot_WhenEmailBelongsToDifferentClinic_ShouldReturnTrue() {
        ClinicDocument clinic1 = repository.save(buildDocument("clinica1@test.com", "12345678A"));
        repository.save(buildDocument("clinica2@test.com", "87654321B"));

        assertThat(repository.existsByEmailAndIdNot("clinica2@test.com", clinic1.getId()))
                .isTrue();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot: should return false when email does not exist at all")
    void existsByEmailAndIdNot_WhenEmailNotExists_ShouldReturnFalse() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        assertThat(repository.existsByEmailAndIdNot("noexiste@test.com", saved.getId()))
                .isFalse();
    }

    // =========================================================================
    // existsByLegalNumberAndIdNot
    // =========================================================================

    @Test
    @DisplayName("existsByLegalNumberAndIdNot: should return false when legalNumber belongs to the same clinic")
    void existsByLegalNumberAndIdNot_WhenBelongsToSameClinic_ShouldReturnFalse() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        assertThat(repository.existsByLegalNumberAndIdNot("12345678A", saved.getId()))
                .isFalse();
    }

    @Test
    @DisplayName("existsByLegalNumberAndIdNot: should return true when legalNumber belongs to a different clinic")
    void existsByLegalNumberAndIdNot_WhenBelongsToDifferentClinic_ShouldReturnTrue() {
        ClinicDocument clinic1 = repository.save(buildDocument("clinica1@test.com", "12345678A"));
        repository.save(buildDocument("clinica2@test.com", "87654321B"));

        assertThat(repository.existsByLegalNumberAndIdNot("87654321B", clinic1.getId()))
                .isTrue();
    }

    @Test
    @DisplayName("existsByLegalNumberAndIdNot: should return false when legalNumber does not exist at all")
    void existsByLegalNumberAndIdNot_WhenNotExists_ShouldReturnFalse() {
        ClinicDocument saved = repository.save(buildDocument("clinica@test.com", "12345678A"));

        assertThat(repository.existsByLegalNumberAndIdNot("NO-EXISTE", saved.getId()))
                .isFalse();
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private ClinicDocument buildDocument(String emailValue, String legalNumber) {
        return ClinicDocument.builder()
                .name("Clínica Test")
                .legalName("Clínica Test S.L.")
                .legalNumber(legalNumber)
                .legalType(LegalType.AUTONOMO)
                .address(address)
                .phone(phone)
                .email(emailValue)
                .logoUrl("https://example.com/logo.png")
                .scheduleOpenDays("Lunes - Viernes")
                .scheduleOpenTime(LocalTime.of(9, 0))
                .scheduleCloseTime(LocalTime.of(18, 0))
                .scheduleNotes("Cierra fines de semana")
                .status(ClinicStatus.ACTIVE)
                .build();
    }
}