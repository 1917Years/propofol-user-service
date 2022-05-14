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
import propofol.userservice.api.member.controller.dto.ResponseDto;
import propofol.userservice.domain.exception.NotFoundMember;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto Exception(Exception e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "오류", errorDto);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto NotFoundMemberException(NotFoundMember e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "회원 조회 실패", errorDto);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto ExpiredRefreshTokenException(ExpiredRefreshTokenException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "RefreshToken Expired", errorDto);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto validationError(MethodArgumentNotValidException e){
        ErrorDto errorDto = createError("회원 가입 실패!");
        e.getFieldErrors().forEach(
                error -> {
                    errorDto.getErrors().add(new ErrorDetailDto(error.getField(), error.getDefaultMessage()));
                }
        );
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "검증 오류", errorDto);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto badRequestType1Error(HttpMessageNotReadableException e){
        ErrorDto errorDto = createError("잘못된 요청입니다.");
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "잘못된 요청", errorDto);

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto runtimeException(RuntimeException e){
        ErrorDto errorDto = createError(e.getMessage());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "잘못된 요청", errorDto);

    }

    private ErrorDto createError(String errorMessage) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setErrorMessage(errorMessage);
        return errorDto;
    }
}
