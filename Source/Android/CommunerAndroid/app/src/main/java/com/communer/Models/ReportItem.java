package com.communer.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportItem implements Parcelable {

    private LocationObj locationObj;
    private String type;
    private String description;
    private boolean isPoliceReport;
    private boolean isAnnonymous;
    private String severity;

    public ReportItem(LocationObj locationObj, String type, String description, boolean isPoliceReport, boolean isAnnonymous, String severity) {
        this.locationObj = locationObj;
        this.type = type;
        this.description = description;
        this.isPoliceReport = isPoliceReport;
        this.isAnnonymous = isAnnonymous;
        this.severity = severity;
    }

    public LocationObj getLocationObj() {
        return locationObj;
    }

    public void setLocationObj(LocationObj locationObj) {
        this.locationObj = locationObj;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPoliceReport() {
        return isPoliceReport;
    }

    public void setIsPoliceReport(boolean isPoliceReport) {
        this.isPoliceReport = isPoliceReport;
    }

    public boolean isAnnonymous() {
        return isAnnonymous;
    }

    public void setIsAnnonymous(boolean isAnnonymous) {
        this.isAnnonymous = isAnnonymous;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.locationObj, 0);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeByte(isPoliceReport ? (byte) 1 : (byte) 0);
        dest.writeByte(isAnnonymous ? (byte) 1 : (byte) 0);
        dest.writeString(this.severity);
    }

    protected ReportItem(Parcel in) {
        this.locationObj = in.readParcelable(LocationObj.class.getClassLoader());
        this.type = in.readString();
        this.description = in.readString();
        this.isPoliceReport = in.readByte() != 0;
        this.isAnnonymous = in.readByte() != 0;
        this.severity = in.readString();
    }

    public static final Parcelable.Creator<ReportItem> CREATOR = new Parcelable.Creator<ReportItem>() {
        public ReportItem createFromParcel(Parcel source) {
            return new ReportItem(source);
        }

        public ReportItem[] newArray(int size) {
            return new ReportItem[size];
        }
    };
}
