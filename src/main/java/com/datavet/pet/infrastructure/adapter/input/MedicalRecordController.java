package com.datavet.pet.infrastructure.adapter.input;

import com.datavet.pet.application.dto.MedicalRecordResponse;
import com.datavet.pet.application.mapper.MedicalRecordMapper;
import com.datavet.pet.application.port.in.MedicalRecordUseCase;
import com.datavet.pet.application.port.in.command.medicalrecord.ApplyMedicalRecordActionCommand;
import com.datavet.pet.application.port.in.command.medicalrecord.CorrectMedicalRecordCommand;
import com.datavet.pet.application.port.in.command.medicalrecord.CreateMedicalRecordCommand;
import com.datavet.pet.domain.model.MedicalRecord;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.infrastructure.adapter.input.dto.medicalrecord.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-record")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordUseCase medicalRecordUseCase;

    // =========================================================================
    // Ciclo de vida
    // =========================================================================

    /**
     * POST /medical-record
     * Crea un nuevo registro médico de cualquier tipo (CONSULTATION, VACCINE,
     * TREATMENT, SURGERY, HOSPITALIZATION, DIAGNOSIS, ALLERGY, WEIGHT, DOCUMENT).
     *
     * El campo "details" del body es polimórfico:
     * el cliente debe incluir "detailsType": "<TYPE>" para que Jackson
     * deserialice al subtipo correcto de MedicalRecordDetailsRequest.
     *
     * Ejemplo de body para una vacuna:
     * {
     *   "petId": "...",
     *   "clinicId": "...",
     *   "type": "VACCINE",
     *   "veterinarianId": "...",
     *   "notes": "Vacuna anual",
     *   "details": {
     *     "detailsType": "VACCINE",
     *     "vaccineName": "Rabia",
     *     "applicationDate": "2025-03-01",
     *     "nextDoseDate": "2026-03-01",
     *     "batchNumber": "BATCH-001",
     *     "manufacturer": "VetPharma"
     *   }
     * }
     */
    @PostMapping
    public ResponseEntity<MedicalRecordResponse> create(
            @Valid @RequestBody CreateMedicalRecordRequest request) {

        CreateMedicalRecordCommand command = CreateMedicalRecordCommand.builder()
                .petId(request.getPetId())
                .clinicId(request.getClinicId())
                .type(request.getType())
                .veterinarianId(request.getVeterinarianId())
                .notes(request.getNotes())
                .detailsRequest(request.getDetails())
                .build();

        MedicalRecord record = medicalRecordUseCase.createMedicalRecord(command);
        return ResponseEntity.status(201).body(MedicalRecordMapper.toResponse(record));
    }

    /**
     * POST /medical-record/{id}/correction
     * Crea un registro de corrección sobre uno existente.
     * El registro original queda con status CORRECTED y el nuevo queda ACTIVE.
     *
     * Los detalles deben ser del mismo tipo que el registro original.
     */
    @PostMapping("/{id}/correction")
    public ResponseEntity<MedicalRecordResponse> correct(
            @PathVariable String id,
            @Valid @RequestBody CorrectMedicalRecordRequest request) {

        CorrectMedicalRecordCommand command = CorrectMedicalRecordCommand.builder()
                .originalRecordId(id)
                .veterinarianId(request.getVeterinarianId())
                .reason(request.getReason())
                .detailsRequest(request.getDetails())
                .build();

        MedicalRecord corrected = medicalRecordUseCase.correctMedicalRecord(command);
        return ResponseEntity.status(201).body(MedicalRecordMapper.toResponse(corrected));
    }

    /**
     * PATCH /medical-record/{id}/action
     * Aplica una acción de ciclo de vida (ACTIVATE, CANCEL, COMPLETE, etc.)
     * a un registro que soporta estados (SURGERY, HOSPITALIZATION, TREATMENT).
     *
     * Los tipos simples (WEIGHT, DOCUMENT, etc.) lanzarán error de dominio
     * si se intenta aplicar una acción sobre ellos.
     */
    @PatchMapping("/{id}/action")
    public ResponseEntity<MedicalRecordResponse> applyAction(
            @PathVariable String id,
            @Valid @RequestBody ApplyMedicalRecordActionRequest request) {

        ApplyMedicalRecordActionCommand command = ApplyMedicalRecordActionCommand.builder()
                .medicalRecordId(id)
                .action(request.getAction())
                .veterinarianId(request.getVeterinarianId())
                .build();

        MedicalRecord record = medicalRecordUseCase.applyAction(command);
        return ResponseEntity.ok(MedicalRecordMapper.toResponse(record));
    }

    // =========================================================================
    // Consultas
    // =========================================================================

    /**
     * GET /medical-record/{id}
     * Obtiene un registro médico por su id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getById(@PathVariable String id) {
        MedicalRecord record = medicalRecordUseCase.getMedicalRecordById(id);
        return ResponseEntity.ok(MedicalRecordMapper.toResponse(record));
    }

    /**
     * GET /medical-record/pet/{petId}
     * Obtiene todos los registros médicos de una mascota.
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByPet(@PathVariable String petId) {
        List<MedicalRecord> records = medicalRecordUseCase.getMedicalRecordsByPet(petId);
        return ResponseEntity.ok(MedicalRecordMapper.toResponseList(records));
    }

    /**
     * GET /medical-record/pet/{petId}/type/{type}
     * Obtiene todos los registros médicos de una mascota filtrados por tipo.
     * Ejemplo: GET /medical-record/pet/abc123/type/VACCINE
     */
    @GetMapping("/pet/{petId}/type/{type}")
    public ResponseEntity<List<MedicalRecordResponse>> getByPetAndType(
            @PathVariable String petId,
            @PathVariable MedicalRecordType type) {

        List<MedicalRecord> records = medicalRecordUseCase.getMedicalRecordsByType(petId, type);
        return ResponseEntity.ok(MedicalRecordMapper.toResponseList(records));
    }
}