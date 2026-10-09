package de.hallberg;

public class Training {

String trainingId;
String title;
String category;
boolean mandatory;
Integer validityMonths;
String targetGroup;
int durationMinutes;
String format;


public Training (String trainingId, String title, String category, boolean mandatory, Integer validityMonths, String targetGroup, int durationMinutes, String format){
    this.trainingId = trainingId;
    this.title = title;
    this.category = category;
    this.mandatory = mandatory;
    this.validityMonths = validityMonths;
    this. targetGroup = targetGroup;
    this.durationMinutes = durationMinutes;
    this.format = format;
}
public String getTrainingId() {
    return trainingId;
    }

    public String getTitle (){
    return title;
    }
    public Integer getValidityMonths() {
        return validityMonths;
    }


    public boolean isMandatory() {
    return mandatory;
    }

    public String getTargetGroup() {
        return targetGroup;
    }
}
