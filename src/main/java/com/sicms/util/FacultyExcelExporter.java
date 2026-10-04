package com.sicms.util;

import com.sicms.dto.FacultyAssignmentResponse;
import com.sicms.dto.FacultyResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class FacultyExcelExporter {

    public static void exportFaculty(List<FacultyResponse> facultyList, OutputStream outputStream) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet("Faculty Directory");
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
                "Faculty ID",
                "Faculty Name",
                "Email",
                "Phone Number",
                "Designation",
                "Department",
                "Primary Group",
                "Qualification",
                "Assigned Sections",
                "Assigned Students",
                "Status",
                "Joining Date"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (FacultyResponse f : facultyList) {
                Row row = sheet.createRow(rowIdx);
                row.setHeightInPoints(20);

                String assignedSecs = (f.getAssignments() != null && !f.getAssignments().isEmpty())
                        ? f.getAssignments().stream()
                            .filter(FacultyAssignmentResponse::isActive)
                            .map(a -> a.getBranchGroup() + "-" + a.getSection())
                            .distinct()
                            .collect(Collectors.joining(", "))
                        : "None";

                Cell c0 = row.createCell(0); c0.setCellValue(rowIdx); c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1); c1.setCellValue(f.getFacultyId() != null ? f.getFacultyId() : ""); c1.setCellStyle(dataStyle);
                Cell c2 = row.createCell(2); c2.setCellValue(f.getFullName() != null ? f.getFullName() : ""); c2.setCellStyle(dataStyle);
                Cell c3 = row.createCell(3); c3.setCellValue(f.getEmail() != null ? f.getEmail() : ""); c3.setCellStyle(dataStyle);
                Cell c4 = row.createCell(4); c4.setCellValue(f.getMobileNumber() != null ? f.getMobileNumber() : ""); c4.setCellStyle(dataStyle);
                Cell c5 = row.createCell(5); c5.setCellValue(f.getDesignation() != null ? f.getDesignation() : ""); c5.setCellStyle(dataStyle);
                Cell c6 = row.createCell(6); c6.setCellValue(f.getDepartment() != null ? f.getDepartment() : ""); c6.setCellStyle(dataStyle);
                Cell c7 = row.createCell(7); c7.setCellValue(f.getPrimaryGroup() != null ? f.getPrimaryGroup() : ""); c7.setCellStyle(dataStyle);
                Cell c8 = row.createCell(8); c8.setCellValue(f.getQualification() != null ? f.getQualification() : ""); c8.setCellStyle(dataStyle);
                Cell c9 = row.createCell(9); c9.setCellValue(assignedSecs); c9.setCellStyle(dataStyle);
                Cell c10 = row.createCell(10); c10.setCellValue(f.getAssignedStudentCount()); c10.setCellStyle(dataStyle);
                Cell c11 = row.createCell(11); c11.setCellValue(f.getStatus() != null ? f.getStatus() : "ACTIVE"); c11.setCellStyle(dataStyle);
                Cell c12 = row.createCell(12); c12.setCellValue(f.getJoiningDate() != null ? f.getJoiningDate().toString() : ""); c12.setCellStyle(dataStyle);

                rowIdx++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.max(currentWidth + 1000, 3500));
            }

            workbook.write(outputStream);
            workbook.dispose();
        }
    }
}
