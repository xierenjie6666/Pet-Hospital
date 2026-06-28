package com.example.pethospitalapp;

public final class ApiConfig {
    public static final String BASE_URL = "http://192.168.29.1:8080";
    //public static final String BASE_URL = "http://192.168.43.48:8080";
    public static final String APPOINTMENTS = BASE_URL + "/appointments";
    public static final String DOCTOR = BASE_URL + "/doctor";
    public static final String API_USER = BASE_URL + "/api/user";
    public static final String API_ADMIN = BASE_URL + "/api/admin";
    public static final String ANNOUNCEMENT = BASE_URL + "/announcement";

    private ApiConfig() {}
}
