package com.datavet.auth.domain.exception;

/**
 * Lanzada cuando el token de verificación de email
 * no existe o ha expirado.
 * Resulta en HTTP 400.
 */
public class EmailTokenExpiredException extends AuthDomainException {

  public EmailTokenExpiredException() {
    super("El token de verificación de email ha expirado o no es válido");
  }
}