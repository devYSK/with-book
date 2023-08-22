package http;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;

import org.junit.Test;

public class HttpRequestTest{
	private String testDirectory = "./src/test/resources/";

	@Test
	public void request_GET() throws Exception {
		InputStream in = Files.newInputStream(new File(testDirectory +
			"Http_GET.txt").toPath());
		HttpRequest request = new HttpRequest(in);
		assertEquals(HttpMethod.GET, request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi", request.getParameter("userId"));
	}

	@Test
	public void request_POST() throws Exception {
		InputStream in = Files.newInputStream(new File(testDirectory + "Http_POST.txt").toPath());
		HttpRequest request = new HttpRequest(in);

		assertEquals(HttpMethod.POST, request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi", request.getParameter("userId"));
	}

	@Test
	public void request_POST2() throws Exception {
		InputStream in = Files.newInputStream(new File(testDirectory + "Http_POST2.txt").toPath());
		HttpRequest request = new HttpRequest(in);

		assertEquals(HttpMethod.POST, request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("1", request.getParameter("id"));
		assertEquals("javajigi", request.getParameter("userId"));
	}

}