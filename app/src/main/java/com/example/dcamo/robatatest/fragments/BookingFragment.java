package com.example.dcamo.robatatest.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dcamo.robatatest.R;
import com.example.dcamo.robatatest.support.Booking;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
//import com.example.dcamo.robatatest.support.MyNotificationPublisher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static com.example.dcamo.robatatest.MainActivity.ip;

public class BookingFragment extends Fragment {
    public final static int QRcodeWidth = 600 ;
    LinearLayout l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12;
    ImageView qr;
    Bitmap bitmap ;
    TextView timeStart;
    TextView timeEnd;
    TextView date;
    TextView dateSorry;
    TextView tableText;
    TextView bookingName;
    TextView booingNumber;
    TextView bookingTable;
    TextInputLayout nameEditBig;
    TextInputLayout phoneEditBig;
    EditText name;
    EditText number;
    Button order;
    Button table;
    Button delete;
    Spinner quantity;
    Spinner tableNumber;
    CheckBox callback;
    String dateString;
    String android_id;
    int savedHour, savedMin = 0;
    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        return inflater.inflate(R.layout.booking_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final ArrayList<Booking> arrayList = new ArrayList<>();
        date = getActivity().findViewById(R.id.datePicker);
        l1 = getActivity().findViewById(R.id.linear1);
        l2 = getActivity().findViewById(R.id.linear2);
        l3 = getActivity().findViewById(R.id.linear3);
        l4 = getActivity().findViewById(R.id.linear4);
        l5 = getActivity().findViewById(R.id.linear5);
        l6 = getActivity().findViewById(R.id.linear6);
        l7 = getActivity().findViewById(R.id.linear7);
        l8 = getActivity().findViewById(R.id.linear8);
        l9 = getActivity().findViewById(R.id.linear9);
        l10 = getActivity().findViewById(R.id.linear10);
        l11 = getActivity().findViewById(R.id.linear11);
        l12 = getActivity().findViewById(R.id.linear12);

        timeStart = getActivity().findViewById(R.id.timePickerStart);
        timeEnd = getActivity().findViewById(R.id.timePickerEnd);
        order = getActivity().findViewById(R.id.orderButton);
        quantity = getActivity().findViewById(R.id.peopleSpinner);
        callback = getActivity().findViewById(R.id.callbackCheck);
        name = getActivity().findViewById(R.id.nameEdit);
        number= getActivity().findViewById(R.id.phoneEdit);
        nameEditBig = getActivity().findViewById(R.id.nameEditBig);
        phoneEditBig = getActivity().findViewById(R.id.phoneEditBig);
        tableNumber = getActivity().findViewById(R.id.tableNumber);
        tableText = getActivity().findViewById(R.id.tableText);
        table = getActivity().findViewById(R.id.button2);
        booingNumber = getActivity().findViewById(R.id.numberBookingIn);
        bookingName = getActivity().findViewById(R.id.nameBookingIn);
        bookingTable = getActivity().findViewById(R.id.tableBookingIn);
        qr = getActivity().findViewById(R.id.qr);
        delete = getActivity().findViewById(R.id.deleteButton);



        android_id = Settings.Secure.getString(getActivity().getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("id", android_id);
        Date now = new Date();
        String sdf = new SimpleDateFormat("EEE, MMM dd, yyyy").format(now);
        date.setText(sdf);

        if(isOnline()){
            String myUrl = ip + "/booking/check/id?id=" + android_id;
            //String to place our result in
            String result;
            //Instantiate new instance of our class
            HttpGetRequest getRequest = new HttpGetRequest();
            try {
                result = getRequest.execute(myUrl).get();
                if(result == null){
                    Toast.makeText(getActivity().getBaseContext(), R.string.no_server_connection, Toast.LENGTH_SHORT).show();
                }else {


                    JSONArray dataArray = new JSONArray(result);
                    for (int i = 0; i <= dataArray.length()-1; i++){
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        arrayList.add(new Booking(dataObject.getString("__v"),dataObject.getString("customerName"),dataObject.getString("customerNumber"),dataObject.getString("tableNumber"),dataObject.getString("timeStart"),dataObject.getString("timeEnd"),dataObject.getString("quantity"),dataObject.getString("callback"),dataObject.getString("bookingDate"),dataObject.getString("_id"),dataObject.getString("createdDate"),dataObject.getString("userId"), dataObject.getString("status")));
                    }



                    if(!arrayList.isEmpty()){
                        Log.e("dataGethered", arrayList.get(0).bookingDate);

                        l1.setVisibility(View.GONE);
                        l2.setVisibility(View.GONE);
                        l3.setVisibility(View.GONE);
                        l4.setVisibility(View.GONE);
                        l5.setVisibility(View.GONE);
                        l6.setVisibility(View.GONE);


                        l7.setVisibility(View.VISIBLE);
                        l8.setVisibility(View.VISIBLE);
                        l9.setVisibility(View.VISIBLE);
                        l10.setVisibility(View.VISIBLE);
                        l11.setVisibility(View.VISIBLE);
                        l12.setVisibility(View.VISIBLE);

                       String datePicked = new SimpleDateFormat("EEEEEE, MMM dd").format(new SimpleDateFormat("MM.dd.yyyy").parse(arrayList.get(0).bookingDate));

                        bookingTable.setText(arrayList.get(0).tableNumber);
                        booingNumber.setText(arrayList.get(0).timeStart);
                        bookingName.setText(datePicked);

                        try {
//                    bitmap = TextToImageEncode(EditTextValue);
                            bitmap = TextToImageEncode(arrayList.get(0).userId + "CODE" + arrayList.get(0)._id);

                            Log.e("qrTest", arrayList.get(0).userId + "CODE" + arrayList.get(0)._id);

                            qr.setImageBitmap(bitmap);

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
//                        bookingName.setText(arrayList.get(0).customerName);
//                        l6.setVisibility(View.GONE);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                if(isOnline()){
                                    String myUrl = ip + "/booking/delete/?id=" + arrayList.get(0)._id;
                                    //String to place our result in
                                    String result;
                                    //Instantiate new instance of our class
                                    HttpGetRequest getRequest = new HttpGetRequest();
                                    try {
                                        result = getRequest.execute(myUrl).get();
                                        if(result == null){
                                            Toast.makeText(getActivity().getBaseContext(), R.string.no_server_connection, Toast.LENGTH_SHORT).show();
                                        }else {

                                            getActivity().finish();
                                            startActivity(getActivity().getIntent());

                                        }
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                        Log.e("meeesex", "here");
                                    }

                                }
//                dateString = datePicker.returnOfTheJedi();
//                Log.e("test", dateString);
//                Toast toast = Toast.makeText(getActivity().getBaseContext(),
//                        datePicker.finaltime,
//                        Toast.LENGTH_SHORT);
//                toast.show();
//                date.setText(datePicker.finaltime);
                            }
                        });

                    }

//                    editText = (AutoCompleteTextView) findViewById(R.id.editGroupName);
//                    editText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, arrA));
                }
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
                Log.e("meeesex", "here");
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }



