package com.datavet.pet.domain.model.details.hospitalization;

import com.datavet.pet.domain.exception.MedicalRecordApplyActionException;
import com.datavet.pet.domain.model.action.RecordAction;

public enum HospitalizationStatus {
    SCHEDULED{
        @Override
        public HospitalizationStatus next(RecordAction action){
            return switch (action) {
                case ADMIT -> ADMITTED;
                case CANCEL -> CANCELLED;
                default -> throw new MedicalRecordApplyActionException(action.toString(), "No se puede ejecutar action");
            };
        }
    },
    ADMITTED {
        @Override
        public HospitalizationStatus next(RecordAction action){
            return switch (action) {
                case START -> IN_PROGRESS;
                default -> throw new MedicalRecordApplyActionException(action.toString(), "No se puede ejecutar action");
            };
        }
    },
    IN_PROGRESS {
        @Override
        public HospitalizationStatus next(RecordAction action){
            return switch (action) {
                case COMPLETE -> COMPLETED;
                case DECLARE_DECEASED -> DECEASED;
                default -> throw new MedicalRecordApplyActionException(action.toString(), "No se puede ejecutar action");
            };
        }
    },
    COMPLETED {
        @Override
        public HospitalizationStatus next(RecordAction action){
            throw new MedicalRecordApplyActionException(action.toString(), "No se puede ejecutar action");
        }
    },
    CANCELLED {
        @Override
        public HospitalizationStatus next(RecordAction action){
            throw new MedicalRecordApplyActionException(action.toString(), "No se puede ejecutar action");
        }
    },
    DECEASED {
        @Override
        public HospitalizationStatus next(RecordAction action){
            throw new MedicalRecordApplyActionException(action.toString(), "No se puede ejecutar action");
        }
    };

    public abstract HospitalizationStatus next(RecordAction action);
}
