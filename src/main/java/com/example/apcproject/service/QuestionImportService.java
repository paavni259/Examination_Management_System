package com.example.apcproject.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.apcproject.model.Question;

@Service
public class QuestionImportService {

    public List<Question> parseFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            filename = "upload";
        }
        String lower = filename.toLowerCase();
        try {
            if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) {
                return parseExcel(file.getInputStream());
            }
            if (lower.endsWith(".csv")) {
                return parseCsv(file.getInputStream());
            }
            // Fallback: try CSV
            return parseCsv(file.getInputStream());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse file: " + ex.getMessage(), ex);
        }
    }

    private List<Question> parseCsv(InputStream in) throws Exception {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Skip header if present
                if (isFirst && looksLikeHeader(line)) {
                    isFirst = false;
                    continue;
                }
                isFirst = false;
                String[] parts = line.split(",");
                // Expecting: questionText, options (comma-separated allowed via |), correctAnswer
                if (parts.length >= 3) {
                    Question q = new Question();
                    q.setQuestionText(parts[0].trim());
                    // If options themselves contain commas, allow user to separate options by '|'
                    String options = parts[1].trim();
                    q.setOptions(options);
                    q.setCorrectAnswer(parts[2].trim());
                    questions.add(q);
                }
            }
        }
        return questions;
    }

    private boolean looksLikeHeader(String line) {
        String lower = line.toLowerCase();
        return lower.contains("question") && (lower.contains("option") || lower.contains("answer"));
    }

    private List<Question> parseExcel(InputStream in) throws Exception {
        List<Question> questions = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            boolean first = true;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (first && headerRow(row)) {
                    first = false;
                    continue;
                }
                first = false;
                Question q = new Question();
                q.setQuestionText(getCellString(row, 0));
                q.setOptions(getCellString(row, 1));
                q.setCorrectAnswer(getCellString(row, 2));
                if (q.getQuestionText() != null && !q.getQuestionText().isBlank()) {
                    questions.add(q);
                }
            }
        }
        return questions;
    }

    private boolean headerRow(Row row) {
        String a = getCellString(row, 0).toLowerCase();
        String b = getCellString(row, 1).toLowerCase();
        String c = getCellString(row, 2).toLowerCase();
        return (a.contains("question") || a.contains("text")) && (b.contains("option") || c.contains("answer"));
    }

    private String getCellString(Row row, int idx) {
        if (row == null) return "";
        Cell cell = row.getCell(idx);
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        String val = formatter.formatCellValue(cell);
        return val == null ? "" : val.trim();
    }
}


