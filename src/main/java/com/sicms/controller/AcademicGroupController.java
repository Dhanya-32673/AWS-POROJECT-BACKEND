package com.sicms.controller;

import com.sicms.dto.AssignStudentsRequest;
import com.sicms.dto.CreateSectionRequest;
import com.sicms.dto.SectionResponse;
import com.sicms.dto.SectionStatsResponse;
import com.sicms.dto.StudentResponse;
import com.sicms.entity.AcademicGroup;
import com.sicms.service.AcademicGroupService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academic")
public class AcademicGroupController {

    private final AcademicGroupService groupService;

    public AcademicGroupController(AcademicGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/groups")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<AcademicGroup>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @PostMapping("/groups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicGroup> createGroup(@Valid @RequestBody AcademicGroup group) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createGroup(group));
    }

    @DeleteMapping("/groups/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sections")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        return ResponseEntity.ok(groupService.getSectionResponses());
    }

    @GetMapping("/sections/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<SectionStatsResponse> getSectionStats() {
        return ResponseEntity.ok(groupService.getSectionStats());
    }

    @GetMapping("/sections/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<SectionResponse> getSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getSectionById(id));
    }

    @PostMapping("/sections")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectionResponse> createSection(@Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.createSection(request));
    }

    @PutMapping("/sections/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long id,
            @Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.ok(groupService.updateSection(id, request));
    }

    @PatchMapping("/sections/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectionResponse> toggleSectionStatus(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Boolean> body) {
        boolean active = body != null && body.containsKey("active") ? body.get("active") : true;
        return ResponseEntity.ok(groupService.toggleSectionStatus(id, active));
    }

    @DeleteMapping("/sections/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        groupService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sections/{id}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<StudentResponse>> getSectionMembers(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getSectionMembers(id));
    }

    @GetMapping("/sections/{id}/available-students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentResponse>> getAvailableStudents(
            @PathVariable Long id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String branchGroup,
            @RequestParam(required = false) String intermediateYear,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false, defaultValue = "false") Boolean unassignedOnly) {
        return ResponseEntity.ok(groupService.getAvailableStudents(id, search, branchGroup, intermediateYear, academicYear, unassignedOnly));
    }

    @PostMapping({"/sections/{id}/assign", "/sections/{id}/members"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignStudentsToSection(
            @PathVariable Long id,
            @RequestBody AssignStudentsRequest request) {
        groupService.assignStudentsToSection(id, request.getStudentIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/sections/{id}/members/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeStudentFromSection(
            @PathVariable Long id,
            @PathVariable String studentId) {
        groupService.removeStudentFromSection(id, studentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sections/{id}/remove-students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeStudentsFromSection(
            @PathVariable Long id,
            @RequestBody List<String> studentIds) {
        groupService.removeStudentsFromSection(id, studentIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sections/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public void exportSectionsToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Bhashyam_Academic_Sections.xlsx\"");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        groupService.exportSectionsToExcel(response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("/sections/{id}/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public void exportSectionStudentsToExcel(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Section_Students.xlsx\"");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        groupService.exportSectionStudentsToExcel(id, response.getOutputStream());
        response.flushBuffer();
    }
}
