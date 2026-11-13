
package model;

public class FitnessInformation {

    private int id;
    private int memberId;
    private double weight;
    private double height;
    private String fitnessGoal;
    private String notes;

    public FitnessInformation(int id, int memberId, double weight,
                              double height, String fitnessGoal,
                              String notes) {
        this.id = id;
        this.memberId = memberId;
        this.weight = weight;
        this.height = height;
        this.fitnessGoal = fitnessGoal;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public int getMemberId() {
        return memberId;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public String getNotes() {
        return notes;
    }
}
