package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

import java.io.*;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine;
    private HttpHeader headers;
    private RequestParams params = new RequestParams();

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            requestLine = new RequestLine(createRequestLine(br));
            params.addQueryString(requestLine.getQueryString());
            headers = processHeader(br);
            params.addBody(IOUtils.readData(br, headers.getContentLength()));

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String createRequestLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) {
            throw new NullPointerException(line + "request line Is null");
        }
        return line;
    }

    private HttpHeader processHeader(BufferedReader br) throws IOException {
        HttpHeader header = new HttpHeader();

        String line = br.readLine();
        while (!line.equals("")) {
            log.debug("request header : {}", line);
            header.add(line);
            line = br.readLine();
        }

        return header;
    }

    public boolean isLogin() {
        return headers.getCookie();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return headers.getHeader(name);
    }

    public String getParams(String name) {
        return params.getParams(name);
    }
}
