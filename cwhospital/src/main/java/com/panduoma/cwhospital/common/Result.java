package com.panduoma.cwhospital.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private boolean success;
    private String message;
    private String userType;
    private Object user;

    public static Result success(String message, String userType, Object user) {
        return new Result(true, message, userType, user);
    }

    public static Result error(String message) {
        return new Result(false, message, null, null);
    }
}