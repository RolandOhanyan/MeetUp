
package com.example.meetup;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
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
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    leaderboardItems.clear();

                    Map<String, Long> bestScores = new HashMap<>();

                    for (var doc : queryDocumentSnapshots) {
                        String username = (String) doc.get("username");
                        Long score = (Long) doc.get("score");

                        if (username != null && score != null) {
                            // Добавляем только лучший результат для каждого пользователя
                            if (!bestScores.containsKey(username) || score > bestScores.get(username)) {
                                bestScores.put(username, score);
                            }
                        }
                    }

                    // Сортируем по убыванию очков
                    List<Map.Entry<String, Long>> sorted = new ArrayList<>(bestScores.entrySet());
                    sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

                    int rank = 1;
                    for (Map.Entry<String, Long> entry : sorted) {
                        if (rank > 10) break;
                        leaderboardItems.add(rank + ". " + entry.getKey() + " — " + entry.getValue() + " очков");
                        rank++;
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка загрузки таблицы лидеров!", Toast.LENGTH_SHORT).show();
                });
    }

}