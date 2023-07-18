package com.example.lumeen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lumeen.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // Déclaration des variables pour les vues
    private TextView mTextView;
    private EditText mEditText;
    private Button mButton;
    private TextView mTextViewQuote;
    private TextView mTextViewAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues avec leurs identifiants dans le layout XML
        mTextView = findViewById(R.id.text_view);
        mEditText = findViewById(R.id.edit_text);
        mButton = findViewById(R.id.button);
        mTextViewQuote = findViewById(R.id.text_view_quote);
        mTextViewAuthor = findViewById(R.id.text_view_author);

        // Limite l'entrée aux chiffres uniquement dans l'EditText
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Ajout d'un TextWatcher pour détecter les modifications de texte dans l'EditText
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Active ou désactive le bouton en fonction du contenu de l'EditText
                mButton.setEnabled(!editable.toString().isEmpty());
            }
        });

        // Définition du OnClickListener pour le bouton
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupération du texte saisi dans l'EditText
                String inputText = mEditText.getText().toString();

                if (!TextUtils.isEmpty(inputText)) {
                    // Conversion du texte en un entier
                    int quoteId = Integer.parseInt(inputText);

                    // Vérification de la validité de l'ID de citation
                    if (quoteId <= 30 && quoteId > 0) {
                        // Envoi de la requête de citation
                        sendQuoteRequest(quoteId);
                    } else {
                        // Affichage d'un message d'erreur si l'ID de citation est invalide
                        showErrorMessage("Quote with id " + quoteId + " not found");
                    }
                }
            }
        });
    }

    // Envoie une requête pour récupérer une citation en fonction de son ID
    private void sendQuoteRequest(int quoteId) {
        String apiUrl = "https://dummyjson.com/quotes/" + quoteId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extraction de la citation et de l'auteur de la réponse JSON
                            String quote = response.getString("quote");
                            String author = response.getString("author");

                            // Affichage de la citation et de l'auteur dans les TextView correspondants
                            mTextViewQuote.setText(quote);
                            mTextViewAuthor.setText(author);

                            // Envoi d'une diffusion (broadcast) avec les données de la citation
                            sendResponseBroadcast(quoteId, quote, author);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Ajout de la requête à la file d'attente de Volley pour l'exécuter
        Volley.newRequestQueue(this).add(request);
    }

    // Envoie une diffusion (broadcast) contenant les données de la citation
    private void sendResponseBroadcast(int quoteId, String quote, String author) {
        // Création de l'intent avec l'action spécifique et les données en extra
        Intent intent = new Intent("com.lumeen.inside.technique.QuoteBroadcastReceiver.ACTION_RESPONSE");
        intent.putExtra("com.lumeen.inside.technique.QuoteBroadcastReceiver.EXTRA_QUOTE_ID", quoteId);
        intent.putExtra("com.lumeen.inside.technique.QuoteBroadcastReceiver.EXTRA_QUOTE", quote);
        intent.putExtra("com.lumeen.inside.technique.QuoteBroadcastReceiver.EXTRA_AUTHOR", author);

        // Envoi de la diffusion (broadcast)
        sendBroadcast(intent);
    }

    // Affiche une boîte de dialogue d'erreur avec un message spécifié
    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }
}
