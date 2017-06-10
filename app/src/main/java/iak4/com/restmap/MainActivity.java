
package iak4.com.restmap;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import iak4.com.restmap.Kelas.Peta;

public class MainActivity extends AppCompatActivity {
    RecyclerView rc;
    Adapter adapter;
    ArrayList<Peta> semua_data;
    HttpURLConnection connection;
    RequestData req;

    ProgressBar pb;

    void reload(){
        req = new RequestData();
        pb.setVisibility(View.VISIBLE);
        req.execute("http://192.168.1.1/restmap");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.pb);
        rc = (RecyclerView) findViewById(R.id.rv);
        rc.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rc.setLayoutManager(lm);
        reload();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_toolbar){
            startActivity(new Intent(this, Add.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private class RequestData extends AsyncTask<String,Void, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            semua_data = new ArrayList<>();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params){
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setDoInput(true);
                connection.connect();
                int status = connection.getResponseCode();

                if(status == 200) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    sb.append(br.readLine()).append("\n");
                    br.close();

                    JSONObject data = new JSONObject(sb.toString());
                    if (data.getInt("status") == 1) {
                        JSONArray array_data = data.getJSONArray("data");
                        semua_data = new ArrayList<>();
                        for(int i = 0;i<array_data.length();i++){
                            Peta peta = new Peta();
                            JSONObject isi_data = array_data.getJSONObject(i);
                            peta.setTitle(isi_data.getString("judul"));
                            peta.setLat(Double.parseDouble(isi_data.getString("lat")));
                            peta.setLng(Double.parseDouble(isi_data.getString("lng")));
                            semua_data.add(peta);
                        }
                    }
                }else{
                    return false;
                }
            }catch (UnknownHostException abc){
                if(connection != null){
                    connection.disconnect();
                    return false;
                }
            }catch (Exception abc){
                if(connection != null){
                    connection.disconnect();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            pb.setVisibility(View.GONE);
            if(!aBoolean){
                Toast.makeText(getBaseContext(), "Maaf ada yg bermasalah", Toast.LENGTH_LONG).show();
            }

            adapter = new Adapter(semua_data, getBaseContext());
            rc.setAdapter(adapter);
        }
    }
}
