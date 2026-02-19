package com.datavet.datavet.pet.domain.model.details.document;

import com.datavet.datavet.pet.domain.model.action.RecordAction;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.model.result.StatusChangeResult;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentDetails implements MedicalRecordDetails {

    private String documentName;
    private String documentType;
    private String fileUrl;
    private String mimeType;
    private LocalDate uploadedAt;
    private String uploadedBy;
    private String description;
    private Long fileSizeInBytes;
    private boolean confidential;
    private String checksum;

    @Override
    public MedicalRecordType getType() {
        return MedicalRecordType.DOCUMENT;
    }

    @Override
    public void validate(){
        if (documentName == null || documentName.isBlank()) {
            throw new IllegalArgumentException("El nombre del documento no debe de ser nulo o estar vacio");
        }

        if (documentType == null || documentType.isBlank()) {
            throw new IllegalArgumentException("El tipo del documento no debe de ser nulo o estar vacio");
        }

        if (fileUrl == null || fileUrl.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es nula o esta vacio");
        }

        if (mimeType == null || mimeType.isBlank()) {
            throw new IllegalArgumentException("El tipo de MIME es nulo o esta vacio");
        }

        if (uploadedAt == null || uploadedAt.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("El fecha de la carga del archivo no debe ser nula o tener una fecha futura a hoy");
        }

        if (uploadedBy == null || uploadedBy.isBlank()) {
            throw new IllegalArgumentException("El responsable de cargar el archivo no debe ser nulo o estar vacio");
        }

        if (fileSizeInBytes != null  && fileSizeInBytes <= 0) {
            throw new IllegalArgumentException("El peso del archivo no puede ser menor a 0 bytes");
        }

        if (confidential) {
            if (description == null || description.isBlank()) {
                throw new IllegalArgumentException("Si el archivo es confidencial, debe de incluir una descripción.");
            }
        }

        if (checksum != null) {
            if (checksum.isBlank()) {
                throw new IllegalArgumentException("La integridad del archivo no puede estar vacia.");
            }
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        return false;
    }

    @Override
    public StatusChangeResult applyAction(RecordAction action) {
        throw new IllegalArgumentException("No se puede aplicar una acción de cambio de estado en un regristro que no tiene estados.");
    }

    public static DocumentDetails create(
            String documentName,
            String documentType,
            String fileUrl,
            String mimeType,
            LocalDate uploadedAt,
            String uploadedBy,
            String description,
            Long fileSizeInBytes,
            boolean confidential,
            String checksum
    ) {
        try {
            DocumentDetails details = DocumentDetails.builder()
                    .documentName(documentName)
                    .documentType(documentType)
                    .fileUrl(fileUrl)
                    .mimeType(mimeType)
                    .uploadedAt(uploadedAt)
                    .uploadedBy(uploadedBy)
                    .description(description)
                    .fileSizeInBytes(fileSizeInBytes)
                    .confidential(confidential)
                    .checksum(checksum)
                    .build();

            details.validate();
            return details;
        } catch (RuntimeException e){
            throw e;
        }
    }

}
