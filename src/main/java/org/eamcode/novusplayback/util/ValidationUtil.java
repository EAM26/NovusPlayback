package org.eamcode.novusplayback.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
public class ValidationUtil {

    public StringBuilder validationMessage(BindingResult br) {
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid request parameters:\n");
        for (FieldError fe : br.getFieldErrors()) {
            sb.append(fe.getField());
            sb.append(": ");
            sb.append(fe.getDefaultMessage());
            sb.append("\n");
        }
        return sb;
    }
}
