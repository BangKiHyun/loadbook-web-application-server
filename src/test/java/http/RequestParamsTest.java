package http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestParamsTest {

    @Test
    public void put_queryString() {
        RequestParams params = new RequestParams();
        params.addQueryString("userId=bang&password=1234&name=hyun");
        assertEquals("bang", params.getParams("userId"));
    }
}
