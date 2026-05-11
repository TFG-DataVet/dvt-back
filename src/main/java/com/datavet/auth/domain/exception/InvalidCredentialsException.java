package com.datavet.auth.domain.exception;

/**
 * Lanzada cuando las credenciales de acceso son incorrectas
 * o cuando una contraseña no cumple los requisitos mínimos.
 * Resulta en HTTP 401.
 */
public class InvalidCredentialsException extends AuthDomainException {

  public InvalidCredentialsException(String message) {
    super(message);
  }
}