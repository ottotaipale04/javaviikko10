package com.example.viikko10;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private EditText cityName;
    private EditText yearInput;
    private Button searchInfo;
    private Button listInfo;
    private TextView statusText;

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
        statusText = findViewById(R.id.StatusText);
    }

    public void switchToListInfo(View view) {
        Intent intent = new Intent(this, ListInfoActivity.class);
        startActivity(intent);
    }

    public void switchToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void getData(Context context, String city, int year) {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode areas = null;

        try {

            areas = objectMapper.readTree(new URL("https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/mkan/statfin_mkan_pxt_11ic.px"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        for (JsonNode node : areas.get("variables").get(0).get("values")) {
            values.add(node.asText());
        }
        for (JsonNode node : areas.get("variables").get(0).get("valueTexts")) {
            keys.add(node.asText());
        }

        HashMap<String, String> municipalityCodes = new HashMap<>();

        for(int i = 0; i < keys.size(); i++) {
            municipalityCodes.put(keys.get(i), values.get(i));
        }

        String code = municipalityCodes.get(city);

        if (code == null) {
            return;
        }

        try {
            URL url = new URL("https://pxdata.stat.fi/PxWeb/api/v1/fi/StatFin/mkan/statfin_mkan_pxt_11ic.px");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JsonNode jsonInputString = objectMapper.readTree(context.getResources().openRawResource(R.raw.query));

            ((ObjectNode) jsonInputString.get("query").get(0).get("selection")).putArray("values").removeAll().add(code);

            ((ObjectNode) jsonInputString.get("query").get(3).get("selection")).putArray("values").removeAll().add(String.valueOf(year));

            byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
            OutputStream os = con.getOutputStream();
            os.write(input, 0, input.length);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            JsonNode municipalityData = objectMapper.readTree(response.toString());

            ArrayList<String> carTypes = new ArrayList<>();
            ArrayList<Integer> carAmounts = new ArrayList<>();

            for (JsonNode node : municipalityData.get("dimension").get("Ajoneuvoluokka").get("category").get("label")) {
                carTypes.add(node.asText());
            }

            for (JsonNode node : municipalityData.get("value")) {
                carAmounts.add(node.asInt());
            }

            CarDataStorage storage = CarDataStorage.getInstance();
            storage.clearData();
            storage.setCity(city);
            storage.setYear(year);

            for(int i = 0; i < carTypes.size(); i++) {
                storage.addCarData(new CarData(carTypes.get(i), carAmounts.get(i)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickSearch(View view) {
        String cityString = cityName.getText().toString().trim();
        String yearString = yearInput.getText().toString().trim();

        if (cityString.isEmpty()) {
            return;
        }

        try {
            int year = Integer.parseInt(yearString);

            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    getData(SearchActivity.this, cityString, year);
                }
            });

        } catch (NumberFormatException e) {
        }
    }
}