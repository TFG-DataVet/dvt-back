package com.datavet.auth.domain.exception;

import com.datavet.shared.domain.exception.DomainException;

/**
 * Lanzada cuando las credenciales de acceso son incorrectas
 * o cuando una contraseña no cumple los requisitos mínimos.
 * Resulta en HTTP 401.
 */
public class InvalidCredentialsException extends DomainException {

  public InvalidCredentialsException(String message) {
    super(message);
  }
}