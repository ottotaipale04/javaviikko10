package com.example.viikko10;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListInfoActivity extends AppCompatActivity {

    private TextView cityText, yearText, carInfoText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cityText = findViewById(R.id.CityText);
        yearText = findViewById(R.id.YearText);
        carInfoText = findViewById(R.id.CarInfoText);

        CarDataStorage storage = CarDataStorage.getInstance();

        cityText.setText(storage.getCity());

        yearText.setText(String.valueOf(storage.getYear()));

        StringBuilder s = new StringBuilder();

        int total = 0;
        for (CarData data : storage.getCarData()) {
            s.append(data.getType()).append(": ").append(data.getAmount()).append("\n");
            total += data.getAmount();
        }
        s.append("\n\nYhteensä: " + total);
        carInfoText.setText(s.toString());
    }
}