package com.datavet.datavet.pet.domain.model.details.treatment;

import com.datavet.datavet.pet.domain.model.action.RecordAction;

public enum TreatmentStatus {
    PLANNED {
        @Override
        public TreatmentStatus next(RecordAction action) {
            return switch (action) {
                case ACTIVATE -> ACTIVE;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    ACTIVE {
        @Override
        public TreatmentStatus next(RecordAction action) {
            return switch (action){
                case SUSPEND -> SUSPENDED;
                case FINISH -> FINISHED;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    SUSPENDED {
        @Override
        public TreatmentStatus next(RecordAction action) {
            return switch (action){
                case ACTIVATE -> ACTIVE;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    FINISHED {
        @Override
        public TreatmentStatus next(RecordAction action) {
            throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
        }
    };

    public abstract TreatmentStatus next(RecordAction action);
}
