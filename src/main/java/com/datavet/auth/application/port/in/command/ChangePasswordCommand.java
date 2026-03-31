package com.datavet.auth.application.port.in.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChangePasswordCommand {
    String userId;
    String currentRawPassword;
    String newRawPassword;
}