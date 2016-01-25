package com.communer.Models;

/**
 * Created by יובל on 03/09/2015.
 */
public class PrefixCountry {

    private String countryName, countryNum;

    public PrefixCountry(String countryName, String countryNum) {
        this.countryName = countryName;
        this.countryNum = countryNum;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryNum() {
        return countryNum;
    }

    public void setCountryNum(String countryNum) {
        this.countryNum = countryNum;
    }
}
