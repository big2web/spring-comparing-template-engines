package com.jeroenreijn.examples.controller;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import benchmark.LaunchJMH;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.ModelMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PresentationsControllerTest {

	static final InputStream stream = PresentationsController.class.getClassLoader().getResourceAsStream("ExpectedOutput.html");
	static final String expectedBody = new BufferedReader(new InputStreamReader(stream)).lines().collect(joining(""));
	final LaunchJMH jmh = new LaunchJMH();

	public PresentationsControllerTest() {
		jmh.startupSpring();
	}

	@Autowired
	private PresentationsController controller;
	private ModelMap modelMap;

	@Before
	public void setUp() throws Exception {
		modelMap = new ModelMap();		
	}

	@Test
	public void should_return_jsp_view() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServerName("localhost");
		request.setRequestURI("/");
		
		String view = controller.home(request, modelMap);
		assertEquals("index-jsp", view);
	}

	@Test
	public void should_return_other_view() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServerName("localhost");
		request.setRequestURI("/test");
		
		final String view = controller.showList(request, "test", modelMap);
		assertEquals("index-test", view);
	}
	@Test
	public void should_return_view_content_for_Thymeleaf() throws Exception {
		String actual = Pattern
			.compile("\\r\\n")
			.splitAsStream(jmh.benchmarkThymeleaf())
			.map(String::trim)
			.collect(joining(""));
		assertTemplateOutput(actual, "Thymeleaf");
	}
	@Test
	public void should_return_view_content_for_Freemarker() throws Exception {
		String actual = Pattern
			.compile("\\r\\n")
			.splitAsStream(jmh.benchmarkFreemarker())
			.map(String::trim)
			.collect(joining(""));
		assertTemplateOutput(actual, "Freemarker");
	}
	@Test
	public void should_return_view_content_for_HtmlFlow() throws Exception {
		String actual = Pattern
			.compile("\\n")
			.splitAsStream(jmh.benchmarkHtmlFlow())
			.map(String::trim)
			.collect(joining(""));
		assertTemplateOutput(actual, "HtmlFlow");
	}
	@Test
	public void should_return_view_content_for_Jsp() throws Exception {
		String actual = Pattern
			.compile("\\n")
			.splitAsStream(jmh.benchmarkJsp())
			.map(String::trim)
			.collect(joining(""));
		assertTemplateOutput(actual, "JSP");
	}
	static void assertTemplateOutput(String actual, String name) {
		final Pattern MARKUP = Pattern.compile("<");
		Iterator<String> expected = MARKUP
			.splitAsStream(expectedBody.replace("JSP", name))
			.iterator();
		MARKUP
			.splitAsStream(actual)
			.forEach(act -> {
				String e = expected.next().trim();
				String a = act.trim();
				assertEquals(e, a);
			});
		assertFalse(expected.hasNext());
	}

}
