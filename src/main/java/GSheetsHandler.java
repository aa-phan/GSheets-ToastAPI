import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.JsonArray;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GSheetsHandler {

    private static final String APPLICATION_NAME = "Toast to Google Sheets";
    private static final String SPREADSHEET_ID = "May-1 to May-15";
    private static final String SHEET_NAME = "Sheet1";
    private Sheets sheetsService;
    private ToastHandler toastHandler;

    public GSheetsHandler() throws GeneralSecurityException, IOException {
        sheetsService = getSheetsService();
        toastHandler = new ToastHandler();
    }

    private Sheets getSheetsService() throws GeneralSecurityException, IOException {
        FileInputStream serviceAccountStream = new FileInputStream("path/to/credentials.json");
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void updateGoogleSheetWithShifts(String startDate, String endDate) throws IOException, InterruptedException {
        JsonArray shifts = toastHandler.fetchShifts(startDate, endDate);

        // Process and combine data
        // Example: Update Google Sheets with shifts data

        ValueRange body = new ValueRange()
                .setValues(List.of(
                        List.of("Employee Name", "Hours Worked", "Tips Earned")
                        // Add more rows here
                ));

        sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, SHEET_NAME + "!A1", body)
                .setValueInputOption("RAW")
                .execute();
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException, InterruptedException {
        GSheetsHandler gSheetsHandler = new GSheetsHandler();
        gSheetsHandler.updateGoogleSheetWithShifts("2023-05-13", "2023-05-13");
    }
}
