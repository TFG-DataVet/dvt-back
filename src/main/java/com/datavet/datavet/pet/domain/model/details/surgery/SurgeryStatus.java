package com.datavet.datavet.pet.domain.model.details.surgery;

import com.datavet.datavet.pet.domain.model.action.RecordAction;

public enum SurgeryStatus {
    SCHEDULED {
        @Override
        public SurgeryStatus next(RecordAction action) {
            return switch (action){
                case ADMIT -> ADMITTED;
                case CANCEL -> CANCELLED;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    ADMITTED {
        @Override
        public SurgeryStatus next(RecordAction action) {
            return switch (action){
                case START -> IN_PROGRESS;
                case CANCEL -> CANCELLED;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    IN_PROGRESS {
        @Override
        public SurgeryStatus next(RecordAction action) {
            return switch (action){
                case COMPLETE -> COMPLETED;
                case DECLARE_DECEASED -> DECEASED;
                default -> throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
            };
        }
    },
    COMPLETED {
        @Override
        public SurgeryStatus next(RecordAction action) {
            throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
        }
    },
    CANCELLED {
        @Override
        public SurgeryStatus next(RecordAction action) {
            throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
        }
    },
    DECEASED {
        @Override
        public SurgeryStatus next(RecordAction action) {
            throw new IllegalStateException("No se puede ejecutar " + action + " desde " + this);
        }
    };

    public abstract SurgeryStatus next(RecordAction action);
}
