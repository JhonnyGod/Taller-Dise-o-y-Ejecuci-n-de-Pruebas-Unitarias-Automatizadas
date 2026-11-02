package com.cps.disenodepruebas.taller.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarErroresValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> detalles = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            detalles.putIfAbsent(
                error.getField(),
                error.getDefaultMessage()
            )
        );

        ApiError respuesta = new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Se encontraron errores de validación",
            detalles,
            request.getRequestURI()
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(respuesta);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> manejarErrorLecturaJSON(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        ApiError respuesta = new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "El cuerpo JSON es inválido o contiene valores no permitidos",
            Map.of(),
            request.getRequestURI()
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(respuesta);
    }
}