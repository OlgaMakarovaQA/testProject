package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@DisplayName("Tests for w3schools SQL editor")
public class SQLEditorTest {

    public static WebDriver driver;
    public static MainPage mainPage;
    public static TableParser tableParser;
    public static ChromeOptions options;
    public static WebDriverWait wait;


    @BeforeAll
    public static void setup() {
//      Создание необходимых объектов для всех тестов
        options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
//      Можно явно задать нужный драйвер в config.properties System.setProperty("webdriver.chrome.driver",
//      ConfigPropertiesReader.getProperty("chromeDriver"));
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        mainPage = new MainPage(driver);
        tableParser = new TableParser(mainPage.resultTable, driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

//      Открытие браузера и нужной страницы
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(ConfigPropertiesReader.getProperty("mainPage"));

//      Добавлено, чтобы скипать окно с куки, если есть
        if (mainPage.acceptCookieButton.isEnabled()) {
            mainPage.acceptCookieButton.click();
        }
    }

    @Test
    @DisplayName("Test for Select query")
    public void checkSelectQuery() {
//      Получение всех записей из базы
        setDataIntoField(ConfigPropertiesReader.getProperty("getAllCustomers"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultCounter));

//      Проверка что для ContactName 'Giovanni Rovelli' есть запись Address со значением 'Via Ludovico il Moro 22'
        String result = tableParser.getValueFromCell("Giovanni Rovelli", "ContactName",
                "Address");
        Assertions.assertEquals("Via Ludovico il Moro 22", result);
    }

    @Test
    @DisplayName("Test for filters query")
    public void checkQueryWithFilter() {
//      Получение записей соответствующих условию
        setDataIntoField(ConfigPropertiesReader.getProperty("getCustomersFromLondon"));
        mainPage.runSQLbutton.click();

//      Проверка, что кол-во записей совпадает с ожиданием
        Assertions.assertTrue(mainPage.resultCounter.getText().contains("Number of Records: 6"));
        Assertions.assertEquals(6, tableParser.getRows().size());
    }

    @Test
    @DisplayName("Test for query Insert")
    public void checkInsertQuery() {
//Проверить кол-во записей до добавление новой
        setDataIntoField(ConfigPropertiesReader.getProperty("getAllCustomers"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultCounter));
        Assertions.assertTrue(mainPage.resultCounter.getText().contains("Number of Records: 91"));

//Добавление новой строки
        setDataIntoField(ConfigPropertiesReader.getProperty("addNewCustomer"));
        mainPage.runSQLbutton.click();
        Assertions.assertTrue(mainPage.resultInfo.getText()
                .contains("You have made changes to the database. Rows affected: 1"));

//Проверить кол-во записей после добавление новой
        setDataIntoField(ConfigPropertiesReader.getProperty("getAllCustomers"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultCounter));
        Assertions.assertTrue(mainPage.resultCounter.getText().contains("Number of Records: 92"));

//Проверка, что строка добавлена и ее можно получить
        setDataIntoField(ConfigPropertiesReader.getProperty("getNewCustomer"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultCounter));
        Assertions.assertTrue(mainPage.resultCounter.getText().contains("Number of Records: 1"));
        String result = tableParser.getValueFromCell(1, 2);
        Assertions.assertEquals("Novak Djokovic", result);
    }

    @Test
    @DisplayName("Test for Update query")
    public void checkUpdateQuery() {
        setDataIntoField(ConfigPropertiesReader.getProperty("getAllCustomers"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultCounter));
        Assertions.assertTrue(mainPage.resultCounter.getText().contains("Number of Records: 91"));

//      Создание сущности для последующего обновления
        setDataIntoField(ConfigPropertiesReader.getProperty("insertCustomerBeforeUpdate"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultInfo));
        Assertions.assertTrue(mainPage.resultInfo.getText()
                .contains("You have made changes to the database. Rows affected: 1"));

//      Update созданной сущности
        setDataIntoField(ConfigPropertiesReader.getProperty("updateCustomer"));
        mainPage.runSQLbutton.click();
        Assertions.assertTrue(mainPage.resultInfo.getText().contains("You have made changes to the database."));

//      Получение обновленной сущности
        setDataIntoField(ConfigPropertiesReader.getProperty("getUpdatedCustomer"));
        mainPage.runSQLbutton.click();
        Assertions.assertTrue(mainPage.resultCounter.getText().contains("Number of Records: 1"));

//      Проверки измененных полей
        Assertions.assertEquals("German Gref", tableParser.getValueFromCell(1, "CustomerName"));
        Assertions.assertEquals("Mark Zuckerberg", tableParser.getValueFromCell(1, "ContactName"));
        Assertions.assertEquals("Arbat 8", tableParser.getValueFromCell(1, "Address"));
        Assertions.assertEquals("Moscow", tableParser.getValueFromCell(1, "City"));
        Assertions.assertEquals("4444", tableParser.getValueFromCell(1, "PostalCode"));
        Assertions.assertEquals("Russia", tableParser.getValueFromCell(1, "Country"));
    }

    @Test
    @DisplayName("Test for Join query")
    public void checkJoinQuery() {
        setDataIntoField(ConfigPropertiesReader.getProperty("getAllCustomers"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultCounter));
        Assertions.assertTrue(mainPage.resultCounter.getText().contains("Number of Records: 91"));

//      Создание сущности в customers
        setDataIntoField(ConfigPropertiesReader.getProperty("addNewCustomer"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultInfo));
        Assertions.assertTrue(mainPage.resultInfo.getText()
                .contains("You have made changes to the database. Rows affected: 1"));

//      Создание сущности в orders
        setDataIntoField(ConfigPropertiesReader.getProperty("addNewOrder"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultInfo));
        Assertions.assertTrue(mainPage.resultInfo.getText()
                .contains("You have made changes to the database. Rows affected: 1"));

//      Запрос из двух таблиц
        setDataIntoField(ConfigPropertiesReader.getProperty("joinCustomerAndOrder"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultCounter));
        Assertions.assertTrue(mainPage.resultCounter.getText().contains("Number of Records: 1"));

//      Проверки вернувшихся полей
        Assertions.assertEquals("Novak Djokovic", tableParser.getValueFromCell(1, "CustomerName"));
        Assertions.assertEquals("Serena Williams", tableParser.getValueFromCell(1, "ContactName"));
        Assertions.assertEquals("Alexandrou Panagioli 7", tableParser.getValueFromCell(1, "Address"));
        Assertions.assertEquals("Limassol", tableParser.getValueFromCell(1, "City"));
        Assertions.assertEquals("4106", tableParser.getValueFromCell(1, "PostalCode"));
        Assertions.assertEquals("Cyprus", tableParser.getValueFromCell(1, "Country"));
        Assertions.assertEquals("5", tableParser.getValueFromCell(1, "EmployeeID"));
        Assertions.assertEquals("2023-06-04", tableParser.getValueFromCell(1, "OrderDate"));
        Assertions.assertEquals("3", tableParser.getValueFromCell(1, "ShipperID"));

    }

    @AfterEach
    public void clearData() {
//      Удаление добавленных данных
        setDataIntoField(ConfigPropertiesReader.getProperty("deleteCustomer"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultInfo));

        setDataIntoField(ConfigPropertiesReader.getProperty("deleteCustomerAfterUpdate"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultInfo));

        setDataIntoField(ConfigPropertiesReader.getProperty("deleteOrder"));
        mainPage.runSQLbutton.click();
        wait.until(ExpectedConditions.visibilityOf(mainPage.resultInfo));
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }

    private static void setDataIntoField(String query) {
        String js_call = "window.editor.setValue(" + query + ");";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(js_call, mainPage.inputField);
    }
}