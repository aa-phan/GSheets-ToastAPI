import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsHandler {

    private static final String APPLICATION_NAME = "Google Sheets API Java";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/path/to/client_secret.json";

    private Sheets sheetsService;

    public GoogleSheetsHandler() throws GeneralSecurityException, IOException {
        sheetsService = getSheetsService();
    }

    private Sheets getSheetsService() throws GeneralSecurityException, IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CREDENTIALS_FILE_PATH));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singletonList(SheetsScopes.SPREADSHEETS))
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void updateGoogleSheet(String spreadsheetId, String range, List<List<Object>> values) throws IOException {
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values().update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        GoogleSheetsHandler handler = new GoogleSheetsHandler();
        // Replace with your spreadsheet ID and range
        String spreadsheetId = "your_spreadsheet_id";
        String range = "Sheet1!A1";
        List<List<Object>> values = List.of(
                List.of("Employee Name", "Hours Worked", "Tips Earned"),
                List.of("John Doe", "8", "100"),
                List.of("Jane Doe", "7", "120")
        );
        handler.updateGoogleSheet(spreadsheetId, range, values);
    }
}
