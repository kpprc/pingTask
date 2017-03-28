package com.example.frank.pingtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private Button  okBtn;
    private EditText inputText;
    private TextView pingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        okBtn = (Button) findViewById(R.id.okButton);
        inputText = (EditText) findViewById(R.id.ipEditText);
        pingTextView = (TextView) findViewById(R.id.pingResultText);

        okBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                String targetIP = inputText.getText().toString();
                if(targetIP.length()==0){
                    Toast.makeText(getApplicationContext(), "Please enter the IP address", Toast.LENGTH_SHORT).show();
                    return;
                }
                hideSoftwareKeyboard(inputText);
                pingTask task = new pingTask();
                task.execute(new String[] {targetIP});
            }

        });
    }

    public void hideSoftwareKeyboard(EditText currentEditText) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(currentEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class pingTask extends AsyncTask<String, Void, String>{
        ProgressDialog pDialog;
        @Override
        protected String doInBackground(String... targetIP) {
            return launchPing(targetIP[0]);
        }

        public void onPreExecute(){
            super.onPreExecute();
            pDialog = ProgressDialog.show(MainActivity.this, "Wait", "Ping the destination IP...");
        }

        public void onPostExecute (String res){
            pDialog.dismiss();
            pingTextView.setText(res);
        }
        public String launchPing(String ip){
            Process p;
            String format = "ping -c 4 " + ip;
            try{
                p = Runtime.getRuntime().exec(format);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                // Construct the response from ping
                String s;
                String res = "";
                while ((s = stdInput.readLine()) != null) {
                    res += s + "\n";
                }
                p.destroy();
                return res;
            }
            catch(IOException ioe){
                Log.d("pingClass", "io exception");
            }
            return "fail";
        }
    }

}
