package com.gn4k.dailybites;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class InboxReader {

    private Context context;

    public InboxReader(Context context) {
        this.context = context;
    }

    public Cursor getInboxMessages() {
        Uri inboxUri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"_id", "address", "date", "body"};
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(inboxUri, projection, null, null, null);
        return cursor;
    }

    public String getMessageBody(Cursor cursor) {
        int bodyIndex = cursor.getColumnIndex("body");
        if (bodyIndex >= 0) {
            return cursor.getString(bodyIndex);
        } else {
            // Handle the case when the "body" column is not found in the cursor.
            return "";
        }
    }

    public String getSender(Cursor cursor) {
        int addressIndex = cursor.getColumnIndex("address");
        if (addressIndex >= 0) {
            return cursor.getString(addressIndex);
        } else {
            // Handle the case when the "address" column is not found in the cursor.
            return "";
        }
    }

    public long getMessageDate(Cursor cursor) {
        int dateIndex = cursor.getColumnIndex("date");
        if (dateIndex >= 0) {
            return cursor.getLong(dateIndex);
        } else {
            // Handle the case when the "date" column is not found in the cursor.
            return 0; // Or any other appropriate default value for the date.
        }
    }

}
