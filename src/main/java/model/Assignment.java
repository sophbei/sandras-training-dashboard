package model;

import java.time.LocalDate;
public class Assignment {

 // public enum  AssignmentStatus  {IN_PROGRESS, COMPLETED, ASSIGNED};

  String assignmentId;
  String employeeId;
  String trainingId;
  LocalDate assignedDate;
  LocalDate dueDate;
  String status;
  public LocalDate completionDate;

  public Assignment (String assignmentId, String employeeId, String trainingId, LocalDate assignedDate, LocalDate dueDate, String status, LocalDate completionDate){
    this.assignmentId = assignmentId;
    this.employeeId = employeeId;
    this.trainingId = trainingId;
    this.assignedDate = assignedDate;
    this.dueDate = dueDate;
    this.status = status;
    this.completionDate = completionDate;
  }
  public String getEmployeeId () {
    return employeeId;
  }
  public String getTrainingId(){
    return trainingId;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public LocalDate getCompletionDate() {
    return completionDate;
  }

  public String getStatus() {
    return status;
  }
}
