package com.expense.expensetracker.service.impl;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.Category;
import com.expense.expensetracker.entity.Expense;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.repository.ExpenseRepository;
import com.expense.expensetracker.service.CategoryService;
import com.expense.expensetracker.service.ExpenseService;
import com.expense.expensetracker.service.UserService;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Expense createExpense(Expense expense, Long userId) {
        User user = userService.getUserById(userId);
        expense.setUser(user);
        expense.setDate(LocalDate.now());
        // Validate that category is provided and not null
        if (expense.getCategory() == null || expense.getCategory().getId() == null) {
            throw new RuntimeException("Category is required to create an expense");
        }
        Category category = categoryService.getCategoryById(expense.getCategory().getId());
        expense.setCategory(category);
        return expenseRepository.save(expense);
    }

    @Override
    public Expense updateExpense(Long expenseId, Expense updatedExpense) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setTitle(updatedExpense.getTitle());
        expense.setDescription(updatedExpense.getDescription());
        expense.setAmount(updatedExpense.getAmount());
        // Validate that updated category is provided
        if (updatedExpense.getCategory() == null || updatedExpense.getCategory().getId() == null) {
            throw new RuntimeException("Category is required to update an expense");
        }
        if (!Objects.equals(expense.getCategory().getId(), updatedExpense.getCategory().getId())) {
            Category category = categoryService.getCategoryById(updatedExpense.getCategory().getId());
            expense.setCategory(category);
        }
        return expenseRepository.save(expense);

    }

    @Override
    public void deleteExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found!"));
        expenseRepository.delete(expense);
    }

    @Override
    public Expense getExpenseById(Long expenseId) {
        return expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    @Override
    public List<Expense> getAllExpensesByUser(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    @Override
    public List<Expense> getExpensesByDateRange(Long userId, LocalDate start, LocalDate end) {
        return expenseRepository.findByUserIdAndDateBetween(userId, start, end);
    }

    @Override
    public List<Expense> getExpensesByCategory(Long userId, Long categoryId) {
        return expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    public List<Expense> getExpensesByAmountRange(Long userId, double minAmount, double maxAmount) {
        return expenseRepository.findByUserIdAndAmountBetween(userId, minAmount, maxAmount);
    }

    @Override
    public List<Expense> searchExpenses(Long userId, String keyword) {
        return expenseRepository.search(userId, keyword);
    }

    @Override
    public List<Expense> getExpensesSortedByDateInDesc(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    public List<Expense> getExpensesSortedByAmount(Long userId) {
        return expenseRepository.findByUserIdOrderByAmountAsc(userId);
    }

    @Override
    public List<Expense> getExpensesSortedByCategory(Long userId) {
        return expenseRepository.findByUserIdOrderByCategoryNameAsc(userId);
    }

    @Override
    public Double getTotalExpenseForMonth(Long userId, int month, int year) {
        return expenseRepository.getMonthlyExpenseTotal(userId, month, year);
    }

    @Override
    public Map<String, Double> getTotalExpenseByCategory(Long userId, int month, int year) {
        List<Object[]> results = expenseRepository.getTotalExpenseByCategory(userId, month, year);
        Map<String, Double> map = new HashMap<>();
        if (results != null) {
            for (Object[] row : results) {
                if (row != null && row.length >= 2) {
                    String category = row[0] != null ? row[0].toString() : "Unknown";
                    Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                    map.put(category, total);
                }
            }
        }
        return map;
    }

    @Override
    public Map<LocalDate, Double> getDailyExpenseSummary(Long userId, int month, int year) {
        List<Object[]> results = expenseRepository.getDailyExpenseSummary(userId, month, year);
        Map<LocalDate, Double> map = new HashMap<>();
        if (results != null) {
            for (Object[] row : results) {
                if (row != null && row.length >= 2) {
                    LocalDate date = row[0] instanceof LocalDate ? (LocalDate) row[0]
                            : LocalDate.parse(row[0].toString());
                    Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                    map.put(date, total);
                }
            }
        }
        return map;
    }

    @Override
    public Map<Integer, Double> getMonthlyExpenseSummary(Long userId, int year) {
        List<Object[]> results = expenseRepository.getMonthlyExpenseSummary(userId, year);
        Map<Integer, Double> map = new HashMap<>();
        if (results != null) {
            for (Object[] row : results) {
                if (row != null && row.length >= 2) {
                    Integer month = row[0] instanceof Number ? ((Number) row[0]).intValue()
                            : Integer.parseInt(row[0].toString());
                    Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                    map.put(month, total);
                }
            }
        }
        return map;
    }

    @Override
    public void exportExpensesToCSV(PrintWriter writer) {

        List<Expense> expenses = expenseRepository.findAll();

        writer.println("Title,Amount,Category,Date,Description");

        for (Expense e : expenses) {
            writer.println(
                    e.getTitle() + "," +
                            e.getAmount() + "," +
                            e.getCategory().getName() + "," +
                            e.getDate() + "," +
                            e.getDescription());
        }

        writer.flush();
    }

    /*
     * @Override
     * public byte[] exportExpensesToPDF(Long userId) {
     * 
     * List<Expense> expenses = expenseRepository.findByUserId(userId);
     * 
     * try {
     * ByteArrayOutputStream out = new ByteArrayOutputStream();
     * Document document = new Document();
     * PdfWriter.getInstance(document, out);
     * 
     * document.open();
     * 
     * Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
     * document.add(new Paragraph("Expense Report", titleFont));
     * document.add(new Paragraph("User ID: " + userId));
     * document.add(new Paragraph("Generated On: " + LocalDate.now()));
     * document.add(Chunk.NEWLINE);
     * 
     * PdfPTable table = new PdfPTable(5);
     * table.setWidthPercentage(100);
     * 
     * table.addCell("Title");
     * table.addCell("Amount");
     * table.addCell("Category");
     * table.addCell("Type");
     * table.addCell("Date");
     * 
     * for (Expense exp : expenses) {
     * table.addCell(exp.getTitle());
     * table.addCell(String.valueOf(exp.getAmount()));
     * table.addCell(exp.getCategory().getName());
     * // .addCell(exp.getType().name());
     * table.addCell(exp.getDate().toString());
     * }
     * 
     * document.add(table);
     * document.close();
     * 
     * return out.toByteArray();
     * 
     * } catch (Exception e) {
     * throw new RuntimeException("PDF generation failed", e);
     * }
     * }
     * 
     * /*
     * 
     * @Override
     * public byte[] exportExpensesToExcel(Long userId) {
     * 
     * List<Expense> expenses = expenseRepository.findByUserId(userId);
     * 
     * try (Workbook workbook = new XSSFWorkbook()) {
     * 
     * Sheet sheet = workbook.createSheet("Expenses");
     * 
     * Row header = sheet.createRow(0);
     * header.createCell(0).setCellValue("Title");
     * header.createCell(1).setCellValue("Amount");
     * header.createCell(2).setCellValue("Category");
     * header.createCell(3).setCellValue("Type");
     * header.createCell(4).setCellValue("Date");
     * 
     * int rowIdx = 1;
     * for (Expense exp : expenses) {
     * Row row = sheet.createRow(rowIdx++);
     * row.createCell(0).setCellValue(exp.getTitle());
     * row.createCell(1).setCellValue(exp.getAmount());
     * row.createCell(2).setCellValue(exp.getCategory().getName());
     * //row.createCell(3).setCellValue(exp.getType().name());
     * row.createCell(4).setCellValue(exp.getDate().toString());
     * }
     * 
     * ByteArrayOutputStream out = new ByteArrayOutputStream();
     * workbook.write(out);
     * return out.toByteArray();
     * 
     * } catch (Exception e) {
     * throw new RuntimeException("Excel export failed", e);
     * }
     * }
     */ @Override
    public List<Expense> filterExpenses(Long userId, String keyword, Integer month, Integer year, Long categoryId) {

        List<Expense> result = expenseRepository.findByUserId(userId);

        if (keyword != null && !keyword.isEmpty()) {
            result = result.stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(keyword.toLowerCase())
                            || e.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }

        if (month != null) {
            result = result.stream()
                    .filter(e -> e.getDate().getMonthValue() == month)
                    .toList();
        }

        if (year != null) {
            result = result.stream()
                    .filter(e -> e.getDate().getYear() == year)
                    .toList();
        }

        if (categoryId != null) {
            result = result.stream()
                    .filter(e -> e.getCategory() != null && e.getCategory().getId().equals(categoryId))
                    .toList();
        }

        return result;
    }

    @Override
    public boolean isCategoryBudgetExceeded(Long userId, Long categoryId, int month, int year) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCategoryBudgetExceeded'");
    }

    @Override
    public boolean isMonthlyBudgetExceeded(Long userId, int month, int year) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isMonthlyBudgetExceeded'");
    }

    @Override
    public void generateRecurringExpenses(Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateRecurringExpenses'");
    }

    @Override
    public List<Expense> getByMonth(Long userId, int month, int year) {
        return expenseRepository.getByMonth(userId, month, year);
    }

}
