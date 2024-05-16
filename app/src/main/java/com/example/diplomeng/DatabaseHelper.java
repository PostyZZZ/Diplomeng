package com.example.diplomeng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 3; // Увеличим версию базы данных
    private static final String COLUMN_AVATAR_PATH = "avatar_path";


    // Таблица пользователей
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Таблица избранных слов
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_FAVORITE_ID = "id";
    private static final String COLUMN_USER_ID_FK = "user_id";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_TRANSLATION = "translation";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_AVATAR_PATH + " TEXT)"; // Добавляем столбец для пути к аватару
        db.execSQL(createUserTableQuery);



    // Создаем таблицу избранных слов
        String createFavoritesTableQuery = "CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_WORD + " TEXT, " +
                COLUMN_TRANSLATION + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createFavoritesTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаляем старые таблицы, если они есть
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        // Создаем новые таблицы
        onCreate(db);
    }

    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public boolean addFavorite(int userId, String word, String translation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_ID_FK, userId); // Используйте полученный userId
        contentValues.put(COLUMN_WORD, word);
        contentValues.put(COLUMN_TRANSLATION, translation);
        long result = db.insert(TABLE_FAVORITES, null, contentValues);
        return result != -1;
    }


    public List<String> getFavoritesByUserId(int userId) {
        List<String> favoritesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + COLUMN_USER_ID_FK + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(cursor.getColumnIndex(COLUMN_WORD));
                String translation = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSLATION));
                favoritesList.add(word + " - " + translation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoritesList;
    }

    public boolean deleteFavorite(int userId, String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_FAVORITES, COLUMN_USER_ID_FK + " = ? AND " + COLUMN_WORD + " = ?", new String[]{String.valueOf(userId), word});
        return deletedRows > 0;
    }

    public boolean checkUser(String emailOrUsername, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE (" +
                COLUMN_EMAIL + " = ? OR " +
                COLUMN_USERNAME + " = ?) AND " +
                COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{emailOrUsername, emailOrUsername, password});
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    public String getUsernameByEmailOrUsername(String emailOrUsername) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_EMAIL + " = ? OR " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{emailOrUsername, emailOrUsername});
        String username = null;
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
        }
        cursor.close();
        return username;
    }

    public String getEmailByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_EMAIL + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        String email = null;
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
        }
        cursor.close();
        return email;
    }

    public int getUserIdByEmailOrUsername(String emailOrUsername) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_EMAIL + " = ? OR " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{emailOrUsername, emailOrUsername});
        int userId = -1; // Инициализируем userId -1 в случае, если пользователя не найдено
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
        }
        cursor.close();
        return userId;
    }
    public boolean updateAvatarPath(int userId, String avatarPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AVATAR_PATH, avatarPath);
        int affectedRows = db.update(TABLE_USERS, contentValues, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return affectedRows > 0;
    }
    public String getAvatarPathByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_AVATAR_PATH + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        String avatarPath = null;
        if (cursor.moveToFirst()) {
            avatarPath = cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR_PATH));
        }
        cursor.close();
        return avatarPath;
    }

}

