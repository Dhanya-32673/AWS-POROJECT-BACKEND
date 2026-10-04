package com.sicms.util;

import com.sicms.dto.SectionResponse;
import com.sicms.dto.StudentResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SectionExcelExporter {

    public static void exportSections(List<SectionResponse> sections, OutputStream outputStream) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet("Academic Sections");
            sheet.trackAllColumnsForAutoSizing();

            // Styling
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            String[] headers = {
                "S.No",
                "Section Name",
                "Academic Year",
                "Group / Stream",
                "Year of Study",
                "Assigned Students",
                "Capacity",
                "Utilization %",
                "Status",
                "Assigned Faculty",
                "Description"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (SectionResponse sec : sections) {
                Row row = sheet.createRow(rowIndex);
                row.setHeightInPoints(20);

                int cap = sec.getCapacity() != null ? sec.getCapacity() : 60;
                long assigned = sec.getTotalStudents();
                double util = cap > 0 ? (assigned * 100.0 / cap) : 0.0;

                createCell(row, 0, String.valueOf(rowIndex), dataStyle);
                createCell(row, 1, sec.getName() != null ? sec.getName() : "", dataStyle);
                createCell(row, 2, sec.getAcademicYear() != null ? sec.getAcademicYear() : "", dataStyle);
                createCell(row, 3, sec.getBranchGroup() != null ? sec.getBranchGroup() : "", dataStyle);
                createCell(row, 4, sec.getIntermediateYear() != null ? sec.getIntermediateYear() : "", dataStyle);
                createCell(row, 5, String.valueOf(assigned), dataStyle);
                createCell(row, 6, String.valueOf(cap), dataStyle);
                createCell(row, 7, String.format("%.1f%%", util), dataStyle);
                createCell(row, 8, sec.isActive() ? "Active" : "Inactive", dataStyle);
                createCell(row, 9, sec.getAssignedFacultyName() != null ? sec.getAssignedFacultyName() : "Not Assigned", dataStyle);
                createCell(row, 10, sec.getDescription() != null ? sec.getDescription() : "", dataStyle);

                rowIndex++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            workbook.dispose();
        }
    }

    public static void exportSectionMembers(String sectionName, List<StudentResponse> students, OutputStream outputStream) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet("Section Members");
            sheet.trackAllColumnsForAutoSizing();

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            String[] headers = {
                "S.No",
                "Student ID",
                "Admission Number",
                "Student Name",
                "Group / Stream",
                "Year of Study",
                "Academic Year",
                "Section",
                "Mobile Number",
                "Email Address",
                "Status"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (StudentResponse student : students) {
                Row row = sheet.createRow(rowIndex);
                row.setHeightInPoints(20);

                createCell(row, 0, String.valueOf(rowIndex), dataStyle);
                createCell(row, 1, student.getStudentId() != null ? student.getStudentId() : "", dataStyle);
                createCell(row, 2, student.getAdmissionNumber() != null ? student.getAdmissionNumber() : "", dataStyle);
                createCell(row, 3, student.getFullName() != null ? student.getFullName() : "", dataStyle);
                createCell(row, 4, student.getBranchGroup() != null ? student.getBranchGroup() : "", dataStyle);
                createCell(row, 5, student.getIntermediateYear() != null ? student.getIntermediateYear() : "", dataStyle);
                createCell(row, 6, student.getAcademicYear() != null ? student.getAcademicYear() : "", dataStyle);
                createCell(row, 7, sectionName != null ? sectionName : (student.getSection() != null ? student.getSection() : ""), dataStyle);
                createCell(row, 8, student.getMobileNumber() != null ? student.getMobileNumber() : "", dataStyle);
                createCell(row, 9, student.getEmailAddress1() != null ? student.getEmailAddress1() : "", dataStyle);
                createCell(row, 10, student.getStatus() != null ? student.getStatus().name() : "ACTIVE", dataStyle);

                rowIndex++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            workbook.dispose();
        }
    }

    private static void createCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}
