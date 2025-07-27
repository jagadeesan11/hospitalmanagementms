package com.example.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ErrorMessageResolver {
    private final MessageSource messageSource;

    public ErrorMessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getErrorMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public ErrorDetail getErrorDetail(String prefix) {
        return new ErrorDetail(
            getErrorMessage(prefix + ".code"),
            getErrorMessage(prefix + ".title"),
            getErrorMessage(prefix + ".detail")
        );
    }

    public static class ErrorDetail {
        private final String code;
        private final String title;
        private final String detail;

        public ErrorDetail(String code, String title, String detail) {
            this.code = code;
            this.title = title;
            this.detail = detail;
        }

        public String getCode() { return code; }
        public String getTitle() { return title; }
        public String getDetail() { return detail; }
    }
}
