package com.example.curity.Objects;

import java.util.Date;

public class AcceptedAlerts {

    public String userId;
    public String adminId;
    public Double userCurrentLatitude,userCurrentLongitude;
    public Double adminCurrentLatitude,adminCurrentLongitude;

    public AcceptedAlerts(String userId, String adminId, Double userCurrentLatitude, Double userCurrentLongitude, Double adminCurrentLatitude, Double adminCurrentLongitude) {
        this.userId = userId;
        this.adminId = adminId;
        this.userCurrentLatitude = userCurrentLatitude;
        this.userCurrentLongitude = userCurrentLongitude;
        this.adminCurrentLatitude = adminCurrentLatitude;
        this.adminCurrentLongitude = adminCurrentLongitude;

    }
}