        timeStart.setText("18:00");
        timeEnd.setText("20:00");

        if(name.getText().toString().isEmpty())order.setEnabled(false);
        if(number.getText().toString().isEmpty())order.setEnabled(false);

        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    phoneEditBig.setError(null);
                    order.setEnabled(true);

                }
                else{
                    phoneEditBig.setError(getString(R.string.required));
                    order.setEnabled(false);
                }
            }
        });
        number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(((EditText) v).getText())) {
                        phoneEditBig.setError(null);
                        order.setEnabled(true);

                    }
                    else{
                        phoneEditBig.setError(getString(R.string.required));
                        order.setEnabled(false);

                    }
                }
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    nameEditBig.setError(null);
                    order.setEnabled(true);

                }
                else{
                    nameEditBig.setError(getString(R.string.required));
                    order.setEnabled(false);
                }
            }
        });
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(((EditText) v).getText())) {
                        nameEditBig.setError(null);
                        order.setEnabled(true);
                    }
                    else{
                        nameEditBig.setError(getString(R.string.required));
                        order.setEnabled(false);
                    }
                }
            }
        });


//        date.setText(currentTime);

        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showHourPicker();

//                final Calendar mcurrentTime = Calendar.getInstance();
//                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                final int minute = mcurrentTime.get(Calendar.MINUTE);
//                final RangeTimePickerDialog mTimePicker;
//                mTimePicker = new RangeTimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                        Log.e(selectedHour + "", selectedMinute + "");
//
//                        Log.d("TAG", "inside OnTimeSetListener");
//
//                    }
//                }, hour, minute, true);//true = 24 hour time
//                mTimePicker.setTitle("Select Time");
//                mTimePicker.setMin(hour, minute);
//                mTimePicker.show();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String datePicked, timeStartString, timeEndString, quanity, namePicked, numberPicked, callbackPicked, tablePicked;
                datePicked = date.getText().toString();


                try {
                    datePicked = new SimpleDateFormat("MM.dd.yyyy").format(new SimpleDateFormat("EEE, MMM dd, yyyy").parse(date.getText().toString()));
                    Log.e("XXXXXXXXXX", datePicked);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timeStartString = timeStart.getText().toString();
                timeEndString = timeEnd.getText().toString();
                namePicked = name.getText().toString();
                numberPicked = number.getText().toString();
                tablePicked = tableNumber.getSelectedItem().toString();
                try {
                    namePicked = URLEncoder.encode(namePicked, "utf-8");
                    numberPicked = URLEncoder.encode(numberPicked, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                quanity = quantity.getSelectedItem().toString();
                callbackPicked = callback.isChecked() ? "true" : "false";
                Date now = new Date();


                Log.e("datePicked", datePicked);
                    Log.e("timePickedStart", timeStartString);
                    Log.e("timePickedEnd", timeEndString);
                    Log.e("tablePicked", timeEndString);
                    Log.e("quantity", quanity);
                    Log.e("name", namePicked);
                    Log.e("phone", numberPicked);
                    Log.e("callback", callbackPicked);
                    String answerString, timeString, errorString;
                    ArrayList<String> arrayList = new ArrayList<>();


                    HttpGetRequest getRequest = new HttpGetRequest();
                    try {
                        String result = getRequest.execute(ip + "/robata?date="+ datePicked + "&timeStart=" + timeStartString + "&timeEnd=" + timeEndString + "&quantity=" + quanity + "&table=" + tablePicked + "&name=" + namePicked + "&phone=" + numberPicked+ "&callback=" + callbackPicked + "&userId=" + android_id + "&status=pending").get();



                        Log.e("result", result);

                        JSONObject dataJsonObj = new JSONObject(result);
                        JSONArray arr = dataJsonObj.getJSONArray("response");
                        JSONObject arrJSONObject = arr.getJSONObject(0);
                        answerString = arrJSONObject.getString("answer");


                        if(answerString.equals("true")){
                            Log.e("order","true");

                            JSONObject dataObject = arr.getJSONObject(2);

                            arrayList.add(dataObject.getJSONObject("data").getString("__v"));
                            arrayList.add(dataObject.getJSONObject("data").getString("customerName"));
                            arrayList.add(dataObject.getJSONObject("data").getString("customerNumber"));
                            arrayList.add(dataObject.getJSONObject("data").getString("tableNumber"));
                            arrayList.add(dataObject.getJSONObject("data").getString("timeStart"));
                            arrayList.add(dataObject.getJSONObject("data").getString("timeEnd"));
                            arrayList.add(dataObject.getJSONObject("data").getString("quantity"));
                            arrayList.add(dataObject.getJSONObject("data").getString("callback"));
                            arrayList.add(dataObject.getJSONObject("data").getString("bookingDate"));
                            arrayList.add(dataObject.getJSONObject("data").getString("_id"));
                            arrayList.add(dataObject.getJSONObject("data").getString("createdDate"));
                            arrayList.add(dataObject.getJSONObject("data").getString("userId"));
                            arrayList.add(dataObject.getJSONObject("data").getString("status"));




                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                            notificationIntent.addCategory("android.intent.category.DEFAULT");

                            PendingIntent broadcast = PendingIntent.getBroadcast(getActivity().getBaseContext(), 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Calendar cal = Calendar.getInstance();


                            cal.set(Integer.parseInt(datePicked.substring(datePicked.length()-4, datePicked.length())),Integer.parseInt(datePicked.substring(0, datePicked.length()-8))-1,Integer.parseInt(datePicked.substring(datePicked.length()-7, datePicked.length()-5)),Integer.parseInt(timeStartString.subSequence(0, 2) + ""), Integer.parseInt(timeStartString.subSequence(3, timeStartString.length()) + ""),00);
                            Log.e("time", cal.getTime().toString());
                            cal.add(Calendar.HOUR, -1);
                            Log.e("time", cal.getTime().toString());

                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

                            getActivity().finish();
                            startActivity(getActivity().getIntent());






//                        JSONObject answer = arr.getJSONObject(0);
//                        JSONObject responseTime = arr.getJSONObject(1);
//                        JSONObject error = arr.getJSONObject(2);

//                        responseTimeString = answer.getString("time");
//                        errorString = answer.getString("error");

//                            Log.e("answer", answerString);
//                        Log.e("responseTime", responseTime);
//                        Log.e("error", error);


                        }else{
                            Log.e("order", "false");
                        }



                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


//                performPostCall("192.168.88.251:3001/robata", hashMap);
//            }
        });


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String normalizedDate = "";
                com.example.dcamo.robatatest.support.DatePicker datePicker = new com.example.dcamo.robatatest.support.DatePicker(date, dateSorry);
                datePicker.show(getFragmentManager(), ")");
//                dateString = datePicker.returnOfTheJedi();
//                Log.e("test", dateString);
//                Toast toast = Toast.makeText(getActivity().getBaseContext(),
//                        datePicker.finaltime,
//                        Toast.LENGTH_SHORT);
//                toast.show();
//                date.setText(datePicker.finaltime);
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void validateEditText(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            phoneEditBig.setError(null);
        }
        else{
            phoneEditBig.setError(getString(R.string.required));
        }
    }

    public void showHourPicker() {
        final Calendar myCalender = Calendar.getInstance();


        if(savedHour == 0 && savedMin == 0){
            savedHour = myCalender.get(Calendar.HOUR_OF_DAY);
            savedMin = myCalender.get(Calendar.MINUTE);
        }



        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    savedHour = hourOfDay;
                    savedMin = minute;
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    Toast toast = Toast.makeText(getActivity().getBaseContext(),
                            hourOfDay + ":" + minute,
                            Toast.LENGTH_SHORT);
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        String sdf = new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("H:m").parse(hourOfDay + ":" + minute));
                        timeStart.setText(sdf);
                        myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalender.set(Calendar.MINUTE, minute);

                        String timeStart = myCalender.get(Calendar.HOUR_OF_DAY) + ":" + myCalender.get(Calendar.MINUTE);
//                        Log.e("timeStart", timeStart);

                        myCalender.add(Calendar.HOUR_OF_DAY, 2);
                        String timeReservedString = myCalender.get(Calendar.HOUR_OF_DAY) + ":" + myCalender.get(Calendar.MINUTE);
//                        Log.e("timeReserved", timeReservedString);

                        String ts = new SimpleDateFormat("HH:mm").format(new SimpleDateFormat("H:m").parse(timeReservedString));

                        Date timeReserved = simpleDateFormat.parse(timeReservedString);
                        Date time = simpleDateFormat.parse(timeStart);
                        Date dayFinish = simpleDateFormat.parse("23:30");
                        Date nightAlmostFinish = simpleDateFormat.parse("23:59");
                        Date nightStart = simpleDateFormat.parse("00:00");
                        Date nightFinish = simpleDateFormat.parse("01:30");
                        Date dayStart = simpleDateFormat.parse("11:30");


                        if((time.after(dayStart)) && (time.before(dayFinish))){
                            Log.e("time is ok", ts.toString());
                            timeEnd.setText(ts.toString());
                        }else if((time.after(dayFinish)) && (time.before(nightAlmostFinish))){
                            Log.e("time is almost ok", "01:30");
                            timeEnd.setText("01:30");
                        }else if((time.after(nightStart)) && time.before(nightFinish)){
                            Log.e("time is night, but ok", "01:30");
                            timeEnd.setText("01:30");
                        }else if((time.after(nightFinish)) && (time.before(dayStart))){
                            Log.e("time is bad, but ok", "--:--");
                            timeEnd.setText("--:--");
                        }else if(time.compareTo(nightStart) == 0){
                            Log.e("time is midnight, ok", "--:--");
                            timeEnd.setText("01:30");
                        }else if(time.compareTo(dayFinish) == 0){
                            Log.e("time is 23:30, ok", "01:30");
                            timeEnd.setText("01:30");
                        }



                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    toast.show();
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), myTimeListener, savedHour, savedMin, true);
        timePickerDialog.setTitle("Choose hour:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    boolean timeCompare(String timeStart, String timeEnd, String timeNow){

        try {
            String string1 = timeStart;
            Date time1 = new SimpleDateFormat("HH:mm").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String string2 = timeEnd;
            Date time2 = new SimpleDateFormat("HH:mm").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);




            String someRandomTime = timeNow;
            Date d = new SimpleDateFormat("HH:mm").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                //checkes whether the current time is between 14:49:00 and 20:11:13.
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return false;
    }

    private boolean checktimings(String timeReserved, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(timeReserved);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            }else if (timeCompare("00:00", "01:30", timeReserved)){
                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    public String  performPostCall(String requestURL, HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public void toastMaker(String msg){
        Toast toast = Toast.makeText(getActivity().getBaseContext(),
                msg,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
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
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                Log.d("lol", result);
                Log.d("lol", "lolol");
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }


        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        int color_black = getResources().getColor(R.color.QRCodeBlackColor);
        int color_white = getResources().getColor(R.color.QRCodeWhiteColor);

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        color_black:color_white;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 600, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


}
