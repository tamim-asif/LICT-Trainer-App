package kingdom.bnlive.in.trainermonitorlive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.security.Provider;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Sk Faisal on 3/28/2018.
 */

public class FragmentDashboard extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public Handler handler;
    private Spinner spinner;
    View view;
    Context context;
    private RecyclerView myListView;
    private RecyclerView.Adapter adapter;
    private List<BatchStatusModel> dataList;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore db;
    private String TAG = "dashboardfragment";
    private String name;
    private String email;
    private String role;
    private TextView university;
    private TextView location;
    private TextView start;
    private TextView end;
    private TextView latlong;
    private TextView attendence;
    private TextView address;
    private TextView statusview;
    private TextView startTextView;
    private Button btnStart;
    private Button btnEnd;
    private ImageView btnMap;
    private EditText inpAttendence;
    private boolean isDataSet = false;
    boolean flag;
    private boolean isStartClicked;
    SharedPreferences sharedPreferences;
    String MyPreferences = "monitordb";
    private String batchId;
    boolean isConfirmed = false;
    List<MergeSheduleUniversity> mergeSheduleUniversityList;
    private Bundle savedInstanceState;
    int datasize = 0;
    String status;
    String intime;
    int selectposition = 0;
    private TextView distanceText;
    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private boolean mLocationPermissionGranted;
    double lat1, lon1;
    protected LocationManager locationManager;
    private DecimalFormat df2 = new DecimalFormat(".##");

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            boolean isUpdate=false;
//           if(lat1==location.getLatitude()&&lon1==location.getLongitude())
//                {
//                    isUpdate=false;
//                }
//                else isUpdate=true;
//
//            if(isUpdate==true) {
                lat1 = location.getLatitude();
                lon1 = location.getLatitude();
                //Toast.makeText(getActivity().getBaseContext(), "Location Change: " + lat1 + "," + lon1, Toast.LENGTH_LONG).show();
                Log.v(TAG, "IN ON LOCATION CHANGE");
            updateDistance();
//            if (waitingForLocationUpdate) {
//                getNearbyStores();
//                waitingForLocationUpdate = false;
//            }

                //locationManager.removeUpdates(this);
           // }
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.v(TAG, "Status changed: " + s);
        }

        public void onProviderEnabled(String s) {
            Log.e(TAG, "PROVIDER DISABLED: " + s);
        }

        public void onProviderDisabled(String s) {
            Log.e(TAG, "PROVIDER DISABLED: " + s);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        getLocationPermission();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getBaseContext())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        locationissue();

        role = "trainer";
        //sharedPreferences.
        handler = new Handler();

        mergeSheduleUniversityList = new ArrayList<>();
        spinner = view.findViewById(R.id.spinner);
        db = FirebaseFirestore.getInstance();
        context = getActivity().getBaseContext();
        sharedPreferences = context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        name = sharedPreferences.getString("name", null);
        email = sharedPreferences.getString("email", null);
        role = sharedPreferences.getString("role", null);

