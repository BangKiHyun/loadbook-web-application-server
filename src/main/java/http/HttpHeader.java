package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader {
    private static final Logger log = LoggerFactory.getLogger(HttpHeader.class);

    private Map<String, String> header = new HashMap<>();

    public String getHeader(String name) {
        return header.get(name);
    }

    public void add(String line) {
        putHeader(line);
    }

    private void putHeader(String line) {
        String[] tokens = line.split(": ");
        header.put(tokens[0], tokens[1]);
    }

    public int getContentLength() {
        return getIntHeader("Content-Length");
    }

    private int getIntHeader(String name) {
        String header = getHeader(name);
        return header == null ? 0 : Integer.parseInt(header);
    }

    public boolean getCookie() {
        return getBooleanHeader("Cookie");
    }

    private boolean getBooleanHeader(String name) {
        String value = getHeader(name);
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
}
