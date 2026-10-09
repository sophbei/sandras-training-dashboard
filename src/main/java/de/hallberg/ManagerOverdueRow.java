package de.hallberg;

public class ManagerOverdueRow {
    private String managerName;
    private String managerEmail;
    private int affectedEmployees;
    private int overdueCount;

    public ManagerOverdueRow(String managerName, String managerEmail, int affectedEmployees, int overdueCount){
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.affectedEmployees = affectedEmployees;
        this.overdueCount = overdueCount;
    }

    public int getOverdueCount() {
        return overdueCount;
    }
    public String getManagerName(){
        return managerName;
    }
    public String getManagerEmail(){
        return managerEmail;
    }
    public int getAffectedEmployees(){
        return affectedEmployees;
    }

}