//        headerName.setText(""+sharedPreferences.getString("name",null));
//        headerEmail.setText(""+sharedPreferences.getString("email",null));
//        headerRole.setText(""+sharedPreferences.getString("role",null));

        currentuserrealtimeUpdate();
        //init all layout components
        initAll();

        return view;
    }

    @SuppressLint("MissingPermission")
    public void locationissue() {

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        Log.w(TAG,"CurrentLocProvider :"+locationProvider);

      Location  location=locationManager.getLastKnownLocation(locationProvider);
        Log.i(TAG,"Location :"+location);
        if(location!=null){
            Log.d(TAG,"Location Lat :"+location.getLatitude());
            Log.d(TAG,"Location Lat :"+location.getLongitude());
           String latitude = Double.toString(location.getLatitude());
          String  longitude = Double.toString(location.getLongitude());

           // txtView.append("\nLatitude : "+latitude+"\n Longitude : "+longitude);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1L, 1f, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,this);
    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 100, 1,this);
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_LOW);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
//
//        String provider = locationManager.getBestProvider(criteria, true);
//
//        // Cant get a hold of provider
//        if (provider == null) {
//            Log.v(TAG, "Provider is null");
//            //showNoProvider();
//            return;
//        } else {
//            Log.v(TAG, "Provider: " + provider);
//        }
//        LocationListener locationListener=new MyLocationListener();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1L, 1f, locationListener);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,locationListener);
//       // locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 100, 1,locationListener);
//        // connect to the GPS location service
//        if (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location oldLocation = locationManager.getLastKnownLocation(provider);
}
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.v(TAG, "Status changed: " + s);
    }

    public void onProviderEnabled(String s) {
        Log.e(TAG, "PROVIDER DISABLED: " + s);
    }

    public void onProviderDisabled(String s) {
        Log.e(TAG, "PROVIDER DISABLED: " + s);
    }
    public List<BatchStatusModel> getDataList() {
        return dataList;
    }

    public void setDataList(List<BatchStatusModel> dataList) {
        this.dataList = dataList;
    }

    public boolean isStartClicked() {
        return isStartClicked;
    }

    public void setStartClicked(boolean startClicked) {
        isStartClicked = startClicked;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void initAll() {
//    private TextView university;
//    private TextView location;
//    private TextView start;
//    private TextView end;
//    private TextView attendence;
//    private TextView address;
//    private TextView statusview;
//    private Button btnStart;
//    private ImageView btnMap;
//    private EditText inpAttendence;
//GeoPoint point
        university = view.findViewById(R.id.textView2);
        location = view.findViewById(R.id.textView3);
        start = view.findViewById(R.id.textView4);
        end = view.findViewById(R.id.textView6);
        attendence = view.findViewById(R.id.textView9);
        latlong = view.findViewById(R.id.txtlatlong);
        address = view.findViewById(R.id.textview12);
        distanceText = view.findViewById(R.id.distance);
        statusview = view.findViewById(R.id.textView10);
        btnStart = view.findViewById(R.id.button2);
        btnMap = view.findViewById(R.id.imageButton);
        inpAttendence = view.findViewById(R.id.editText2);
        startTextView=view.findViewById(R.id.txtStart);
        btnEnd = view.findViewById(R.id.button3);
        String id = "";
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (statusview.getText().toString().equals("scheduled")) {
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    String am_pm;
                    String tostart = start.getText().toString();
                    String[] tohm = tostart.split(":");
                    int thour = Integer.parseInt(tohm[0]);
                    int tmunite = Integer.parseInt(tohm[1]);
                    int hour2 = hour;
                    int starthour = hour - 1;
                    status = "";
                    boolean bstart = false;
                    if ((starthour >= thour - 1) || (starthour <= thour + 1)) {
                        if (hour == thour - 1) {
                            int startminute = tmunite;
                            for (int i = 10; i >= 0; i--) {
                                startminute--;
                                if (startminute == -1)
                                    startminute = 59;
                            }
                            if (minute >= startminute && minute <= 59) {
                                status = "started on time";
                                bstart = true;
                            }
                        }
                        if (hour >= thour) {
                            Log.d("hour", "T munite: " + tmunite);
                            if (hour == thour) {
                                if ((minute >= 0) && (minute <= tmunite + 15)) {
                                    status = "ongoing";
                                    bstart = true;
                                }

                                if ((minute > tmunite + 15) && (minute <= tmunite + 59)) {
                                    status = "late";
                                    bstart = true;
                                }
                            } else {
                                int exmunite = tmunite;
                                for (int i = 0; i <= 15; i++) {
                                    exmunite++;
                                    if (exmunite == 60)
                                        exmunite = 0;
                                }
                                if ((minute >= 0) && (minute <= exmunite)) {
                                    status = "ongoing";
                                    bstart = true;
                                } else {
                                    status = "late";
                                    bstart = true;
                                }
                            }

                        }


                    }
                    if (hour2 >= 12) {
                        am_pm = "pm";
                        hour2 = hour2 - 12;
                    } else
                        am_pm = "am";

                   // startTextView.setText(hour2+":"+minute+" "+am_pm);
                    // setBatchId(statusview.getText().toString());


//                if(hour==thour){
//                    if(minute>=minute+30)
//                        status = "delay";
//                    if(minute<minute+30)
//                        status = "ongoing";
//                }

                    if (bstart == true) {
                        //Snackbar.make(v, "Batch Started " + status + " at: " + "" + hour2 + " : " + minute + " " + am_pm, Snackbar.LENGTH_LONG).setAction(null, null).show();
                        intime = hour + ":" + minute;
                       // getDistanceInMiles(p1,new GeoPoint())
                        String dis=distanceText.getText().toString();
                        if(!dis.equals("")) {
                            String[] distance = dis.split(" ");
                            double d=Double.parseDouble(distance[0]);
                            if (d<=0.5)
                            {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Warning!")
                                        .setMessage("Are you sure to start this batch?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                //isConfirmed=true;

                                                updateStatus(status, intime, getBatchId());
                                                btnStart.setText("End");

                                                // setStartClicked(true);
                                                btnEnd.setVisibility(View.GONE);

                                                inpAttendence.setVisibility(View.VISIBLE);
                                                Snackbar.make(v, "Batch " + status, Snackbar.LENGTH_LONG).setAction(null, null).show();
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Snackbar.make(v, "Batch not started", Snackbar.LENGTH_LONG).setAction(null, null).show();
                                            }
                                        }).show();

                            }
                            else   new AlertDialog.Builder(getContext())
                                    .setTitle("Warning!")
                                    .setMessage("Please stay within 0.5 km from university to start this batch")

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false).show();
                        }

                    } else {
                        Snackbar.make(v, "Batch not started at this time", Snackbar.LENGTH_LONG).setAction(null, null).show();
                    }


                } else if ((statusview.getText().toString().equals("started on time")) || (statusview.getText().toString().equals("late")) || (statusview.getText().toString().equals("ongoing"))) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning!")
                            .setMessage("Are you sure to finish this batch?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String input_attendence = inpAttendence.getText().toString();
                                    btnStart.setVisibility(View.GONE);
                                    //updateStatus("completed",getBatchId(),input_attendence);

                                    String att = inpAttendence.getText().toString();
                                    Calendar calendar = Calendar.getInstance();
                                    // String outhour=""+
                                    String outtime = "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                                    updateStatus("completed", outtime, att, getBatchId());
                                    btnStart.setVisibility(View.GONE);
                                    inpAttendence.setVisibility(View.GONE);
                                    Snackbar.make(v, "Batch completed", Snackbar.LENGTH_LONG).setAction(null, null).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Snackbar.make(v, "Batch not ended", Snackbar.LENGTH_LONG).setAction(null, null).show();
                                }
                            }).show();

                    //setStartClicked(false);
