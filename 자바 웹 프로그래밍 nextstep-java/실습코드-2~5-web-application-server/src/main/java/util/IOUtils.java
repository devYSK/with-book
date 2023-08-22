package util;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.RequestHandler;

public class IOUtils {
    private static final Logger log = LoggerFactory.getLogger(IOUtils.class);
    /**
     * @param BufferedReader는 Request Body를 시작하는 시점이어야
     * @param contentLength는  Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader BufferedReader, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        BufferedReader.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

}
