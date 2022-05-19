package com.example.getdata;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "awsexample";

    private EditText mEditTextName;
    private EditText mEditTextCountry;
    private TextView mTextViewResult;
    private ArrayList<PersonalData> mArrayList;
    private UsersAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEditTextSearchKeyword;
    private String mJsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEditTextSearchKeyword = (EditText) findViewById(R.id.editText_main_searchKeyword);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());



        mArrayList = new ArrayList<>();

        mAdapter = new UsersAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);



        Button button_search = (Button) findViewById(R.id.button_main_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();


                String Keyword =  mEditTextSearchKeyword.getText().toString();
                mEditTextSearchKeyword.setText("");

                GetData task = new GetData();
                task.execute( "https://504voisdi1.execute-api.ap-northeast-2.amazonaws.com/default/getDatatest", Keyword);
            }
        });


    }

    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String name = params[1];
            String serverURL = params[0];

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("timestamp", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(body)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                String resStr = response.body().string();

                return resStr;
            } catch (IOException e) {
                e.printStackTrace();

                return e.toString();
            }
        }
    }

    private void showResult(){

        String TAG_JSON="webnautes";
        String TAG_ID = "id";
        String TAG_NAME = "timestamp";
        String TAG_TEMPERATURE ="temperature";
        String TAG_HUMIDITY ="humidity";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            String str = jsonObject.getString("body");
            JSONObject jsonObjectBody = new JSONObject(str);

            String name = jsonObjectBody.getString(TAG_NAME);
            String address = jsonObjectBody.getString(TAG_TEMPERATURE);
            String address1 = jsonObjectBody.getString(TAG_HUMIDITY);

            PersonalData personalData = new PersonalData();

            personalData.setMember_name(name);
            personalData.setMember_address(address);
            personalData.setMember_address1(address1);

            mArrayList.add(personalData);
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}