package com.communer.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class SecurityEvent implements Parcelable {

    private String id;
    private ReportItem report;
    private String reporterName;

    public SecurityEvent(String id, ReportItem report, String reportName) {
        this.id = id;
        this.report = report;
        this.reporterName = reportName;
    }

    public ReportItem getReport() {
        return report;
    }

    public void setReport(ReportItem report) {
        this.report = report;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reportName) {
        this.reporterName = reportName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.report, 0);
        dest.writeString(this.reporterName);
    }

    protected SecurityEvent(Parcel in) {
        this.id = in.readString();
        this.report = in.readParcelable(ReportItem.class.getClassLoader());
        this.reporterName = in.readString();
    }

    public static final Creator<SecurityEvent> CREATOR = new Creator<SecurityEvent>() {
        public SecurityEvent createFromParcel(Parcel source) {
            return new SecurityEvent(source);
        }

        public SecurityEvent[] newArray(int size) {
            return new SecurityEvent[size];
        }
    };
}
