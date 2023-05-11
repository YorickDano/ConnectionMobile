package com.example.connectionmobile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import android.provider.Browser;


public class MainActivity extends AppCompatActivity {
    private Button OpenLink;
    private String Url = null;
    private EditText CodeInput;
    private Button SendLink;
    private EditText UrlInput;
    private EditText CodeInput2;
    private TextView textView;

    private static String API_URL = "http://ConnectionAPI.somee.com/Connection?requestCode=";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenLink = findViewById(R.id.openLink);
        CodeInput = findViewById(R.id.codeInput);
        SendLink = findViewById(R.id.sendLink);
        CodeInput2 = findViewById(R.id.codeInput2);
        UrlInput = findViewById(R.id.urlInput);
        RequestQueue queue = Volley.newRequestQueue(this);

        OpenLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CodeInput.getText().toString();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL + code,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ObjectMapper mapper = new ObjectMapper();
                                try {
                                    Root root = mapper.readValue(response, Root.class);
                                    Url = root.body;
                                    try{
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));

                                        if (Url.contains("twitch.tv")) {
                                            intent.setPackage("com.twitch.android.app");
                                        } else if (Url.contains("youtube.com")) {
                                            intent.setPackage("com.google.android.youtube");
                                        } else if (Url.contains("instagram.com")) {
                                            intent.setPackage("com.instagram.android");
                                        } else if (Url.contains("twitter.com")) {
                                            intent.setPackage("com.twitter.android");
                                        } else if (Url.contains("telegram.me")) {
                                            intent.setPackage("org.telegram.messenger");
                                        } else if (Url.contains("vk.com")) {
                                            intent.setPackage("com.vkontakte.android");
                                        } else if (Url.contains("tiktok.com")) {
                                            intent.setPackage("com.zhiliaoapp.musically");
                                        } else {
                                            intent.putExtra(Intent.EXTRA_TITLE, "Title of the Page");
                                            intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
                                            intent.putExtra(Browser.EXTRA_CREATE_NEW_TAB, true);

                                        }
                                        startActivity(intent);
                                    }
                                    catch (Exception e){ DialogFragment dialog = new MyDialogFragment("Can't open this url: "+Url);
                                        dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");}
                                } catch (JsonMappingException e) {
                                    e.printStackTrace();
                                } catch (JsonProcessingException e) {
                                    //throw new RuntimeException(e);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String a = " ";
                    }
                });
                queue.add(stringRequest);
            }
        });
        SendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CodeInput2.getText().toString();
                String url = UrlInput.getText().toString();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL + code + "&url=" + url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                DialogFragment dialog = new MyDialogFragment("Data sent");
                                try {
                                    dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                                }
                                catch (Exception e) {e.printStackTrace();}
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogFragment dialog = new MyDialogFragment("Something wrong");
                        dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
                    }
                });
                queue.add(stringRequest);
            }
        });
    }

    public static class Root{
        @JsonProperty("id")
        public int id;
        @JsonProperty("body")
        public String body;

        @JsonProperty("code")
        public String code;
    }
    public static class MyDialogFragment extends DialogFragment {
        private String message;

        public MyDialogFragment(String message) {
            this.message = message;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


}
