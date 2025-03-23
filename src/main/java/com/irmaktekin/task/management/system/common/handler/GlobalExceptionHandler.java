package com.irmaktekin.task.management.system.common.handler;

import com.irmaktekin.task.management.system.common.ErrorResponseDto;
import com.irmaktekin.task.management.system.common.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.relation.RoleNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponseDto> createErrorResponse(Exception ex,HttpStatus status){
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .message(ex.getMessage())
                .statusCode(status.value())
                .errorType(ex.getClass().getSimpleName())
                .build();
        return new ResponseEntity<>(errorResponseDto,status);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return createErrorResponse(ex,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TaskReasonRequiredException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(TaskReasonRequiredException ex) {
        return createErrorResponse(ex,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(TaskAlreadyCompletedException ex) {
        return createErrorResponse(ex,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskBlockedTransitionException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(TaskBlockedTransitionException ex) {
        return createErrorResponse(ex,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskStateAlreadySameException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(TaskStateAlreadySameException ex) {
        return createErrorResponse(ex,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTaskStateException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException( InvalidTaskStateException ex) {
        return createErrorResponse(ex,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(RoleNotFoundException ex){
        return createErrorResponse(ex,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(TaskNotFoundException ex){
        return createErrorResponse(ex,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(ProjectNotFoundException ex){
        return createErrorResponse(ex,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(UserNotFoundException ex){
        return createErrorResponse(ex,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyAssignedToProjectException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyAssignedProject(UserAlreadyAssignedToProjectException ex) {
        return createErrorResponse(ex,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex) {
        return createErrorResponse(ex,HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponseDto> handleUnAuthorized(UnauthorizedActionException ex) {
        return createErrorResponse(ex,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .message("Validation failed")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errorType("There are missing fields")
                .validationErrors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponseDto);
    }

}
