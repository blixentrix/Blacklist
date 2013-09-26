package sk.pixel.blacklist;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactSearcher {

	private static final Object HAS_PHONE = "1";
	private Context context;

	public ContactSearcher(Context context) {
		this.context = context;
	}

	public Map<String, ArrayList<String>> searchByName(String name) {
		String where = "DISPLAY_NAME like ?";
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, where,
				new String[] { "%" + name + "%" }, null);
		Map<String, ArrayList<String>> result = new TreeMap<String, ArrayList<String>>();
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String hasPhone = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			String contactName = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			if (hasPhone.equals(HAS_PHONE)) {
				Cursor phones = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				ArrayList<String> numbers = new ArrayList<String>();
				while (phones.moveToNext()) {
					String phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					numbers.add(phoneNumber);
				}
				phones.close();
				result.put(contactName, numbers);
			}
		}
		return result;
	}

	public boolean existsNumber(String number) {
		String where = "DATA1 like ? ";
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				where, new String[] { "%" + number + "%" }, null);
		
		return cursor.getCount() > 0;
	}
}
