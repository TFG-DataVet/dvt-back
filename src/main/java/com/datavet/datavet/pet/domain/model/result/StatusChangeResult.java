package com.datavet.datavet.pet.domain.model.result;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StatusChangeResult{

    private final String previousStatus;
    private final String newStatus;

    public static StatusChangeResult of(Enum<?> previousStatus, Enum<?> newStatus){
        return new StatusChangeResult(previousStatus.name(), newStatus.name());
    }

}
