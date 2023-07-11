package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MainPage {

    public WebDriver driver;
    public MainPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver; }

    @FindBy(xpath = "/html/body/div[2]/div/div[1]/div[1]/button")
    public WebElement runSQLbutton ;

    @FindBy(xpath = "//*[@id=\"tryitform\"]/div/div[6]")
    public WebElement inputField ;

    @FindBy(xpath = "//*[@id=\"divResultSQL\"]/div/table")
    public WebElement resultTable;

    @FindBy(xpath = "//*[@id=\"divResultSQL\"]/div/div")
    public WebElement resultCounter;

    @FindBy(xpath = "//*[@id=\"divResultSQL\"]")
    public WebElement resultInfo;

    @FindBy(id = "accept-choices")
    public WebElement acceptCookieButton;

}