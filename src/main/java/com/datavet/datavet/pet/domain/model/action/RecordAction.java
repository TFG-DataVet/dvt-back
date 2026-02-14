package com.datavet.datavet.pet.domain.model.action;

public enum RecordAction {
    /*TREATMENT*/
    ACTIVATE,
    REACTIVE,
    SUSPEND,
    FINISH

    /*Hospitalization*/,
    ADMIT,
    START_TREATMENT,
    COMPLETE,
    CANCEL,
    DECLARE_DECEASED,


    DISCHARGE,
    MARK_NO_SHOW
}
