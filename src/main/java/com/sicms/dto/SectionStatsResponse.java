package com.sicms.dto;

public class SectionStatsResponse {

    private long totalSections;
    private long totalStudentsAssigned;
    private long unassignedStudents;
    private long activeSections;
    private long assignedFaculty;

    public SectionStatsResponse() {}

    public SectionStatsResponse(long totalSections, long totalStudentsAssigned, long unassignedStudents, long activeSections) {
        this.totalSections = totalSections;
        this.totalStudentsAssigned = totalStudentsAssigned;
        this.unassignedStudents = unassignedStudents;
        this.activeSections = activeSections;
    }

    public SectionStatsResponse(long totalSections, long totalStudentsAssigned, long unassignedStudents, long activeSections, long assignedFaculty) {
        this.totalSections = totalSections;
        this.totalStudentsAssigned = totalStudentsAssigned;
        this.unassignedStudents = unassignedStudents;
        this.activeSections = activeSections;
        this.assignedFaculty = assignedFaculty;
    }

    public long getTotalSections() {
        return totalSections;
    }

    public void setTotalSections(long totalSections) {
        this.totalSections = totalSections;
    }

    public long getTotalStudentsAssigned() {
        return totalStudentsAssigned;
    }

    public void setTotalStudentsAssigned(long totalStudentsAssigned) {
        this.totalStudentsAssigned = totalStudentsAssigned;
    }

    public long getUnassignedStudents() {
        return unassignedStudents;
    }

    public void setUnassignedStudents(long unassignedStudents) {
        this.unassignedStudents = unassignedStudents;
    }

    public long getActiveSections() {
        return activeSections;
    }

    public void setActiveSections(long activeSections) {
        this.activeSections = activeSections;
    }

    public long getAssignedFaculty() {
        return assignedFaculty;
    }

    public void setAssignedFaculty(long assignedFaculty) {
        this.assignedFaculty = assignedFaculty;
    }
}
