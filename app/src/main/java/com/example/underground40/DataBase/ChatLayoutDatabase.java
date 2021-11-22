package com.example.underground40.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.underground40.Model.ToDo;
import com.example.underground40.Model.ToDoChatLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChatLayoutDatabase extends SQLiteOpenHelper {
    public  static  ChatLayoutDatabase INSTANCE = null;

    private static final String DB_NAME = "ChatLayout";
    private static final int Version = 9;
    private static final String TABLE_NAME = "ChatLayout";

    private static final String ID_COLUMN = "ID";
    private static final String NAME_COLUMN = "name";
    private static final String DUEDATE_COLUMN = "dueDate";
    private static final String PICTURE = "picture";

    private  ChatLayoutDatabase(final Context context) {
        super(context, DB_NAME,null, Version);
    }
    public  static  ChatLayoutDatabase getInstance(final  Context context){
        if(INSTANCE == null){
            INSTANCE = new  ChatLayoutDatabase(context);
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createQuery = " CREATE TABLE "+ TABLE_NAME + " ( "+ ID_COLUMN+ " INTEGER PRIMARY KEY, "+ NAME_COLUMN+ " TEXT NOT NULL, "+ DUEDATE_COLUMN + " INTEGER DEFAULT NULL, "+PICTURE+ " TEXT DEFAULT NULL)";
        sqLiteDatabase.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropTable = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        sqLiteDatabase.execSQL(dropTable);

        onCreate(sqLiteDatabase);
    }


    public ToDo createToDo(final ToDoChatLayout todo){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME_COLUMN, todo.getName());
        values.put(DUEDATE_COLUMN, todo.getDueDate()== null ? null : todo.getDueDate().getTimeInMillis() /1000);
        values.put(PICTURE, todo.getPicture());
        long newID = database.insert(TABLE_NAME, null , values);


        return  readTodo(newID);
    }

    private ToDo readTodo(final long id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query
                (TABLE_NAME, new String[]{ID_COLUMN, NAME_COLUMN, DUEDATE_COLUMN,PICTURE}, ID_COLUMN + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        ToDo todo = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            todo = new ToDo(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
            todo.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
            Calendar calendar = null;
            if (!cursor.isNull(cursor.getColumnIndex(DUEDATE_COLUMN))) {
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(cursor.getInt(cursor.getColumnIndex(DUEDATE_COLUMN)) * 1000);

            }
            todo.setDueDate(calendar);
        }

        return  todo;
    }

    public List<ToDoChatLayout> readALLToDos(){
        List<ToDoChatLayout> toDos = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ TABLE_NAME, null);

        if (cursor.moveToFirst()){
            do{
                ToDoChatLayout toDo = readTodo(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
                if(toDo != null){
                    toDos.add(toDo);
                }
            }while (cursor.moveToNext());
        }


        return toDos;
    }

    public ToDoChatLayout updateToDo(final ToDo toDo){
        SQLiteDatabase database = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME_COLUMN, toDo.getDueDate()== null ? null :toDo.getDueDate().getTimeInMillis()/1000  );

        database.update(TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{String.valueOf(toDo.getId())});



        return this.readTodo(toDo.getId());
    }

    public  void deleteToDo(final  ToDo toDo){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, ID_COLUMN+ " = ?", new String[]{String.valueOf(toDo.getId())});

    }
    public void deleteALLToDOS(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM "+ TABLE_NAME);


    }

}
