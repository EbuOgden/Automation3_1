import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Automation3_1 {
  static public WebDriver driver;

  public static void main(String[] args) throws InterruptedException {

    System.setProperty("webdriver.chrome.driver", "/Users/ebu/Desktop/SEL/chromedriver");

    String website = "https://www.cars.com";
    String expectedSelectedOptionStockType = "New & used cars";
    String expectedSelectedOptionMakeType = "All makes";
    String expectedSelectedOptionModelType = "All models";
    String expectedSelectedOptionPrice = "No max price";
    String expectedSelectedOptionDistance = "20 miles";

    String[] newUsedDropDownExpectedValues = {"New & used cars", "New & certified cars", "New cars", "Used cars", "Certified cars"};
    String[] modelsDropDownExpectedValues = {"All models", "Model 3", "Model S", "Model X", "Model Y", "Roadster"};
    String expectedTeslaModels = "Tesla Model S";

    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.get(website);

    Select stockTypeSelects = new Select(getElementByXPath("//select[@id='make-model-search-stocktype']"));
    Select makeTypeSelects = new Select(getElementByXPath("//select[@id='makes']"));
    Select modelTypeSelects = new Select(getElementByXPath("//select[@id='models']"));
    Select maxPriceTypeSelects = new Select(getElementByXPath("//select[@id='make-model-max-price']"));
    Select distanceTypeSelects = new Select(getElementByXPath("//select[@id='make-model-maximum-distance']"));


    String selectedStockType = getFirstSelectedOption(stockTypeSelects);
    String selectedMakeType = getFirstSelectedOption(makeTypeSelects);
    String selectedModelType = getFirstSelectedOption(modelTypeSelects);
    String selectedMaxPrice = getFirstSelectedOption(maxPriceTypeSelects);
    String selectedDistance = getFirstSelectedOption(distanceTypeSelects);

    Assert.assertEquals(expectedSelectedOptionStockType, selectedStockType);
    Assert.assertEquals(expectedSelectedOptionMakeType, selectedMakeType);
    Assert.assertEquals(expectedSelectedOptionModelType, selectedModelType);
    Assert.assertEquals(expectedSelectedOptionPrice, selectedMaxPrice);
    Assert.assertEquals(expectedSelectedOptionDistance, selectedDistance);

    List<WebElement> stockTypeSelectList = stockTypeSelects.getOptions();

    for(int i = 0; i < stockTypeSelectList.size(); i++){
      Assert.assertEquals(stockTypeSelectList.get(i).getText(), newUsedDropDownExpectedValues[i]);
    }

    stockTypeSelects.selectByIndex(3);
    makeTypeSelects.selectByValue("tesla");

    Thread.sleep(randNumber(500, 729));

    modelTypeSelects = new Select(getElementByXPath("//select[@id='models']"));

    List<WebElement> modelTypeSelectList = modelTypeSelects.getOptions();

    for(int i = 0; i < modelTypeSelectList.size(); i++){
      Assert.assertEquals(modelTypeSelectList.get(i).getText(), modelsDropDownExpectedValues[i]);
    }

    modelTypeSelects.selectByIndex(2);
    Thread.sleep(randNumber(300, 400));
    maxPriceTypeSelects.selectByValue("100000");
    Thread.sleep(randNumber(300, 400));
    distanceTypeSelects.selectByValue("50");
    Thread.sleep(randNumber(300, 400));
    clearElementById("make-model-zip");
    Thread.sleep(randNumber(300, 400));
    sendKeysElementById("make-model-zip", "22182");
    Thread.sleep(randNumber(300, 400));

    clickElementByXPath("//button[@data-searchtype='make']");

    Thread.sleep(randNumber(3005, 4000));

    List<WebElement> searchResults = getElementsByXPath("//div[@data-tracking-type='srp-vehicle-card']");

    Thread.sleep(randNumber(333, 555));

    Assert.assertEquals(String.valueOf(searchResults.size()), "20");

    Select sortSelects = new Select(getElementById("sort-dropdown"));
    List<Integer> carPrices = new ArrayList<>();
    List<Integer> carMiles = new ArrayList<>();
    List<Integer> carDistance = new ArrayList<>();
    List<Integer> carYears = new ArrayList<>();

    Thread.sleep(randNumber(444, 667));

    for(WebElement eachSearchResult : searchResults){
      String carTitle = eachSearchResult.findElement(By.className("vehicle-card-visited-tracking-link")).getAttribute("aria-label");
      Assert.assertTrue(carTitle.contains(expectedTeslaModels));
    }

    // check low to high price sorting

    sortSelects.selectByIndex(1);

    Thread.sleep(randNumber(1500, 3400));

    searchResults = getElementsByXPath("//div[@data-tracking-type='srp-vehicle-card']");

    Thread.sleep(randNumber(643, 994));

    for(WebElement eachSearchResult : searchResults) {
      Integer currentCarPrice = Integer.valueOf(eachSearchResult.findElement(By.className("primary-price")).getText().split("\\$")[1].replace(",", ""));
      carPrices.add(currentCarPrice);
    }

    Collections.sort(carPrices);

    for(int i = 0; i < carPrices.size(); i++){
      Integer currentCarPrice = Integer.valueOf(searchResults.get(i).findElement(By.className("primary-price")).getText().split("\\$")[1].replace(",", ""));
      Assert.assertEquals(currentCarPrice, carPrices.get(i));
    }

    /*              */

    // check highest mileage

    Thread.sleep(randNumber(1500, 3400));

    sortSelects.selectByIndex(4);

    Thread.sleep(randNumber(1500, 3400));

    searchResults = getElementsByXPath("//div[@data-tracking-type='srp-vehicle-card']");

    Thread.sleep(randNumber(643, 994));

    for(WebElement eachSearchResult : searchResults) {
      Integer currentCarMile = Integer.valueOf(eachSearchResult.findElement(By.className("mileage")).getText().split(" ")[0].replace(",", ""));
      carMiles.add(currentCarMile);
    }

    Collections.sort(carMiles, Collections.reverseOrder());

    for(int i = 0; i < carMiles.size(); i++){
      Integer currentCarMile = Integer.valueOf(searchResults.get(i).findElement(By.className("mileage")).getText().split(" ")[0].replace(",", ""));
      Assert.assertEquals(currentCarMile, carMiles.get(i));
    }

    /*              */

    // check nearest location

    Thread.sleep(randNumber(1500, 3400));

    sortSelects.selectByIndex(5);

    Thread.sleep(randNumber(1500, 3400));

    searchResults = getElementsByXPath("//div[@data-tracking-type='srp-vehicle-card']");

    Thread.sleep(randNumber(643, 994));

    for(WebElement eachSearchResult : searchResults) {
      String distance = eachSearchResult.findElement(By.cssSelector(".miles-from, .online-seller")).getText();
      if(!distance.equals("Online seller")) {
        carDistance.add(Integer.valueOf(distance.split(" ")[0]));
      }
    }

    Collections.sort(carDistance);

    for(int i = 0; i < carDistance.size(); i++){
      String distance = searchResults.get(i).findElement(By.cssSelector(".miles-from, .online-seller")).getText();
      if(!distance.equals("Online seller")) {
        Assert.assertEquals(Integer.valueOf(distance.split(" ")[0]), carDistance.get(i));
      }
    }

    /*          */

    // check oldest year

    Thread.sleep(randNumber(1500, 3400));

    sortSelects.selectByIndex(8);

    Thread.sleep(randNumber(1500, 3400));

    searchResults = getElementsByXPath("//div[@data-tracking-type='srp-vehicle-card']");

    Thread.sleep(randNumber(643, 994));

    for(WebElement eachSearchResult : searchResults) {
      String carTitle = eachSearchResult.findElement(By.className("vehicle-card-visited-tracking-link")).getAttribute("aria-label");

      carYears.add(Integer.valueOf(carTitle.split(" ")[0]));

    }

    Collections.sort(carYears);

    for(int i = 0; i < carYears.size(); i++){
      String carTitle = searchResults.get(i).findElement(By.className("vehicle-card-visited-tracking-link")).getAttribute("aria-label");
      Assert.assertEquals(Integer.valueOf(carTitle.split(" ")[0]), carYears.get(i));
    }

    /*          */

    driver.quit();

  }

  public static void clickElementByXPath(String xPath){
    driver.findElement(By.xpath(xPath)).click();
  }

  public static void clearElementById(String element){
    driver.findElement(By.id(element)).clear();
  }

  public static void sendKeysElementById(String element, String keys){
    driver.findElement(By.id(element)).sendKeys(keys);
  }

  public static WebElement getElementById(String element){
    return driver.findElement(By.id(element));
  }

  public static WebElement getElementByXPath(String element){
    return driver.findElement(By.xpath(element));
  }

  public static List<WebElement> getElementsByXPath(String element){
    return driver.findElements(By.xpath(element));
  }

  public static String getFirstSelectedOption(Select selectElement){
    return selectElement.getFirstSelectedOption().getText();
  }

  public static int randNumber(int min, int max) {
    return (int) ((Math.random() * (max - min)) + min);
  }

}

