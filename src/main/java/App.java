import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class App {
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        // build a new authorized API client service
        if (args.length == 0) return;
        final GoogleSheetClient client = new GoogleSheetClient(args[0]);
        System.out.println("The script start at " + new Date());

        List<List<Object>> configs = client.findByRange("Settings!B:B");
        if (configs == null || configs.isEmpty()) return;

        final String sheetName = configs.get(0).get(0).toString().trim();
        final String[] expColumns = configs.get(1).get(0).toString().trim().split(",");
        final String dateFormat = configs.get(2).get(0).toString().trim();
        final String[] daysBefore = configs.get(3).get(0).toString().trim().split(",");
        final String receivers = configs.get(4).get(0).toString().trim();

        System.out.println("expColumns: " + Arrays.toString(expColumns));
        System.out.println("daysBefore: " + Arrays.toString(daysBefore));
        System.out.println("receivers: " + receivers);

        final List<List<Object>> records = client.findByRange(sheetName);
        final int[] expIndexes = Arrays.stream(expColumns).mapToInt(col -> records.get(0).indexOf(col.trim())).toArray();
        if (Arrays.stream(expIndexes).anyMatch(index -> index == -1)) return; // check if any expiry column not exists

        // check expiry soon records
        final Date today = DateUtils.getToday();
        final List<List<Object>> results = new ArrayList<>(Collections.singletonList(records.get(0)));
        for (int i = 1; i < records.size(); i++) {
            final List<Object> cols = records.get(i);
            final StringBuilder result = new StringBuilder();
            for (int expIndex : expIndexes) {
                final String dateString = cols.get(expIndex).toString().trim();
                final Date expDate = DateUtils.stringToDate(dateString, dateFormat);
                if (expDate == null) continue; // skip if date invalid

                final long days = DateUtils.daysBetween(today, expDate);
                if (Arrays.stream(daysBefore).anyMatch(d -> days == Long.parseLong(d.trim()))) {
                    cols.set(expIndex, "<span style='color:red'>" + dateString + "</span>");
                    result.append(result.length() == 0 ? days : ", " + days);
                }
            }
            // add to results if match
            if (result.length() > 0) {
                cols.add(result.toString());
                results.add(cols);
            }
        }
        results.get(0).add("Days Remain"); // add extra header
        results.forEach(System.out::println);

        // sending emails
        if (results.size() > 1) {
            StringBuilder content = new StringBuilder();
            // link to target spreadsheet
            final Spreadsheet spreadsheet = client.getSpreadsheet();
            content.append("Google Sheet: <a href='" + spreadsheet.getSpreadsheetUrl() + "'>");
            content.append(spreadsheet.getProperties().getTitle());
            content.append("</a><br/>");

            // table view for display expiry items
            results.forEach(cols -> {
                content.append("<tr style='text-align:center'>");
                cols.forEach(col -> content.append("<td style='border:1px solid; padding:5px'>" + col + "</td>"));
                content.append("</tr>");
            });
            new EmailHandler().sendEmail(receivers, content.toString());
            System.out.println("Email sent: " + content);
        } else {
            System.out.println("No records found. Skip to send email.");
        }
        System.out.println("The script end at " + new Date());
    }
}
