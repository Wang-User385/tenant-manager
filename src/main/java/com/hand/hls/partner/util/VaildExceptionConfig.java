package com.hand.hls.partner.util;


import com.hand.hls.partner.dto.NotValidDTO;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VaildExceptionConfig {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public NotValidDTO notValidR(MethodArgumentNotValidException e){
        NotValidDTO notValid = new NotValidDTO();
        notValid.setCode("400");
        notValid.setMsg(e.getBindingResult().getFieldError().getDefaultMessage());
        return notValid;
    }
}
