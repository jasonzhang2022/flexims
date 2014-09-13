package com.flexdms.flexims.it;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.OrderDirection;
import com.flexdms.flexims.query.OrderField;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.unit.rs.report.helper.QueryUtil;
import com.google.common.base.Function;

@RunWith(Arquillian.class)
@RunAsClient
public class TestRelationEditorIT extends TestITbase {

	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		WebArchive webArchive = ArchiveUtil.buildRsWebArchive(null);
		return webArchive;
	}

	@ArquillianResource
	protected URL baseURL;

	private String baseUrl;
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = null;
	int sleepSecond = 2;
	int waitSecond = 10;

	@Before
	public void setupEach() {
		baseUrl = baseURL.toExternalForm() + "index.html";
		verificationErrors = new StringBuffer();
	}

	@After
	public void tearDownEach() throws Exception {
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}

	}

	public WebElement waitOptions(final int total, final String css) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(waitSecond, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class).ignoring(TimeoutException.class);

		return wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement element = driver.findElement(By.cssSelector(css));
				if (element == null) {
					return element;
				}
				if (new Select(element).getOptions().size() == total) {
					return element;
				}

				return null;
			}
		});

	}

	

	@Test
	public void demoRelationEditor() throws SQLException, InterruptedException {

		com.flexdms.flexims.users.unit.DataUtil.createTestUsers(em);

		// Prepare Data for demo purpose.
		DataUtil.studentForRelationEditor(em);

		// search room by build: create query
		ConditionQuery query = QueryUtil.createSimpleQuery("Mdoomroom", "doombuild", Operator.eq, em);
		QueryUtil.saveQuery(query, em);

		// create report.
		FleximsDynamicEntityImpl reportEntityImpl = JpaHelper.createNewEntity(em, FxReportWrapper.TYPE_NAME);
		FxReportWrapper wrapper = new FxReportWrapper();
		wrapper.setEntity(reportEntityImpl);
		wrapper.setQuery(query);
		wrapper.setName("Room By Build");
		wrapper.setProperties("name", "number");
		wrapper.setOrderBy(Arrays.asList(new OrderField("name", OrderDirection.ASC)));
		wrapper.addParamValue("2"); // build Kmart
		em.getTransaction().begin();
		em.persist(reportEntityImpl);
		em.getTransaction().commit();

		// course by college
		ConditionQuery cquery = QueryUtil.createSimpleQuery("Mcourse", "college", Operator.eq, em);
		QueryUtil.saveQuery(cquery, em);

		// create report.
		FleximsDynamicEntityImpl courseReport = JpaHelper.createNewEntity(em, FxReportWrapper.TYPE_NAME);
		FxReportWrapper courseWrapper = new FxReportWrapper();
		courseWrapper.setEntity(courseReport);
		courseWrapper.setQuery(cquery);
		courseWrapper.setName("Course By College");
		courseWrapper.setProperties("name");
		courseWrapper.setOrderBy(Arrays.asList(new OrderField("name", OrderDirection.ASC)));
		courseWrapper.addParamValue("Arts"); // build Kmart
		em.getTransaction().begin();
		em.persist(courseReport);
		em.getTransaction().commit();

		// login as admin to manipulate type property
		delayGetAndDelay(baseUrl);
		login("testadmin@example.com", "123456");

		WebElement element;

		// --------------------------All build is shown as selection box.
		delayGetAndDelay(baseUrl + "#/relationui/Mstudent/doombuild");
		Thread.sleep(sleepSecond*1000);
		new Select(driver.findElement(By.cssSelector("select#relationuireport"))).selectByIndex(1);
		click("#relationuiform .panel .btn-toolbar button"); // save

		// go to add student.
		delayGetAndDelay(baseUrl + "#/addinst/Mstudent");

		// we have a select element instead of an input
		assertThat(new Select(driver.findElement(By.cssSelector(".prop.doombuild select"))).getOptions(), hasSize(3));
		// -------------------------------end of section one:

		// --------------Doomroom uses a specific report with fixed param value
		delayGetAndDelay(baseUrl + "#/relationui/Mstudent/doomroom");
		// a specific report.
		new Select(driver.findElement(By.cssSelector("select#relationuireport"))).selectByVisibleText(wrapper.getName());
		click("#relationuiform .panel .btn-toolbar button");

		delayGetAndDelay(baseUrl + "#/addinst/Mstudent");
		element = waitOptions(201, ".prop.doomroom select");
		// sorted correctly as is in report.
		assertThat(new Select(element).getOptions().get(1).getText(), equalTo("name200"));

		// ------------------------------Doomroom uses a specific report with
		// parameter value from an input field.
		delayGetAndDelay(baseUrl + "#/relationui/Mstudent/doomroom");
		// room options depend on build in the same form.
		new Select(driver.findElements(By.cssSelector("select")).get(2)).selectByVisibleText("doombuild");
		click("#relationuiform .panel .btn-toolbar button");

		delayGetAndDelay(baseUrl + "#/addinst/Mstudent");
		try {
			driver.findElement(By.cssSelector(".prop.doomroom select"));
			fail("no Select element is query is not ready");
		} catch (NoSuchElementException e) {

		}

		// select build
		new Select(driver.findElement(By.cssSelector(".prop.doombuild select"))).selectByVisibleText("Porter");
		element = waitOptions(201, ".prop.doomroom select");
		assertThat(new Select(element).getOptions().get(1).getText(), equalTo("name0"));

		new Select(driver.findElement(By.cssSelector(".prop.doombuild select"))).selectByVisibleText("Kmart");
		element = waitOptions(201, ".prop.doomroom select");
		assertThat(new Select(element).getOptions().get(1).getText(), equalTo("name200"));

		// ----------------------room options changes as inline table.
		delayGetAndDelay(baseUrl + "#/relationui/Mstudent/doomroom");
		// inline table.
		new Select(driver.findElement(By.cssSelector("select#uitype"))).selectByIndex(1);
		click("#relationuiform .panel .btn-toolbar button");

		delayGetAndDelay(baseUrl + "#/addinst/Mstudent");
		new Select(driver.findElement(By.cssSelector(".prop.doombuild select"))).selectByVisibleText("Porter");
		Thread.sleep(sleepSecond * 1000); // let the table loading
		assertThat(driver.findElement(By.cssSelector(".ngRow")).findElement(By.cssSelector(".ngCellText")).getText(), equalTo("name0"));

		new Select(driver.findElement(By.cssSelector(".prop.doombuild select"))).selectByVisibleText("Kmart");
		Thread.sleep(3 * 1000); // give time to switch
		assertThat(driver.findElement(By.cssSelector(".ngRow")).findElement(By.cssSelector(".ngCellText")).getText(), equalTo("name200"));

		// select one to make sure we can select
		click(".ngRow input[type=checkbox]");
		assertThat(driver.findElements(By.cssSelector("div.selected > a")), hasSize(1));
		assertThat(driver.findElement(By.cssSelector("div.selected > a")).getText(), equalTo("name200"));

		driver.findElements(By.cssSelector(".ngRow")).get(1).findElement(By.cssSelector("input[type=checkbox]")).click();
		assertThat(driver.findElement(By.cssSelector("div.selected > a")).getText(), equalTo("name201"));

		// ----------------------room options changes as popup table.
		delayGetAndDelay(baseUrl + "#/relationui/Mstudent/doomroom");
		// popup table
		new Select(driver.findElement(By.cssSelector("select#uitype"))).selectByIndex(2);
		click("#relationuiform .panel .btn-toolbar button");

		delayGetAndDelay(baseUrl + "#/addinst/Mstudent");
		new Select(driver.findElement(By.cssSelector(".prop.doombuild select"))).selectByVisibleText("Porter");
		click(".prop.doomroom .editor button");
		driver.switchTo().activeElement();
		Thread.sleep(sleepSecond * 1000);
		assertThat(driver.findElement(By.cssSelector(".ngRow")).findElement(By.cssSelector(".ngCellText")).getText(), equalTo("name0"));
		// dismiss popup
		click(".modal-footer button");
		// wait for modal dialog to disappear
		Thread.sleep(100);

		new Select(driver.findElement(By.cssSelector(".prop.doombuild select"))).selectByVisibleText("Kmart");
		driver.switchTo().activeElement();
		click(".prop.doomroom .editor button");
		driver.switchTo().activeElement();
		assertThat(driver.findElement(By.cssSelector(".ngRow")).findElement(By.cssSelector(".ngCellText")).getText(), equalTo("name200"));

		// select one to make sure
		click(".ngRow input[type=checkbox]");
		assertThat(driver.findElements(By.cssSelector("div.selected > a")), hasSize(1));
		assertThat(driver.findElement(By.cssSelector("div.selected > a")).getText(), equalTo("name200"));

		driver.findElements(By.cssSelector(".ngRow")).get(1).findElement(By.cssSelector("input[type=checkbox]")).click();
		assertThat(driver.findElement(By.cssSelector("div.selected > a")).getText(), equalTo("name201"));
		// dismiss popup
		click(".modal-footer button");
		// wait for modal dialog to disappear
		Thread.sleep(100);

		// save instance
		Thread.sleep(100);
		click(".btn-toolbar  button");
		Thread.sleep(sleepSecond * 1000);
		String viewurl = driver.getCurrentUrl();
		assertThat(viewurl, containsString("viewinst"));

		// edit instance
		click(".btn-toolbar  a:first-child");
		Thread.sleep(sleepSecond * 1000);
		String editurl = driver.getCurrentUrl();
		assertThat(editurl, containsString("editinst"));

		assertThat(new Select(driver.findElement(By.cssSelector(".prop.doombuild select"))).getFirstSelectedOption().getText(), equalTo("Kmart"));
		assertThat(driver.findElement(By.cssSelector("div.selected > a")).getText(), equalTo("name201"));
		click(".prop.doomroom .editor button");
		driver.switchTo().activeElement();
		Thread.sleep(sleepSecond * 1000);
		assertTrue(driver.findElements(By.cssSelector("input[type=checkbox]")).get(1).isSelected());
		// dismiss popup
		click(".modal-footer button");
		// wait for modal dialog to disappear
		Thread.sleep(100);

		//-----------------------many selection
		//multi selection for mCourse
		delayGetAndDelay(baseUrl + "#/relationui/Mstudent/Courses");
		// a specific report.
		new Select(driver.findElement(By.cssSelector("select#relationuireport"))).selectByVisibleText(courseWrapper.getName());
		// inline table.
		new Select(driver.findElement(By.cssSelector("select#uitype"))).selectByIndex(1);
		
		click("#relationuiform .panel .btn-toolbar button");
		
		//-----------------multi selection for OneManys
		delayGetAndDelay(baseUrl + "#/relationui/Mstudent/OneManys");
		// All report
		new Select(driver.findElement(By.cssSelector("select#relationuireport"))).selectByIndex(1);
		// multi selection.
		new Select(driver.findElement(By.cssSelector("select#uitype"))).selectByIndex(0);
		click("#relationuiform .panel .btn-toolbar button");
		delayGetAndDelay(editurl);
		//more sleep to make sure two reports are loaded
		Thread.sleep(sleepSecond*1000);
		
		WebElement courseElement=driver.findElement(By.cssSelector(".prop.Courses"));
		courseElement.findElements(By.cssSelector(".ngRow")).get(0).findElement(By.cssSelector("input[type=checkbox]")).click();
		courseElement.findElements(By.cssSelector(".ngRow")).get(1).findElement(By.cssSelector("input[type=checkbox]")).click();
		assertThat(courseElement.findElements(By.cssSelector("div.selected > a")), hasSize(2));
		
		
		WebElement oneManyElement=driver.findElement(By.cssSelector(".prop.OneManys"));
		Select oneManySelect=new Select(oneManyElement.findElement(By.cssSelector("select")));
		oneManySelect.selectByIndex(0);
		oneManySelect.selectByIndex(1);
		assertThat(oneManyElement.findElements(By.cssSelector("div.selected > a")), hasSize(2));
		assertThat(oneManySelect.getAllSelectedOptions(), hasSize(2));
		//save
		click(".btn-toolbar  button");
		//make sure selection state is correct after save and edit
		delayGetAndDelay(viewurl);
		delayGetAndDelay(editurl);
		
		courseElement=driver.findElement(By.cssSelector(".prop.Courses"));
		assertThat(courseElement.findElements(By.cssSelector("div.selected > a")), hasSize(2));
		assertTrue(courseElement.findElements(By.cssSelector(".ngRow")).get(0).findElement(By.cssSelector("input[type=checkbox]")).isSelected());
		assertTrue(courseElement.findElements(By.cssSelector(".ngRow")).get(1).findElement(By.cssSelector("input[type=checkbox]")).isSelected());
		
		oneManyElement=driver.findElement(By.cssSelector(".prop.OneManys"));
		assertThat(oneManyElement.findElements(By.cssSelector("div.selected > a")), hasSize(2));
		oneManySelect=new Select(oneManyElement.findElement(By.cssSelector("select")));
		assertThat(oneManySelect.getAllSelectedOptions(), hasSize(2));
		
		
	}

}
