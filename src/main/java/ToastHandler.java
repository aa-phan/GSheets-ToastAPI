import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ToastHandler {

    private static final String TOAST_API_URL = "https://api.toasttab.com";
    private static final String TOAST_ACCESS_TOKEN = "your_toast_access_token";

    public JsonArray fetchShifts(String startDate, String endDate) throws IOException {
        String url = TOAST_API_URL + "/labor/v1/shifts?startDate=" + startDate + "&endDate=" + endDate;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + TOAST_ACCESS_TOKEN);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            return JsonParser.parseString(responseBody).getAsJsonArray();
        }
    }
}
