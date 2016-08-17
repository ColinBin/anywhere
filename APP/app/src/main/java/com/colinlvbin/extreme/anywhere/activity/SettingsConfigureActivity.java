package com.colinlvbin.extreme.anywhere.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.colinlvbin.extreme.anywhere.MyApplication;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;

import java.util.Locale;

public class SettingsConfigureActivity extends AppCompatActivity implements View.OnClickListener{

    private Button confirmButton;
    private Button cancelButton;
    private Spinner languageChoicesSpinner;
    private String selectedLanguage=null;
    private ArrayAdapter<String> languageAdapter;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_configure);
        confirmButton=(Button)findViewById(R.id.confirm_button_configure_settings);
        cancelButton=(Button)findViewById(R.id.cancel_button_configure_settings);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        languageChoicesSpinner=(Spinner)findViewById(R.id.language_choices_configure_settings);
        String[] languages=getResources().getStringArray(R.array.languages);
        languageAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageChoicesSpinner.setAdapter(languageAdapter);
        languageChoicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] languages=getResources().getStringArray(R.array.languages);
                selectedLanguage=languages[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.confirm_button_configure_settings:
                if(selectedLanguage!=null){
                    sharedPreferences=getSharedPreferences("app_config", MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences
                            .edit();
                    if(selectedLanguage.equals("English")){
                        ((MyApplication)getApplication()).SetLanguage(Locale.ENGLISH);
                        editor.putString("language","en");
                    }else{
                        ((MyApplication)getApplication()).SetLanguage(Locale.SIMPLIFIED_CHINESE);
                        editor.putString("language","zh");
                    }
                    editor.apply();
                    RoutineOps.MakeToast(this,getResources().getString(R.string.restart_app));
                }
                finish();
                break;
            case R.id.cancel_button_configure_settings:
                finish();
                break;

        }
    }

}
