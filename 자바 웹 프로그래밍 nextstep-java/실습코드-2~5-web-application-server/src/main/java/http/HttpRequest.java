package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.IOUtils;

public class HttpRequest {

	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private HttpMethod httpMethod;

	private RequestLine requestLine;

	private HttpHeaders httpHeaders;

	private RequestParams requestParams = new RequestParams();

	public HttpRequest(InputStream in) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		try {
			this.requestLine = new RequestLine(createRequestLine(br));
			this.httpMethod = requestLine.getMethod();
			requestParams.addQueryString(requestLine.getQueryString());
			this.httpHeaders = processHeaders(br);
			this.requestParams.addBody(IOUtils.readData(br, httpHeaders.getContentLength()));
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	public String getHeader(String name) {
		return this.httpHeaders.getHeader(name);
	}

	public String getParameter(String name) {
		return this.requestParams.getParameter(name);
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	private String createRequestLine(BufferedReader br) throws IOException {
		String line = br.readLine();
		if (line == null) {
			throw new IllegalStateException();
		}
		return line;
	}

	private HttpHeaders processHeaders(BufferedReader br) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		String line;
		while ((line = br.readLine()) != null && !line.equals("")) {
			headers.add(line);
		}
		return headers;
	}

	public HttpCookie getCookies() {
		return new HttpCookie(getHeader("Cookie"));
	}

	public HttpSession getSession() {
		return HttpSessions.getSession(getCookies().getCookie("JSESSIONID"));
	}


}
