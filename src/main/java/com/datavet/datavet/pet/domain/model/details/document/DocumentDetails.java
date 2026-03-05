package com.datavet.datavet.pet.domain.model.details.document;

import com.datavet.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.datavet.pet.domain.model.details.MedicalRecordDetails;
import com.datavet.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.datavet.shared.domain.validation.ValidationResult;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentDetails implements MedicalRecordDetails {

    private String documentName;
    private String documentType;
    private String fileUrl;
    private String mimeType;
    private LocalDateTime uploadedAt;
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
        ValidationResult result = new ValidationResult();

        if (documentName == null || documentName.isBlank()) {
            result.addError("Document - name","El nombre del documento no debe de ser nulo o estar vacio.");
        }

        if (documentType == null || documentType.isBlank()) {
            result.addError("Document - type","El tipo del documento no debe de ser nulo o estar vacio.");
        }

        if (fileUrl == null || fileUrl.isBlank()) {
            result.addError("Document - FileUrl","La ruta del archivo es nula o esta vacio");
        }

        if (mimeType == null || mimeType.isBlank()) {
            result.addError("Document - mimeType", "El tipo de MIME es nulo o esta vacio.");
        }

        if (uploadedAt == null || uploadedAt.isAfter(LocalDateTime.now())){
            result.addError("Document - uploadedAt","El fecha de la carga del archivo no debe ser nula o tener una fecha futura a hoy.");
        }

        if (uploadedBy == null || uploadedBy.isBlank()) {
            result.addError("Document - uploadedBy","El responsable de cargar el archivo no debe ser nulo o estar vacio.");
        }

        if (fileSizeInBytes != null  && fileSizeInBytes <= 0) {
            result.addError("Document - fileSizeInBytes", "El peso del archivo no puede ser menor a 0 bytes.");
        }

        if (confidential) {
            if (description == null || description.isBlank()) {
                result.addError("Document - confidential, description","Si el archivo es confidencial, debe de incluir una descripción.");
            }
        }

        if (checksum != null && checksum.isBlank()) {
            result.addError("Document - checksum", "La integridad del archivo no puede estar vacia.");
        }

        if (result.hasErrors()) {
            throw new MedicalRecordValidationException(result);
        }
    }

    @Override
    public boolean canCorrect(MedicalRecordDetails previous) {
        return false;
    }

    public static DocumentDetails create(
            String documentName,
            String documentType,
            String fileUrl,
            String mimeType,
            LocalDateTime uploadedAt,
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
        } catch (MedicalRecordValidationException e){
            throw e;
        }
    }

}
