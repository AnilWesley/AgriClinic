package com.novaagritech.agriclinic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.modals.Comments;
import com.novaagritech.agriclinic.adapters.MessageAdapter;
import com.novaagritech.agriclinic.constants.ConstantValues;
import com.novaagritech.agriclinic.constants.MyAppPrefsManager;
import com.novaagritech.agriclinic.retrofit.ApiInterface;
import com.novaagritech.agriclinic.retrofit.RetrofitClientInstance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN=1;
    private MessageAdapter mMessageAdapter;

    private EditText mMessageEditText;
    private Button mSendButton;

    String mUsername;
    TextView emptyView;
    ListView mMessageListView;
    List<Comments.CommentDetails> friendlyMessages;
    String user_id,article_id;
    MyAppPrefsManager myAppPrefsManager;
    ImageView action_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ConstantValues.internetCheck(ChatActivity.this);

        Intent intent=getIntent();
        article_id=intent.getStringExtra("article_id");

        // Initialize references to views
        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView= (ListView) findViewById(R.id.messageListView);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        emptyView = (TextView) findViewById(R.id.emptyView);
        mSendButton = (Button) findViewById(R.id.sendButton);
        myAppPrefsManager = new MyAppPrefsManager(ChatActivity.this);

        user_id= myAppPrefsManager.getUserId();

        getComment();

        action_image = (ImageView) findViewById(R.id.actionImage);
        action_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                getComment();
                addComment();

            }
        });


    }

    public void addComment(){

        String text=mMessageEditText.getText().toString().trim();
        JsonObject jsonObject = new JsonObject();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String currentTime = new SimpleDateFormat("hh:mm:sss", Locale.getDefault()).format(new Date());

        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("article_id", article_id);
        jsonObject.addProperty("comment", text);
        jsonObject.addProperty("date", currentDate);
        jsonObject.addProperty("time", currentTime);

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Comments> call = service.processAddComment(jsonObject);
        call.enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(@NonNull Call<Comments> call, @NonNull Response<Comments> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    // Clear input box
                    Toast.makeText(ChatActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                    mMessageEditText.setText("");


                }
            }

            @Override
            public void onFailure(@NonNull Call<Comments> call, @NonNull Throwable t) {

                Log.d("ResponseF",""+t);
            }
        });
    }


    public void getComment(){


        JsonObject jsonObject = new JsonObject();


        jsonObject.addProperty("article_id", article_id);

        Log.d(TAG,""+jsonObject);
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Comments> call = service.processListComment(jsonObject);
        call.enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(@NonNull Call<Comments> call, @NonNull Response<Comments> response) {

                // Check if the Response is successful
                if (response.isSuccessful()){
                    Log.d(TAG,""+response.toString());
                    assert response.body() != null;
                    Comments cropsData = response.body();
                    friendlyMessages = response.body().getResponse();
                    if (cropsData.isStatus()){
                        if (friendlyMessages != null&&friendlyMessages.size()>0  ) {
                            for (int i = 0; i < friendlyMessages.size(); i++) {
                                Log.d(TAG, "" + friendlyMessages.size());
                                mMessageAdapter = new MessageAdapter(ChatActivity.this, R.layout.item_message, friendlyMessages);
                                mMessageListView.setAdapter(mMessageAdapter);
                                mMessageAdapter.notifyDataSetChanged();
                            }
                        }else {
                            mMessageListView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        }else {
                        mMessageListView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<Comments> call, @NonNull Throwable t) {

                Log.d("ResponseF",""+t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();
        }
        return true;
    }

}
