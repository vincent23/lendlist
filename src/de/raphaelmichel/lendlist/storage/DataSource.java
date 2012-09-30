package de.raphaelmichel.lendlist.storage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {
	private SQLiteDatabase database;
	private Database dbHelper;
	private Context ctx;

	public DataSource(Context context) {
		dbHelper = new Database(context);
		ctx = context;
	}

	public void openWritable() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void open() throws SQLException {
		database = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void addItem(Item item) {
		ContentValues values = new ContentValues();
		values.put("direction", item.getDirection());
		values.put("thing", item.getThing());
		values.put("person", item.getPerson());
		values.put("contact_id", item.getContact_id());
		values.put("until", (item.getUntil() != null ? item.getUntil().getTime() : 0));
		database.insert("objects", null, values);
	}

	public void updateItem(Item item) {
		ContentValues values = new ContentValues();
		values.put("direction", item.getDirection());
		values.put("thing", item.getThing());
		values.put("person", item.getPerson());
		values.put("contact_id", item.getContact_id());
		values.put("until", (item.getUntil() != null ? item.getUntil().getTime() : 0));
		String[] selA = { item.getId()+"" };
		database.update("objects", values, "id = ?", selA);
	}

	public List<Item> getAllItems(String direction) {
		List<Item> items = new ArrayList<Item>();
		String[] selA = { direction };
		Cursor cursor = database.query("objects", Database.COLUMNS,
				"direction = ?", selA, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return items;
	}

	private Item cursorToItem(Cursor cursor) {
		Item item = new Item();
		item.setId(cursor.getLong(0));
		item.setDirection(cursor.getString(1));
		item.setThing(cursor.getString(3));
		item.setPerson(cursor.getString(4));
		item.setContact_id(cursor.getLong(5));
		try {
			item.setUntil(new SimpleDateFormat(
					ctx.getString(R.string.date_format)).parse(cursor.getString(6)));
		} catch (ParseException e) {
			item.setUntil(null);
			e.printStackTrace();
		}
		return item;
	}
}