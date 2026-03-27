package com.datavet.auth.application.port.in.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginCommand {
    String email;
    String rawPassword;
}