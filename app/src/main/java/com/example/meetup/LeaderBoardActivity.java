package com.example.meetup;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderBoardActivity extends AppCompatActivity {

    private ListView leaderboardListView;
    private ArrayAdapter<String> adapter;
    private List<String> leaderboardItems = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardListView = findViewById(R.id.leaderboardListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leaderboardItems);
        leaderboardListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        db.collection("leaderboard")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    leaderboardItems.clear();
                    int rank = 1;
                    for (var doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        String username = (String) data.get("username");
                        Long score = (Long) data.get("score");
                        leaderboardItems.add(rank + ". " + username + " — " + score + " очков");
                        rank++;
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка загрузки таблицы лидеров!", Toast.LENGTH_SHORT).show();
                });
    }
}
