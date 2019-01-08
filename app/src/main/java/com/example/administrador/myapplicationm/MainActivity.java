package com.example.administrador.myapplicationm;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String ARR = "array";

    private EditText editTextTitle;
    private EditText editTextDescription;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getRain();
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);

        editTextDescription.setText("HI");

        new doit().execute();

    }

    public class doit extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            String [] temps = {"-1","-1","-1","-1"};

            try {
                Document docVista = Jsoup.connect("https://www.avamet.org/mxo_i.php?id=c04m139e01").get();
                temps[0] = cutBeforeData(docVista.getElementById("prec").text());

                Document docXodos = Jsoup.connect("https://www.avamet.org/mxo_i.php?id=c04m055e02").get();
                temps[1] = cutBeforeData(docXodos.getElementById("prec").text());

                Document docAtz = Jsoup.connect("https://www.avamet.org/mxo_i.php?id=c04m001e02").get();
                temps[2] = docAtz.getElementById("prec").text();

                Document docVf = Jsoup.connect("https://www.avamet.org/mxo_i.php?id=c02m129e02").get();
                temps[3] = docVf.getElementById("prec").text();

                Map<String,Object> tempz = new HashMap<>();
                tempz.put(KEY_TITLE,temps[0]);
                tempz.put(KEY_DESCRIPTION, temps[1]);
                //editTextDescription.setText();
                //Toast.makeText(MainActivity.this, "HOLI", Toast.LENGTH_SHORT).show();
                db.collection("Notebook").document("Array").set(tempz)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                            }
                        })


                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void saveNote(View v){
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        Map<String,Object> note = new HashMap<>();
        note.put(KEY_TITLE,title);
        note.put(KEY_DESCRIPTION, description);

        db.collection("Notebook").document("My first note").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();;
                    }
                })


                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public String cutBeforeData(String orgData){
        int idx = 0;
        for (int i=0;i<orgData.length();i++){
            if (Character.isDigit(orgData.charAt(i))) {
                idx = i;
                break;
            }
        }
        // Contemplar temperaturas negativas
        if (idx != 0 && orgData.charAt(idx-1) == '-'){
            return orgData.substring(idx-1,orgData.length()-2);
        }
        return orgData.substring(idx,orgData.length()-2);
    }


}
