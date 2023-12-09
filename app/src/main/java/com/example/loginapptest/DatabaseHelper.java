package com.example.loginapptest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DATABASE";
    private static final int DATABASE_VERSION = 2;
    private Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void insertDHT11(float TEMPERATURE, int HUMID, String TIME) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TEMPERATURE", TEMPERATURE);
        values.put("HUMID", HUMID);
        values.put("TIME", TIME);
        db.insert("DHT11", null, values);
        db.close();
    }

    public void insertDEFAULT_WEATHER(float TEMPERATURE, int HUMID, float RAIN, float WIND_DIRECTION
            , float WIND_SPEED, String PLACE, String TIME) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TEMPERATURE", TEMPERATURE);
        values.put("HUMID", HUMID);
        values.put("RAIN", RAIN);
        values.put("WIND_DIRECTION", WIND_DIRECTION);
        values.put("WIND_SPEED", WIND_SPEED);
        values.put("PLACE", PLACE);
        values.put("TIME", TIME);
        db.insert("DEFAULT_WEATHER", null, values);
        db.close();
    }

    public void insertWEATHER(int AQI, int AQI_PREDICT, int CO2, float HUMID, float PM10, float PM25
            , String TIME) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("AQI", AQI);
        values.put("AQI_PREDICT", AQI_PREDICT);
        values.put("CO2", CO2);
        values.put("HUMID", HUMID);
        values.put("PM10", PM10);
        values.put("PM25", PM25);
        values.put("TIME", TIME);
        db.insert("WEATHER", null, values);
        db.close();
    }

    public void insertUSER(String ID, String USERNAME, String EMAIL, String FIRSTNAME, String LASTNAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID", ID);
        values.put("USERNAME", USERNAME);
        values.put("FIRSTNAME", FIRSTNAME);
        values.put("LASTNAME", LASTNAME);
        db.close();
    }

    public Cursor readAllDHT11(){
        String query = "SELECT * FROM DHT11";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Thực thi câu lệnh SQL để tạo bảng khi cơ sở dữ liệu được tạo lần đầu tiên
        String CREATE_TABLE_DHT11 =
                "CREATE TABLE DHT11 (" +
                        "TEMPERATURE FLOAT, " +
                        "HUMID INTEGER," +
                        "TIME TEXT)";
        String CREATE_TABLE_DEFAULT =
                "CREATE TABLE DEFAULT_WEATHER (" +
                        "TEMPERATURE FLOAT, " +
                        "HUMID INTEGER," +
                        "RAIN FLOAT," +
                        "WIND_DIRECTION FLOAT," +
                        "WIND_SPEED FLOAT," +
                        "PLACE TEXT," +
                        "TIME TEXT)";
        String CREATE_TABLE_WEATHER =
                "CREATE TABLE WEATHER (" +
                        "AQI INTEGER," +
                        "AQI_PREDICT INTEGER," +
                        "CO2 INTEGER," +
                        "HUMID FLOAT," +
                        "PM10 FLOAT," +
                        "PM25 FLOAT," +
                        "TIME TEXT)";
        String CREATE_TABLE_USER =
                "CREATE TABLE USER (" +
                        "ID TEXT," +
                        "USERNAME TEXT," +
                        "EMAIL TEXT," +
                        "FIRSTNAME TEXT," +
                        "LASTNAME TEXT)";
        db.execSQL(CREATE_TABLE_DHT11);
        db.execSQL(CREATE_TABLE_DEFAULT);
        db.execSQL(CREATE_TABLE_WEATHER);
        db.execSQL(CREATE_TABLE_USER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu có sự thay đổi trong cấu trúc của bảng, bạn có thể xử lý ở đây
        db.execSQL("DROP TABLE IF EXISTS DHT11");
        db.execSQL("DROP TABLE IF EXISTS DEFAULT_WEATHER");
        db.execSQL("DROP TABLE IF EXISTS WEATHER");
        db.execSQL("DROP TABLE IF EXISTS USER");
        onCreate(db);
    }

    public void DropTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        db.close();
    }
}
