package ApplicationPages;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.spec.SecretKeySpec;

import org.junit.AfterClass;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import Resources.AESUtils;
import Resources.base;
import pageObjects.HomePage;
import pageObjects.LandingPage;
import pageObjects.LoginPage;

public class HomePageTest extends base {
	public WebDriver driver;
	public static Logger log = LogManager.getLogger(base.class.getName());

	LoginPage login;
	LandingPage landing;
	HomePage homePage;
	

	@BeforeTest

	public void setup() throws IOException, InterruptedException, GeneralSecurityException {
		driver = initializeDriver();
		login = new LoginPage(driver);
		landing = new LandingPage(driver);
		homePage = new HomePage(driver);
		

		driver.get(prop.getProperty("url"));
		landing.getLogin();
		String password=prop.getProperty("originalPassword");
		
		 // The salt (probably) can be stored along with the encrypted data
        byte[] salt = "12345678".getBytes();

        // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
        int iterationCount = 40000;
        // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
        int keyLength = 128;
        SecretKeySpec key = AESUtils.createSecretKey(prop.getProperty("originalPassword").toCharArray(),
                salt, iterationCount, keyLength);

       /* I am displaying Original password just for reference, this should be avoided in coding as main purpose of encrypting is 
        not to display the actual password */
        System.out.println("Original password is: " + password);
        
       /* You can run this once and get the encrypted password and store it in config.properties file */
        String encryptedPassword = AESUtils.encrypt(password, key);
        System.out.println("Encrypted password is: " + encryptedPassword);
        
        String decryptedPassword = AESUtils.decrypt(prop.getProperty("encryptedPassword"), key);
        System.out.println("Decrypted password is: " + decryptedPassword);
        
        login.loginsuccess(prop.getProperty("emailid"), decryptedPassword);
		log.info(" User logged in successfully");
        
    }
	
	@Test

	public void ValidateTheHeaderOptions() {

		homePage.ValidateMenuTabs();
	}

	@Test

	public void ValidateWomenShoppingPageisDisplayed() {
		homePage.ValidateWomenMenuisclicable();
		homePage.ValidateWomenPageIsDisplayed();
		log.info("The user is navigated to women shopping list");
	}

	@AfterMethod

	public void takeScreenShotOnFailed(ITestResult result) throws IOException {

		String testMethodName = result.getMethod().getMethodName();
		if (result.getStatus() == ITestResult.FAILURE) {
			getScreenShotPath(testMethodName, driver);
		}
	}

	@AfterTest
	public void teardown() {
		driver.close();
	}



   
    

}

