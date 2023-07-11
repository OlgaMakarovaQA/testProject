package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableParser {
    private WebElement table;
    private WebDriver driver;

    public TableParser(WebElement table, WebDriver driver) {
        this.table = table;
        this.driver = driver;
    }

//  Получаем строки таблицы и удаляем строку с заголовками
    public List<WebElement> getRows() {
        List<WebElement> rows = table.findElements(By.xpath(".//tr"));
        rows.remove(0);
        return rows;
    }
    
//  Получам все заголвки таблицы заголовками
    private List<WebElement> getHeaders() {
        WebElement headersRow = table.findElement(By.xpath(".//tr[1]"));
        List<WebElement> headersColumns = headersRow.findElements(By.xpath(".//th"));
        return headersColumns;
    }

//  Получаем строки с разбиением на столбцы
    private List<List<WebElement>> getRowsWithColumns() {
        List<WebElement> rows = getRows();
        List<List<WebElement>> rowsWithColumns = new ArrayList<List<WebElement>>();
        for (WebElement row : rows) {
            List<WebElement> rowWithColumns = row.findElements(By.xpath(".//td"));
            rowsWithColumns.add(rowWithColumns);
        }
        return rowsWithColumns;
    }

//  Получаем строки и столбцы с привязкой к заголовкам
    private List<Map<String, WebElement>> getRowsAndColumnsByHeader() {
        List<List<WebElement>> rowsAndColumns = getRowsWithColumns();
        List<Map<String, WebElement>> rowsAndColumnsByHeader = new ArrayList<Map<String, WebElement>>();
        Map<String, WebElement> rowByHeader;
        List<WebElement> headerColumns = getHeaders();

        for (List<WebElement> row : rowsAndColumns) {
            rowByHeader = new HashMap<String, WebElement>();

            for (int i = 0; i < headerColumns.size(); i++) {
                String header = headerColumns.get(i).getText();
                WebElement cell = row.get(i);
                rowByHeader.put(header, cell);
            }
            rowsAndColumnsByHeader.add(rowByHeader);
        }
        return rowsAndColumnsByHeader;
    }

//  Получаем значение из ячейки таблицы по номеру строки и номеру столбца
    public String getValueFromCell(int rowNumber, int columnNumber) {
        List<List<WebElement>> rowsAndColumns = getRowsWithColumns();
        WebElement cell = rowsAndColumns.get(rowNumber - 1).get(columnNumber - 1);
        return cell.getText();
    }

//  Получаем значение из ячейки по номеру строки и заголовку столбца
    public String getValueFromCell(int rowNumber, String columnName) {
        List<Map<String, WebElement>> rowsAndColumnsByHeader = getRowsAndColumnsByHeader();
        return rowsAndColumnsByHeader.get(rowNumber - 1).get(columnName).getText();
    }

//  Получаем значение из ячейки по известному значению(textFromColumn) в другой ячейке(columnName) 
//  и по заголовку столбца искомой ячейки(expectedColumn)
    public String getValueFromCell(String textFromColumn, String columnName, String expectedColumn) {
        List<Map<String, WebElement>> rowsAndColumnsByHeader = getRowsAndColumnsByHeader();
        for (int i = 1; i <= getRows().size(); i++) {
            if ((rowsAndColumnsByHeader.get(i - 1).get(columnName).getText()).contains(textFromColumn)) {
                return rowsAndColumnsByHeader.get(i - 1).get(expectedColumn).getText();
            }
        }
        return "Value is not found";
    }
}