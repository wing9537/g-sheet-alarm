import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleSheetClient {
    private static final String APPLICATION_NAME = "Google Sheets Alarm";
    private final String apiKey = "..."; // secret key here
    private final String spreadsheetId;
    private final Sheets service;

    public GoogleSheetClient(String spreadsheetId) throws GeneralSecurityException, IOException {
        this.spreadsheetId = spreadsheetId;
        this.service = new Sheets
            .Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), request -> {})
            .setGoogleClientRequestInitializer(request -> request.set("key", apiKey))
            .setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Call the Sheets API to get the spreadsheet data
     *
     * @param range support A1 notation, e.g. Sheet1!A:B,D:E
     * @see <a href="https://developers.google.com/sheets/api/guides/concepts#expandable-1">...</a>
     * @return 2d array of objects
     * @throws IOException
     */
    public List<List<Object>> findByRange(String range) throws IOException {
        final String sheetName = range.contains("!") ? range.split("!")[0] + "!" : "";
        final String[] ranges = range.contains("!") ? range.split("!")[1].split(",") : range.split(",");

        // Get the spreadsheet data
        final List<ValueRange> resData = service.spreadsheets().values().batchGet(spreadsheetId)
            .setRanges(Arrays.stream(ranges).map(a1 -> sheetName + a1).collect(Collectors.toList()))
            .execute().getValueRanges();

        final List<List<Object>> results = new ArrayList<>();
        for (ValueRange data : resData) {
            final List<List<Object>> cells = data.getValues();
            for (int row = 0; row < cells.size(); row++) {
                for (int x = cells.get(row).size(); x < cells.get(0).size(); x++) {
                    cells.get(row).add(""); // add empty cell
                }
                if (row >= results.size()) results.add(new ArrayList<>());
                results.get(row).addAll(cells.get(row));
            }
        }
        return results;
    }

    public Spreadsheet getSpreadsheet() throws IOException {
        return service.spreadsheets().get(spreadsheetId).execute();
    }

}
