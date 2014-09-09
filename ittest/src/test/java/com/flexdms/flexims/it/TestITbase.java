package com.flexdms.flexims.it;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.flexdms.flexims.accesscontrol.ACLInitializer;
import com.flexdms.flexims.auth.PasswordHasher;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.unit.AppInitializerRule;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_NetworkedDerbyForClientAndServer_Rule;
import com.flexdms.flexims.users.FxUser;
import com.google.common.base.Function;

public class TestITbase {
	protected EntityManager em;

	// set up derby database, start database, set up EntityManagerFactory
	@ClassRule
	public static TestRule rule = RuleChain.outerRule(new CDIContainerRule())
	.around(new JPA_JAXB_NetworkedDerbyForClientAndServer_Rule()).around(new ExternalResource() {
		protected void before() throws Throwable {
			ACLInitializer.securityUnit = "fxsecuritytest";
		}
	}).around(new AppInitializerRule());

	protected static WebDriver driver;
	String password="123456";

	@BeforeClass
	public static void translateURL() {
//		if (SystemUtils.IS_OS_WINDOWS) {
//			System.setProperty("webdriver.chrome.driver", "C:\\software\\chromedriver.exe");	
//		} 
//		
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		driver.quit();
	}

	@Rule
	public EntityManagerRule entityManagerRule = new EntityManagerRule();

	public LinkedList<FleximsDynamicEntityImpl> entities = new LinkedList<>();
	FxUser user;
	
	@Before
	public void setup() throws Throwable {
		em = entityManagerRule.em;
		entities.clear();
		user = addUser("test0", password, "test0@email.com");
		entities.add(user.getUserSettings().getEntityImpl());
		entities.add(user.getEntityImpl());
	}

	@After
	public void teardown() throws Exception {
		logout();
		em.getTransaction().begin();
		while (!entities.isEmpty()) {
			em.remove(entities.pop());
		}
		em.getTransaction().commit();
	}


	public FxUser addUser(String username, String password, String email) {
		FxUser user=FxUser.createUser(em);
		user.setName(username);
		user.setEmail(email);
		user.setPassword(password);
		
		
		em.getTransaction().begin();
		em.persist(user.getEntityImpl());
		em.persist(user.getUserSettings().getEntityImpl());
		em.flush();
		PasswordHasher.hashPasswordStatic(user);
		em.getTransaction().commit();
		return user;
		
	}
	
	public void login(String email, String password) {
		driver.switchTo().activeElement();
		WebElement emailElement=driver.findElement(By.cssSelector(".login input[name=email]"));
		emailElement.clear();
		emailElement.click();
		emailElement.sendKeys(email);
		
		WebElement passwordElement=driver.findElement(By.cssSelector(".login input[name=password]"));
		passwordElement.clear();
		passwordElement.click(); //focus element first, then send key
		passwordElement.sendKeys(password);;
		
		driver.findElement(By.cssSelector(".login button")).click();
	}
	
	public void prelogin(String baseUrl) {
		driver.get(baseUrl);
		login(user.getEmail(), password);
	}
	
	public void delayGetAndDelay(String url) throws InterruptedException {
		Thread.sleep(2000);
		driver.get(url);
		Thread.sleep(2000);
	}

	public void click(String css) throws InterruptedException {
		// sleep one second to let the page stablize before click.
		Thread.sleep(2000);
		WebElement element = driver.findElement(By.cssSelector(css));
		if ("input".equals(element.getTagName())) {
			element.sendKeys("");
		} else {
			new Actions(driver).moveToElement(element).perform();

		}
		element.click();
	}
	public void logout() throws InterruptedException {
		click("button#logout");
	}
	
	public WebElement fluentWaitPresent(final By locator, int seconds) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(seconds, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class).ignoring(TimeoutException.class);

		WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(locator);
			}
		});

		return foo;
	};

}
