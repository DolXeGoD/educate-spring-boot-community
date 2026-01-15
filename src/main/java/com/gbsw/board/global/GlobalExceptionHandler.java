package com.gbsw.board.global;

import com.gbsw.board.dto.global.ApiResponse;
import com.gbsw.board.dto.global.ValidationErrorDTO;
import com.gbsw.board.exceptions.*;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler({
            AuthorizationFailureException.class, // 인가 custom exception
            AuthorizationDeniedException.class // from Spring Security @PreAuthorize
    })
    public ResponseEntity<ApiResponse<?>> handleUnauthorized(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthenticated(AuthenticationFailureException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidationErrorDTO>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ValidationErrorDTO> errorResults = new ArrayList<>();
        // 검증 통과 못한 폼 갯수만큼 반복
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            // DTO 생성
            errorResults.add(
                    ValidationErrorDTO.builder()
                            .message(error.getDefaultMessage())
                            .field(error.getField())
                            .build()
            );
        }

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(errorResults, "잘못 입력된 값이 있습니다."));
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidState(InvalidStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExists(ResourceAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
    }
}
