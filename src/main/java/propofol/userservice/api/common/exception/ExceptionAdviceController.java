package propofol.userservice.api.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import propofol.userservice.api.common.exception.dto.ErrorDetailDto;
import propofol.userservice.api.common.exception.dto.ErrorDto;
import propofol.userservice.domain.exception.NotFoundMember;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto SQLException(ConstraintViolationException e){
        log.info("Message = {}", e.getMessage());
        return null;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto NotFoundMemberException(NotFoundMember e){
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto validationError(MethodArgumentNotValidException e){
        ErrorDto errorDto = createError("회원 가입 실패!", HttpStatus.BAD_REQUEST);
        e.getFieldErrors().forEach(
                error -> {
                    errorDto.getErrors().add(new ErrorDetailDto(error.getField(), error.getDefaultMessage()));
                }
        );
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto badRequestType1Error(HttpMessageNotReadableException e){
        ErrorDto errorDto = createError("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    private ErrorDto createError(String errorMessage, HttpStatus httpStatus) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(errorMessage);
        errorDto.setStatus(httpStatus.value());
        return errorDto;
    }
}
