package http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestLineTest {

    @Test
    public void create_method(){
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        assertEquals("GET", String.valueOf(line.getMethod()));
        assertEquals("/index.html", String.valueOf(line.getPath()));

        line = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals("/index.html", String.valueOf(line.getPath()));
    }

    @Test
    public void create_queryString(){
        RequestLine line = new RequestLine("GET /user/create?userId=bang&password=1234&name=hyun HTTP/1.1");
        assertEquals("userId=bang&password=1234&name=hyun", line.getQueryString());
    }
}
