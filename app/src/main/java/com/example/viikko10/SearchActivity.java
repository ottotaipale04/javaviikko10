package com.example.viikko10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SearchActivity extends AppCompatActivity {

    private EditText cityName;
    private EditText yearInput;
    private Button searchInfo;
    private Button listInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cityName = findViewById(R.id.CityNameEdit);
        yearInput = findViewById(R.id.YearEdit);
        searchInfo = findViewById(R.id.SearchButton);
        listInfo = findViewById(R.id.ListInfoActivityButton);
    }

    public void switchToListInfo(View view) {
        Intent intent = new Intent(this, ListInfoActivity.class);
        startActivity(intent);
    }
}