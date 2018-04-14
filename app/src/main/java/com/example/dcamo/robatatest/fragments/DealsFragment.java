package com.example.dcamo.robatatest.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dcamo.robatatest.R;
import com.example.dcamo.robatatest.support.Offers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.dcamo.robatatest.MainActivity.ip;

public class DealsFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    SharedPreferences mSettings;
    public List<Offers> offers = new ArrayList<>();

    //    private static final List<Lessons> lessonses = new ArrayList<>();
    private class OffersAdapter extends ArrayAdapter<Offers> {

        public OffersAdapter(Context context) {
            super(context, R.layout.offers_item, offers);
        }

        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Offers offer = getItem(position);
//            Log.e(position + "", teacher.name);

//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
            convertView = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.offers_item, null);

            ((TextView) convertView.findViewById(R.id.titleOffers)).setText(offer.text);
            ((TextView) convertView.findViewById(R.id.textOffers)).setText(offer.title);

            return convertView;
        }

    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
//        Log.e("z", groupNamePassed);

        return inflater.inflate(R.layout.offers_fragment, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
//        SharedPreferences mSettings2 = getActivity().getSharedPreferences(savedGroupName2, Context.MODE_PRIVATE);

//        final String groupName = mSettings2.getString(savedGroupName2, "");
        //you can set the title for your toolbar here for different fragments different titles
//        mSettings = getActivity().getSharedPreferences(savedTeachers, Context.MODE_PRIVATE);

        final ListView listView1 = (ListView) getView().findViewById(R.id.listViewOffers);
        ArrayAdapter<Offers> adapter = new OffersAdapter(getActivity().getBaseContext());

        String myUrl = ip + "/offer";


        //String to place our result in


        String result;
        //Instantiate new instance of our class

        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            result = getRequest.execute(myUrl).get();
            if(result == null){
                Toast.makeText(getActivity().getBaseContext(), R.string.no_server_connection, Toast.LENGTH_SHORT).show();
            }else {
                try {
                    offers.clear();
                    JSONArray teachersArray = new JSONArray(result);
                    for(int i = 0; i<= teachersArray.length()-1;i++){
                        JSONObject file = teachersArray.getJSONObject(i);
                        offers.add(new Offers(file.getString("text"), file.getString("title"),file.getString("image")));
                        Log.e("test1", file.toString());

                    }
//                        SharedPreferences.Editor editor = mSettings.edit();
//                        editor.putString(savedTeachers, result);
//                        editor.apply();
                    Log.e("teachersNotSaved", result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        listView1.setAdapter(adapter);



        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayoutOffers);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO TOAST NO NET

                if (!isOnline()){
                    Toast.makeText(getActivity().getBaseContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }else{

//                    SharedPreferences mSettings2 = getActivity().getSharedPreferences(savedGroupName2, Context.MODE_PRIVATE);

//                    String groupName = mSettings2.getString(savedGroupName2, "");
                    String myUrl = ip + "/offer";

                    //String to place our result in
                    String result;
                    //Instantiate new instance of our class
                    HttpGetRequest getRequest = new HttpGetRequest();

                    try {
                        result = getRequest.execute(myUrl).get();
                        if(result == null){
                            Toast.makeText(getActivity().getBaseContext(), R.string.no_server_connection, Toast.LENGTH_SHORT).show();
                        }else {
                            offers.clear();
                            JSONArray teachersArray = new JSONArray(result);
                            for(int i = 0; i<= teachersArray.length()-1;i++){
                                JSONObject file = teachersArray.getJSONObject(i);
                                offers.add(new Offers(file.getString("text"), file.getString("title"),file.getString("image")));
                                Log.e("test1", file.toString());

                            }

//                            SharedPreferences.Editor editor = mSettings.edit();
//                            editor.putString(savedTeachers, result);
//                            editor.apply();
                            Log.e("teachersRefreshedSaved", result);
                        }
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }

                    listView1.destroyDrawingCache();
                    listView1.setVisibility(ListView.INVISIBLE);
                    listView1.setVisibility(ListView.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);}
            }
        });
//        getActivity().setTitle(R.string.teachers);
    }



    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                Log.d("lol", result);
                Log.d("lol", "lolol");
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
