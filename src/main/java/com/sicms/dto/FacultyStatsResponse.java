package com.sicms.dto;

public class FacultyStatsResponse {

    private long totalFaculty;
    private long activeFaculty;
    private long assignedFaculty;
    private long unassignedFaculty;

    public FacultyStatsResponse() {}

    public FacultyStatsResponse(long totalFaculty, long activeFaculty, long assignedFaculty, long unassignedFaculty) {
        this.totalFaculty = totalFaculty;
        this.activeFaculty = activeFaculty;
        this.assignedFaculty = assignedFaculty;
        this.unassignedFaculty = unassignedFaculty;
    }

    public long getTotalFaculty() {
        return totalFaculty;
    }

    public void setTotalFaculty(long totalFaculty) {
        this.totalFaculty = totalFaculty;
    }

    public long getActiveFaculty() {
        return activeFaculty;
    }

    public void setActiveFaculty(long activeFaculty) {
        this.activeFaculty = activeFaculty;
    }

    public long getAssignedFaculty() {
        return assignedFaculty;
    }

    public void setAssignedFaculty(long assignedFaculty) {
        this.assignedFaculty = assignedFaculty;
    }

    public long getUnassignedFaculty() {
        return unassignedFaculty;
    }

    public void setUnassignedFaculty(long unassignedFaculty) {
        this.unassignedFaculty = unassignedFaculty;
    }
}
