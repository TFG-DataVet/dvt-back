package com.datavet.auth.domain.exception;

import com.datavet.shared.domain.exception.DomainException;

/**
 * Lanzada cuando el token de verificación de email
 * no existe o ha expirado.
 * Resulta en HTTP 400.
 */
public class EmailTokenExpiredException extends DomainException {

  public EmailTokenExpiredException() {
    super("El token de verificación de email ha expirado o no es válido");
  }
}