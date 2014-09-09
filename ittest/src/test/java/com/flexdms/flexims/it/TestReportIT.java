package com.flexdms.flexims.it;

import static org.junit.Assert.fail;

import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityTransaction;

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
import org.openqa.selenium.support.ui.Wait;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.helper.NameValueList;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.PropertyCondition;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.unit.rs.report.helper.QueryUtil;
import com.flexdms.flexims.util.Utils;
import com.google.common.base.Function;

@RunWith(Arquillian.class)
@RunAsClient
public class TestReportIT extends TestITbase {

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
	static int sleepSecond = 1;
	static int waitSecond = 10;

	@Before
	public void setupEach() {
		baseUrl = baseURL.toExternalForm() + "index.html";
		verificationErrors = new StringBuffer();
		prelogin(baseUrl);
	}

	@After
	public void tearDownEach() throws Exception {
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
	
	public void viewReport(long id){
		//switch to another page first so that old report DOM page is flushed, 
		//to avoid the StaleElementReference issue.
		driver.get(baseUrl+"#/addinst/Basictype"); 	
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		driver.get(baseUrl + "#/report/" + id);
		System.out.println("--------" + baseUrl + "#/report/" + id);
	}
	
	public void verifyTotal(final int total) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(waitSecond, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class).ignoring(TimeoutException.class);

		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				WebElement element=driver.findElement(By.cssSelector(".ngFooterTotalItems span:first-child"));
				if (element==null) {
					return element;
				}
				String text=element.getText();
				int actualTotal=Integer.parseInt(text.substring("Total Items: ".length()));
				if (actualTotal!=total) {
					verificationErrors.append("Expected total is "+total+" but got "+actualTotal+"\n");
				}
				
				return element;
			}
		});

		
	}

	@Test
	public void testBasictypeSingleOperator() throws InterruptedException {
		String type = "Basictype";

		em.getTransaction().begin();
		QueryUtil.deleteAllReport(em);
		QueryUtil.deleteAllQuery(em);
		QueryUtil.deleteData(type, em);
		em.getTransaction().commit();

		DataUtil.prepareBasictype(em);

		ConditionQuery query = null;
		FxReportWrapper report = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar calendar1 = (Calendar) calendar.clone();
		calendar1.add(Calendar.DAY_OF_MONTH, 1);

		// -----------single int
		query = QueryUtil.createSimpleQuery(type, "propint", Operator.ge, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl").addParamValue("50");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		
		verifyTotal(450);

		// --------------multi ints
		Thread.sleep(sleepSecond * 1000);
		query = QueryUtil.createSimpleQuery(type, "propint", Operator.oneof, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl").addParamValue("50", "51", "52");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(3);

		// -------single date
		Thread.sleep(sleepSecond * 1000);
		query = QueryUtil.createSimpleQuery(type, "propdate", Operator.between, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl")
				.addParamValue(Utils.dateToString(calendar.getTime())).addParamValue(Utils.dateToString(calendar1.getTime()));
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(47);

		// -------single date: relative
		Thread.sleep(sleepSecond * 1000);
		query = QueryUtil.createSimpleQuery(type, "propdate", Operator.between, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl").addParamValue("-3:WEEK:true")
				.addParamValue("-2:WEEK:true");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);
		viewReport(report.getEntity().getId());
		//one week worth entity: 7*24
		verifyTotal(192);

		// -------multiple date
		Thread.sleep(sleepSecond * 1000);
		query = QueryUtil.createSimpleQuery(type, "propdate", Operator.oneof, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl").addParamValue(
				Utils.dateToString(calendar.getTime()), Utils.dateToString(calendar1.getTime()));
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(47);

		// -------single String
		Thread.sleep(sleepSecond * 1000);
		query = QueryUtil.createSimpleQuery(type, "shortstring", Operator.like, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl").addParamValue("jason1%");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(111);

		// -------multiple String
		Thread.sleep(sleepSecond * 1000);
		query = QueryUtil.createSimpleQuery(type, "shortstring", Operator.oneof, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl").addParamValue("jason11",
				"jason21");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(2);

		// -------single time
		Calendar from=Calendar.getInstance();
		from.set(Calendar.YEAR, 1970);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DATE, 1);
		from.set(Calendar.HOUR_OF_DAY, 5);
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);
		
		Calendar to=(Calendar) from.clone();
		to.set(Calendar.HOUR_OF_DAY, 6);
		
		Thread.sleep(sleepSecond * 1000);
		query = QueryUtil.createSimpleQuery(type, "proptime", Operator.between, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl")
				.addParamValue(Utils.dateToString(from)).addParamValue(Utils.dateToString(to));
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(21);

		from.set(Calendar.MINUTE, 30);
		to.set(Calendar.MINUTE, 30);
		// -------multiple time
		Thread.sleep(sleepSecond * 1000);
		query = QueryUtil.createSimpleQuery(type, "proptime", Operator.oneof, em);
		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl").addParamValue(
				Utils.dateToString(from), Utils.dateToString(to));
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(42);

	}

	@Test
	public void testCollectionSingleOperator() throws InterruptedException {
		String type = "Collection1";

		em.getTransaction().begin();
		QueryUtil.deleteAllReport(em);
		QueryUtil.deleteAllQuery(em);
		QueryUtil.deleteData(type, em);
		em.getTransaction().commit();

		DataUtil.prepareCollection1type(em);

		ConditionQuery query = null;
		FxReportWrapper report = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar calendar1 = (Calendar) calendar.clone();
		calendar1.add(Calendar.DAY_OF_MONTH, 1);

		// -----------single int
		query = QueryUtil.createSimpleQuery(type, "propint", Operator.ge, em);
		query.getConditions().get(0).setAllColllectionMode();
		report = QueryUtil.prepareSimpleReport(query, em, null, "shortstring", "propint", "propdate", "propurl").addParamValue("498");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(2);

		// -----------single string
		query = QueryUtil.createSimpleQuery(type, "shortstring", Operator.like, em);
		query.getConditions().get(0).setAllColllectionMode();
		report = QueryUtil.prepareSimpleReport(query, em, null, "shortstring", "propint", "propdate", "propurl").addParamValue("jason11%");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(9);

		// -----------single string
		query = QueryUtil.createSimpleQuery(type, "shortstring", Operator.like, em);
		query.getConditions().get(0).setSomeColllectionMode();
		report = QueryUtil.prepareSimpleReport(query, em, null, "shortstring", "propint", "propdate", "propurl").addParamValue("jason11%");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(13);

		// -----------single date
		query = QueryUtil.createSimpleQuery(type, "propdate", Operator.between, em);
		query.getConditions().get(0).setAllColllectionMode();
		report = QueryUtil.prepareSimpleReport(query, em, null, "shortstring", "propint", "propdate", "propurl")
				.addParamValue(Utils.dateToString(calendar.getTime())).addParamValue(Utils.dateToString(calendar1.getTime()));
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		
		verifyTotal(46);

		// -----------single time
		query = QueryUtil.createSimpleQuery(type, "proptime", Operator.between, em);
		query.getConditions().get(0).setAllColllectionMode();
		Calendar from=Calendar.getInstance();
		from.set(Calendar.YEAR, 1970);
		from.set(Calendar.MONTH, 0);
		from.set(Calendar.DATE, 1);
		from.set(Calendar.HOUR_OF_DAY, 5);
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);
		from.set(Calendar.MILLISECOND, 0);
		
		Calendar to=(Calendar) from.clone();
		to.set(Calendar.HOUR_OF_DAY, 7);
		report = QueryUtil.prepareSimpleReport(query, em, null, "shortstring", "propint", "propdate", "propurl").addParamValue(Utils.dateToString(from))
				.addParamValue(Utils.dateToString(to));
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		verifyTotal(21);
	}

	@Test
	public void testBasictypeTwoOperator() {
		String type = "Basictype";

		em.getTransaction().begin();
		QueryUtil.deleteAllReport(em);
		QueryUtil.deleteAllQuery(em);
		QueryUtil.deleteData(type, em);
		em.getTransaction().commit();

		DataUtil.prepareBasictype(em);

		ConditionQuery query = null;
		FxReportWrapper report = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar calendar1 = (Calendar) calendar.clone();
		calendar1.add(Calendar.DAY_OF_MONTH, 1);

		// -----------single int
		query = QueryUtil.createSimpleQuery(type, "propint", Operator.ge, em);
		PropertyCondition shortstring = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
		shortstring.setProp("shortstring");
		shortstring.setOperator(Operator.like);
		query.getConditions().add(shortstring);

		report = QueryUtil.prepareSimpleReport(query, em, "propint", "shortstring", "propint", "propdate", "propurl").addParamValue("150")
				.addParamValue("jason1%");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		verifyTotal(50);

	}

	@Test
	public void testCollection1TwoOperator() {
		String type = "Collection1";

		em.getTransaction().begin();
		QueryUtil.deleteAllReport(em);
		QueryUtil.deleteAllQuery(em);
		QueryUtil.deleteData(type, em);
		em.getTransaction().commit();

		DataUtil.prepareCollection1type(em);

		ConditionQuery query = null;
		FxReportWrapper report = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar calendar1 = (Calendar) calendar.clone();
		calendar1.add(Calendar.DAY_OF_MONTH, 1);

		// -----------single int
		query = QueryUtil.createSimpleQuery(type, "propint", Operator.lt, em);
		query.getConditions().get(0).setAllColllectionMode();

		PropertyCondition shortstring = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
		shortstring.setProp("shortstring");
		shortstring.setOperator(Operator.like);
		shortstring.setSomeColllectionMode();
		query.getConditions().add(shortstring);

		report = QueryUtil.prepareSimpleReport(query, em, null, "shortstring", "propint", "propdate", "propurl").addParamValue("200")
				.addParamValue("jason1%");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		// TODO check the logic here
		verifyTotal(113);

	}

	

	@Test
	public void testTracedown() throws SQLException {
		String qType = "Mdoombuild";
		String propName = "students";
		String relatedTypeString = "Mstudent";
		em.getTransaction().begin();
		QueryUtil.deleteAllReport(em);
		QueryUtil.deleteAllQuery(em);
		em.getTransaction().commit();
		DataUtil.setOneManyTrace(em);
		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		WebElement totalElement;
		ConditionQuery query = null;
		PropertyCondition pc = null;
		ConditionQuery studentNameAndStudentProptin = null;
		PropertyCondition datePcCondition = null;

		// Query on student
		// 1: name like
		// 2. all propint (a collection) >
		studentNameAndStudentProptin = QueryUtil.createSimpleQuery(relatedTypeString, "Name", Operator.like, em);
		datePcCondition = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), studentNameAndStudentProptin);
		datePcCondition.setProp("propint");
		datePcCondition.setOperator(Operator.gt);
		datePcCondition.setAllColllectionMode();
		studentNameAndStudentProptin.getConditions().add(datePcCondition);

		// build trace down to students
		query = QueryUtil.createSimpleQuery(qType, propName, Operator.tracedown, em);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentNameAndStudentProptin);
		pc.setAllColllectionMode(); // ALL COllection mode.

		FxReportWrapper report = QueryUtil.prepareSimpleReport(query, em, "name", "name", "students", "id").addParamValue("%2").addParamValue("30");
		entities.add(studentNameAndStudentProptin.getEntity());
		entities.add(query.getEntity());
		entities.add(report.getEntity());

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		QueryUtil.saveQueryInternal(studentNameAndStudentProptin, em);
		QueryUtil.saveQueryInternal(query, em);
		em.persist(report.getEntity());
		tx.commit();

		viewReport(report.getEntity().getId());
		totalElement = driver.findElement(By.cssSelector(".ngFooterTotalItems span:first-child"));
		// TODO check the logic here
		verifyTotal(1);
	}

	

	/**
	 * Order by single embedded field. property in embedded field. property in
	 * multi embedded field.
	 */
	@Test
	public void testEmbedded() {

		String type = "Embedmain";
		em.getTransaction().begin();
		QueryUtil.deleteAllReport(em);
		QueryUtil.deleteAllQuery(em);
		QueryUtil.deleteByQuery("Embedmain", em);
		em.getTransaction().commit();
		em.getTransaction().begin();
		DataUtil.setupEmbedmain(em);
		em.getTransaction().commit();

		ConditionQuery query = null;
		FxReportWrapper report = null;
		
		// -----------regular
		query = QueryUtil.createSimpleQuery(type, "fname", Operator.like, em);
		report = QueryUtil.prepareSimpleReport(query, em, "fname", "fname", "singleembed", "multiembed").addParamValue("main%0");
		entities.add(query.getEntity());
		entities.add(report.getEntity());
		QueryUtil.saveReport(report, em);

		viewReport(report.getEntity().getId());
		verifyTotal(3);

		// TODO nested property display

		// tracedown
		ConditionQuery subQuery = QueryUtil.createSimpleQuery("Embed1", "mint", Operator.gt, em);

		query = QueryUtil.createSimpleQuery(type, "fname", Operator.like, em);
		PropertyCondition pc = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
		pc.setProp("singleembed");
		pc.setOperator(Operator.tracedown);
		pc.setTracceDown(subQuery);
		pc.setSomeColllectionMode();
		query.getConditions().add(pc);

		// with name end 0 and single embedded with mint >10: main10, main20, do
		// not have main0
		report = QueryUtil.prepareSimpleReport(query, em, "fname", "fname", "singleembed", "multiembed").addParamValue("main%0").addParamValue("10");
		NameValueList list = new NameValueList();
		list.addPair("rowHeight", "70");
		report.getEntity().set("gridOptions", list);

		entities.add(subQuery.getEntity());
		entities.add(query.getEntity());
		entities.add(report.getEntity());

		em.getTransaction().begin();
		QueryUtil.saveQueryInternal(subQuery, em);
		QueryUtil.saveQueryInternal(query, em);
		em.persist(report.getEntity());
		em.getTransaction().commit();

		viewReport(report.getEntity().getId());
		verifyTotal(2);

	}

}
