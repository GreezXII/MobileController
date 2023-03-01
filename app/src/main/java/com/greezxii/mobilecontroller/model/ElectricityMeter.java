package com.greezxii.mobilecontroller.model;

import java.time.LocalDate;

public class ElectricityMeter {
    public String model;
    public String serialId;
    public Boolean isAntimagnet;
    public Boolean isDisabled;
    public LocalDate installationDate;
    public LocalDate verificationDate;
    public int numberOfDigits;
}
