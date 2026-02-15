package com.datavet.datavet.pet.domain.model.details.hospitalization;

import com.datavet.datavet.pet.domain.model.action.RecordAction;

public enum HospitalizationStatus {
    SCHEDULED{
        @Override
        public HospitalizationStatus next(RecordAction action){
            return switch (action) {
                case ADMIT -> ADMITTED;
                case CANCEL -> CANCELLED;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    ADMITTED {
        @Override
        public HospitalizationStatus next(RecordAction action){
            return switch (action) {
                case START -> IN_PROGRESS;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    IN_PROGRESS {
        @Override
        public HospitalizationStatus next(RecordAction action){
            return switch (action) {
                case COMPLETE -> COMPLETED;
                case DECLARE_DECEASED -> DECEASED;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    COMPLETED {
        @Override
        public HospitalizationStatus next(RecordAction action){
            throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
        }
    },
    CANCELLED {
        @Override
        public HospitalizationStatus next(RecordAction action){
            throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
        }
    },
    DECEASED {
        @Override
        public HospitalizationStatus next(RecordAction action){
            throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
        }
    };

    public abstract HospitalizationStatus next(RecordAction action);
}
