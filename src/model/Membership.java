
package model;

public class Membership {

    private int id;
    private int memberId;
    private String plan;
    private String startDate;
    private String endDate;
    private double fee;
    private String status;

    public Membership(int id, int memberId, String plan,
                      String startDate, String endDate,
                      double fee, String status) {
        this.id = id;
        this.memberId = memberId;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getPlan() {
        return plan;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getFee() {
        return fee;
    }

    public String getStatus() {
        return status;
    }
}