//                FragmentManager manager=getFragmentManager();
//                manager.beginTransaction().replace(R.id.changeLayout,new FragmentDashboard()).commit();
                }
//            spinner.setOnItemSelectedListener(false);
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Warining!")
                        .setMessage("Are you sure to cancel this batch?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                String id = statusview.getText().toString();
                                String status = "cancelled";
                                String att = inpAttendence.getText().toString();
                                Calendar calendar = Calendar.getInstance();
                                // String outhour=""+
                                String outtime = "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                                updateStatus(status, outtime + "(Cancelled)", att, getBatchId());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(v, "Batch not cancelled", Snackbar.LENGTH_LONG).setAction(null, null).show();
                            }
                        }).show();


            }
        });
        flag = true;
    }

    public void updateStatus(final String status, final String outtime, final String attendence, String batch_id) {
        final DocumentReference sfDocRef = db.collection("batch_status").document(batch_id);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                transaction.update(sfDocRef, "status", status);
                transaction.update(sfDocRef, "outtime", outtime);
                transaction.update(sfDocRef, "attendance", attendence);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });
    }

    public void updateStatus(final String status, final String intime, String batch_id) {
        final DocumentReference sfDocRef = db.collection("batch_status").document(batch_id);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                transaction.update(sfDocRef, "status", status);
                transaction.update(sfDocRef, "intime", intime);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });
    }

    public void currentuserrealtimeUpdate() {
        try {
            Log.d(TAG, "Call Realtime Update. Name: " + name);
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            db.collection("batch_status")
                    .whereEqualTo("trainer_name", name)
                    .whereEqualTo("date", timeStamp)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d(TAG, "Error: " + e);
                            }
                            if (querySnapshot != null) {
                                final List<BatchStatusModel> list = new ArrayList<>();
                                for (DocumentSnapshot snapshot : querySnapshot) {
                                    String id = snapshot.getId();
                                    BatchStatusModel model = snapshot.toObject(BatchStatusModel.class);
                                    model.setId(id);
                                    list.add(model);
                                    getUniversity(model);
                                    Log.d(TAG, "Data: " + model.toString());
                                }
                                setDataList(list);

                                for (DocumentChange documentChange : querySnapshot.getDocumentChanges()) {
                                    switch (documentChange.getType()) {
                                        case ADDED:
                                            String id = documentChange.getDocument().getId();
                                            BatchStatusModel model = documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model.setId(id);
                                            Log.d(TAG, "Data Added: " + model.toString());
                                            break;
                                        case MODIFIED:
                                            String id2 = documentChange.getDocument().getId();
                                            BatchStatusModel model2 = documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model2.setId(id2);
                                            Log.d(TAG, "Data Modified: " + model2.toString());
//                                            Notification notification=new Notification.Builder
//                                                    (getActivity().getApplicationContext()).setContentTitle("Batch Status Updated!").setContentText("Batch "+model2.getBatch_code()+" has been "+model2.getStatus()).
//                                                    setContentTitle("Date: "+model2.getDate()).setSmallIcon(R.drawable.ic_menu_gallery).build();
//
//                                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//                                            notif.notify(1, notification);
                                            break;
                                        case REMOVED:
                                            String id3 = documentChange.getDocument().getId();
                                            BatchStatusModel model3 = documentChange.getDocument().toObject(BatchStatusModel.class);
                                            model3.setId(id3);
                                            Log.d(TAG, "Data Modified: " + model3.toString());
                                            break;
                                    }
                                }
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    public void setSpinner(List<BatchStatusModel> listModel, final List<MergeSheduleUniversity> mlist) {
        final List<BatchStatusModel> list = listModel;
        Log.d("testmerge", "Data: Item " + mlist.toString());
        String[] items = new String[list.size() + 1];
        items[0] = "Select Batch Code";
        int i = 1;
        for (MergeSheduleUniversity model : mlist) {
            items[i] = model.getStatusModel().getBatch_code();
            Log.d("testmerge", "Info size: " + mlist.size());
            //i++;
        }
        if (list.size() == mlist.size()) {
            int j = 1;
            for (MergeSheduleUniversity model : mlist) {
                items[i] = model.getStatusModel().getBatch_code();
                //Log.d("testmerge","Info size: "+mlist.size());
                j++;
            }
        }
        for (BatchStatusModel model : list) {
            items[i] = model.getBatch_code();
            Log.d("testmerge", "Batch size: " + list.size());
            i++;
        }
        ArrayAdapter<String> arrayAdapter = null;
        if (flag == true) {

            arrayAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.select_dialog_item, items);


            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            if (selectposition != 0)
                spinner.setSelection(selectposition);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //   String s=arrayAdapter.getItem(position);
//                                        btnStart.setText("Start");
//                                        btnEnd.setText("End");
//                                        setStartClicked(false);
//                                        btnStart.setVisibility(View.VISIBLE);
//                                        btnEnd.setVisibility(View.VISIBLE);
                    spinnerClicked(spinner, position, list, mlist);
                   // statusview.setTextColor(getResources().getColor(R.color.colorBlack));
                    String status = statusview.getText().toString();
                    if (status.equals("scheduled")) {
                        btnStart.setVisibility(View.VISIBLE);
                        btnStart.setText("Start");
                        btnEnd.setVisibility(View.VISIBLE);
                        btnEnd.setText("Cancel");
                        inpAttendence.setVisibility(View.GONE);
                    }
                    if ((status.equals("ongoing")) || (status.equals("late")) || (status.equals("started on time"))) {
                        btnStart.setText("End");

                        btnStart.setVisibility(View.VISIBLE);
                        btnEnd.setVisibility(View.GONE);

                        inpAttendence.setVisibility(View.VISIBLE);
                    }
                    if ((status.equals("completed")) || (status.equals("cancelled"))) {
                        btnStart.setVisibility(View.GONE);

                        btnEnd.setVisibility(View.GONE);

                        inpAttendence.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "on pause state. flag value set to false.");
        flag = false;
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
//        if (mGoogleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onDestroyView() {
        flag = false;
        Log.d(TAG, "on distroy state. flag value set to false.");
        super.onDestroyView();
    }

    private void spinnerClicked(Spinner spinner, int position, List<BatchStatusModel> list, List<MergeSheduleUniversity> mlist) {
        if (position > 0) {
            Toast.makeText(context, "University: " + list.get(position - 1).getUniversity_name(), Toast.LENGTH_LONG).show();
          MergeSheduleUniversity msu=null;
           for(MergeSheduleUniversity m:mlist)
           {
               if(m.getUniversity().getUniversity_name().equals(list.get(position-1).getUniversity_name()))
               {
                   msu=m;
                   break;
               }

           }

            fillData(list.get(position - 1),msu);

            selectposition = position;
        }
    }

    public void fillData(BatchStatusModel model, MergeSheduleUniversity mUniversity) {
//        university=view.findViewById(R.id.textView2);
//        location=view.findViewById(R.id.textView3);
//        start=view.findViewById(R.id.textView4);
//        end=view.findViewById(R.id.textView6);
//        attendence=view.findViewById(R.id.textView9);
//        address=view.findViewById(R.id.textView2);
//        statusview=view.findViewById(R.id.textView10);
//        btnStart=view.findViewById(R.id.button2);
//        btnMap=view.findViewById(R.id.imageButton);
//        inpAttendence=view.findViewById(R.id.editText2);
        university.setText(""+mUniversity.getUniversity().getUniversity_name());
        location.setText(""+mUniversity.getUniversity().getLocation());
        start.setText(model.getStart());
        String hour_minute=model.getStart();
        String[] hm=hour_minute.split(":");
        String am_pm="";
        if(Integer.parseInt(hm[0])>=12) {
            hm[0] = "" + (Integer.parseInt(hm[0]) - 12);
            am_pm="pm";
        }
        else am_pm="am";
        startTextView.setText(hm[0]+":"+hm[1]+" "+am_pm);
        end.setText(model.getEnd());
        String[] hm2=model.getEnd().split(":");
        String am_pm2="";
        if(Integer.parseInt(hm2[0])>=12) {
            hm[0] = "" + (Integer.parseInt(hm2[0]) - 12);
            am_pm2="pm";
        }
        else am_pm2="am";
        end.setText(hm2[0]+":"+hm2[1]+" "+am_pm2);
        attendence.setText("Not Input");
        address.setText(mUniversity.getUniversity().getAddress());
        statusview.setText(model.getStatus());
        //getJustUniversity(model);
        UniversityDetailsModel umodel=mUniversity.getUniversity();
        latlong.setText(umodel.getLat_long());
        setBatchId(model.getId());
        updateDistance();

    }
    public void updateDistance() {
        String targetLocation = latlong.getText().toString();
        if (!targetLocation.equals("")) {
            String[] locationString = targetLocation.split(",");
            Double lat = Double.parseDouble(locationString[0]);
            Double lon = Double.parseDouble(locationString[1]);
//Log.d("currentdis","lat1: "+lat1);
//Log.d("currentdis","lat2: "+lat);
            if (lat1 != 0 && lat != 0) {
                String dis = "" + String.format("%.2f", getDistanceInMiles(lat1, lon1, lat, lon)) + " km";
                distanceText.setText(dis);
                Log.d("currentdis", "MyCoordinate: " + lat1 + "," + lon1 + " Target: " +targetLocation);
            }
        }
    }
    private UniversityDetailsModel univerModel;
    public void setUniversity(UniversityDetailsModel umodel)
    {
        univerModel=umodel;
    }
    public UniversityDetailsModel getuniversityDetailsModel()
    {
        return this.univerModel;
    }
    public void getJustUniversity(final BatchStatusModel bmodel) {
final UniversityDetailsModel umodel=null;
        db.collection("university_details")
                .whereEqualTo("university_name", bmodel.getUniversity_name())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document != null) {
                                DocumentSnapshot doc2 = null;
                                for (DocumentSnapshot doc : document) {


                                    doc2 = doc;
                                }

                                //  UniversityDetailsModel umodel=new UniversityDetailsModel(""+document.get("address"),""+document.get("lat_long"),""+document.get("location"),""+document.get("university_name"));
                             setUniversity(doc2.toObject(UniversityDetailsModel.class));
                               // return getuniversityDetailsModel();
                            } else {
                                Log.d(TAG, "No such document named " + bmodel.getUniversity_name());
                            }
                        } else {
                            //Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


    }
    public void getUniversity(final BatchStatusModel bmodel) {

        db.collection("university_details")
                .whereEqualTo("university_name", bmodel.getUniversity_name())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document != null) {
                                DocumentSnapshot doc2 = null;
                                for (DocumentSnapshot doc : document) {

                                    Log.d(TAG, "DocumentSnapshot data:  " + doc.toObject(UniversityDetailsModel.class).toString());
                                    // setUniversityDetailsModel(umodel);
                                    Log.d("mergedata", "University " + doc.toObject(UniversityDetailsModel.class));
                                    doc2 = doc;
                                }
                                getTrainerDetails(bmodel, doc2.toObject(UniversityDetailsModel.class));
                                //  UniversityDetailsModel umodel=new UniversityDetailsModel(""+document.get("address"),""+document.get("lat_long"),""+document.get("location"),""+document.get("university_name"));
                                //umodel=document.getData().to
                                //
                            } else {
                                Log.d(TAG, "No such document named " + bmodel.getUniversity_name());
                            }
                        } else {
                            //Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


    }

    private void getTrainerDetails(final BatchStatusModel bmodel, final UniversityDetailsModel universityDetailsModel) {
        Log.d("mergedata", "Query should be trainer details and name:  " + bmodel.getTrainer_name());
        Task<QuerySnapshot> c = db.collection("trainer_details")
                .whereEqualTo("name", bmodel.getTrainer_name())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document != null) {
                                DocumentSnapshot doc2 = null;
                                for (DocumentSnapshot doc : document) {

                                    Log.d(TAG, "DocumentSnapshot data:  " + doc.toObject(TrainerDetailsModel.class).toString());
                                    // setUniversityDetailsModel(umodel);
                                    Log.d("mergedata1", "Trainer Details " + doc.toObject(TrainerDetailsModel.class).toString());
                                    doc2 = doc;

                                }

                                addMergeList(new MergeSheduleUniversity(bmodel, doc2.toObject(TrainerDetailsModel.class), universityDetailsModel));
                                //  UniversityDetailsModel umodel=new UniversityDetailsModel(""+document.get("address"),""+document.get("lat_long"),""+document.get("location"),""+document.get("university_name"));
                                //umodel=document.getData().to
                                //
                            } else {
                                Log.d(TAG, "No such document named " + bmodel.getUniversity_name());
                                Log.d("mergedata1", "No such document named " + bmodel.getTrainer_name());
                            }
                        } else {
                            //Log.d(TAG, "get failed with ", task.getException());
                            Log.d("mergedata1", "Error in trainer_details " + task.getException());
                        }
                    }
                });

    }

    public void setMergeData(BatchStatusModel batchStatusModel) {
        getUniversity(batchStatusModel);

        Log.d("mergedata", "University " + batchStatusModel.getUniversity_name());
    }

    public void addMergeList(MergeSheduleUniversity mergeSheduleUniversity) {
        mergeSheduleUniversityList.add(mergeSheduleUniversity);
        Log.d(TAG, "mergedata2: " + this.mergeSheduleUniversityList.size());
        Log.d(TAG, "mergedata2: " + mergeSheduleUniversityList.toString());
        setSpinner(dataList, mergeSheduleUniversityList);
        // isDataSet=true
    }

    synchronized void spinerFunc() {
        final List<BatchStatusModel> list = getDataList();

    }

    public boolean isTaskComplete = false;
    int currentsize = 0;


    public class TaskThread extends Thread {
        public void run() {

            // Log.d("mergedata3","Current,existing: "+currentsize+" , "+mergeSheduleUniversityList.size());

            handler.post(new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        if (currentsize != mergeSheduleUniversityList.size()) {
                            for (MergeSheduleUniversity m : mergeSheduleUniversityList) {
                                Log.d("mergedata3", "Merge: " + m.toString());
                            }
                            // if (mergeSheduleUniversityList != null)
                            // spinerFunc();

                            currentsize = mergeSheduleUniversityList.size();
                        }
                    }
                }

            });
        }


    }

    public double getDistanceInMiles(double lat1,double lon1,double lat2,double lon2) {

        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2);
        Log.d("currentdis",""+lat1+","+lon1);
        Log.d("currentdis",""+lat2+","+lon2);
        return distanceInMeters/1000;

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        if (location == null) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//        } else {
//            //If everything went fine lets get latitude and longitude
//            currentLatitude = location.getLatitude();
//            currentLongitude = location.getLongitude();
//           lat1=currentLatitude;
//           lon1=currentLongitude;
//            Toast.makeText(getActivity().getBaseContext(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
//    @Override
//    public void onLocationChanged(Location location) {
//         }


    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
lat1=currentLatitude;
lon1=currentLongitude;
        updateDistance();
//latlong.setText(lat1+","+lon1);
Log.d("currentdis",currentLatitude + "," + currentLongitude);
       // Toast.makeText(getActivity().getBaseContext(), currentLatitude + " Changed " + currentLongitude + "", Toast.LENGTH_LONG).show();

    }
    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    124);
        }
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                    125);
        }
    }
}
