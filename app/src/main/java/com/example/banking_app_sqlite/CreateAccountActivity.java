package com.example.banking_app_sqlite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity {

    EditText nameEt, lNameEt, ageEt;
    RadioGroup genderRg ;
    String name, lName, age, gender, cardNumber;
    CardView createBtn;
    LocalDataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        findAllViews();

        genderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.maleRb){
                    gender = "male";
                }else{
                    gender = "female";
                }
            }
        });
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get user data
                name = nameEt.getText().toString().trim();
                lName = lNameEt.getText().toString().trim();
                age = ageEt.getText().toString().trim();

                //call insert new account method

                String cardNumber = db.InsertNewUser(name,lName,age,gender);

                if(!cardNumber.equals("")){
                    showAlertDialog(cardNumber);
                }
                else{
                    Toast.makeText(CreateAccountActivity.this, "Error See card Number section", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
    public void findAllViews(){
        nameEt = findViewById(R.id.nameEt);
        lNameEt = findViewById(R.id.lnameEt);
        ageEt = findViewById(R.id.ageEt);
        genderRg = findViewById(R.id.genderRg);
        createBtn = findViewById(R.id.signUpBtn);
        db = new LocalDataBaseHelper(this);

    }
    public void showAlertDialog(String cardNumber){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("User created");
        alertDialog.setMessage("Your Card number is: " + cardNumber);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();

    }

}