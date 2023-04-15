package com.example.banking_app_sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.Random;

public class LocalDataBaseHelper  {

    public SQLiteDatabase db;
    public Context context;

    public String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS USER_TABLE(" +
            "_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "NAME VARCHAR," +
            "LAST_NAME VARCHAR," +
            "AGE VARCHAR," +
            "GENDER VARCHAR," +
            "BALANCE VARCHAR DEFAULT -1," +
            "CARDNUMBER VARCHAR);";

    public LocalDataBaseHelper(Context c){

        this.context = c;
        this.db = context.openOrCreateDatabase("mydb",context.MODE_PRIVATE, null );
        this.db.execSQL(CREATE_USER_TABLE);
        this.db.close();

    }

    public String InsertNewUser(String name, String last_name, String age, String gender ){

        this.db = context.openOrCreateDatabase("mydb", context.MODE_PRIVATE, null);
        String cardNumber = GenerateCardNumber();

        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Last Name", last_name);
        values.put("Age", age);
        values.put("Gender", gender);
        values.put("Card Number", cardNumber);

        long r = this.db.insert("USER_TABLE","_ID", values);

        this.db.close();
        
        if ( r != -1 ){
            Toast.makeText(context, " New User Inserted! ", Toast.LENGTH_SHORT).show();
            return cardNumber;
        } else{
            Toast.makeText(context, " Error while inserting new User ", Toast.LENGTH_SHORT).show();
            return "";
        }

    }

    public String GenerateCardNumber(){
        String cardnum;
        Random rand = new Random();
        int number = rand.nextInt(999999);
        cardnum =  String.format("%06",number);

        return cardnum;
    }

    public String login(String cardNumber){

        this.db = context.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);

        Cursor cursor = this.db.rawQuery(" SELECT * FROM USER_TABLE WHERE CARDNUMBER = " + cardNumber, null);

        if(cursor.getCount() == 0){
            Toast.makeText(context, "Wrong Card Number", Toast.LENGTH_SHORT).show();
            this.db.close();
            return "";
        } else {
            this.db.close();
            return cardNumber;
        }

    }

    @SuppressLint("Range")
    public String getUserBalance(String cardNumber){

        String balance;
        this.db = context.openOrCreateDatabase("mydb",context.MODE_PRIVATE, null);

        Cursor cursor = this.db.rawQuery("SELECT * FROM USER_TABLE WHERE CARDNUMBER = " + cardNumber, null);
        cursor.moveToFirst();
        balance = cursor.getString(cursor.getColumnIndex("BALANCE"));

        this.db.close();
        return balance;

    }

    public Boolean topUp(String cardNumber, String amount){
        boolean done = false;

        float currentAmount = Float.parseFloat(this.getUserBalance(cardNumber));

        this.db = context.openOrCreateDatabase("mydb",context.MODE_PRIVATE, null);

        float newAmount = currentAmount + Float.parseFloat(amount);
        ContentValues values = new ContentValues();
        values.put("BALANCE",String.valueOf(newAmount));

        int r = this.db.update("USER_TABLE",values,"CARDNUMBER = ?", new String[]{cardNumber});

        if( r > 0){
            done = true;
        } else{
            done = false;
            Toast.makeText(context, "Something went wrong! ", Toast.LENGTH_SHORT).show();
        }

        this.db.close();
        return done;

    }

    public Boolean UpdateUserBalance(String cardNumber, String ammount){
        boolean done = false;

        this.db = context.openOrCreateDatabase("mydb", Context.MODE_PRIVATE, null);

        ContentValues values = new ContentValues();
        values.put("BALANCE", ammount);

        int r = this.db.update("USER_TABLE",values,"CARDNUMBER = ?", new String[]{cardNumber});

        if( r > 0){
            done = true;
        } else {
            done = false;
            Toast.makeText(context, "Something went wrong! ", Toast.LENGTH_SHORT).show();
        }

        this.db.close();
        return done;

    }

    public void transferMoney(String cardNumber, String targetCardNumber, String amount){

        this.db = context.openOrCreateDatabase("mydb",Context.MODE_PRIVATE, null);

        float sourceBalance = Float.parseFloat(this.getUserBalance(cardNumber));
        float targetBalance = Float.parseFloat(this.getUserBalance(targetCardNumber));
        Float transferAmount = Float.parseFloat(amount);

        if(sourceBalance >= transferAmount){

            sourceBalance = sourceBalance - transferAmount;
            targetBalance = targetBalance + transferAmount;

            if(UpdateUserBalance(cardNumber,String.valueOf(sourceBalance))){

                if(UpdateUserBalance(targetCardNumber,String.valueOf(targetBalance))){
                    Toast.makeText(context, "Transfer is done! ", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Error while updating account! ", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(context, "Error while updating account! ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Insufficient Balance! ", Toast.LENGTH_SHORT).show();
        }

        this.db.close();
    }

}
