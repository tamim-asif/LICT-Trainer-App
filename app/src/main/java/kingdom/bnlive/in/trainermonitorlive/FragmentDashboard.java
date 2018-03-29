package kingdom.bnlive.in.trainermonitorlive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Sk Faisal on 3/28/2018.
 */

public class FragmentDashboard extends Fragment {
    public Handler handler;
    private Spinner spinner;
    View view;
    Context context;
    private RecyclerView myListView;
    private RecyclerView.Adapter adapter;
    private List<BatchStatusModel> dataList;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore db;
    private String TAG="dashboardfragment";
    private String name;
    private String email;
    private String role;
    private TextView university;
    private TextView location;
    private TextView start;
    private TextView end;
    private TextView attendence;
    private TextView address;
    private TextView statusview;
    private Button btnStart;
    private Button btnEnd;
    private ImageView btnMap;
    private EditText inpAttendence;
    private boolean isDataSet=false;
    boolean flag;
    private  boolean isStartClicked;
    SharedPreferences sharedPreferences;
    String MyPreferences="monitordb";
    private String batchId;
     List<MergeSheduleUniversity> mergeSheduleUniversityList;
    private Bundle savedInstanceState;
    int datasize=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_dashboard,container,false);
        role="trainer";
        //sharedPreferences.
        handler=new Handler();

        mergeSheduleUniversityList =new ArrayList<>();
        spinner=view.findViewById(R.id.spinner);
        db=FirebaseFirestore.getInstance();
        context=getActivity().getBaseContext();
        sharedPreferences=context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        name=sharedPreferences.getString("name",null);
        email=sharedPreferences.getString("email",null);
        role=sharedPreferences.getString("role",null);
//        headerName.setText(""+sharedPreferences.getString("name",null));
//        headerEmail.setText(""+sharedPreferences.getString("email",null));
//        headerRole.setText(""+sharedPreferences.getString("role",null));

        currentuserrealtimeUpdate();
        //init all layout components
        initAll();

        return view;
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

    public void initAll()
{
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

    university=view.findViewById(R.id.textView2);
    location=view.findViewById(R.id.textView3);
    start=view.findViewById(R.id.textView4);
    end=view.findViewById(R.id.textView6);
    attendence=view.findViewById(R.id.textView9);
    address=view.findViewById(R.id.textview12);
    statusview=view.findViewById(R.id.textView10);
    btnStart=view.findViewById(R.id.button2);
    btnMap=view.findViewById(R.id.imageButton);
    inpAttendence=view.findViewById(R.id.editText2);
    btnEnd=view.findViewById(R.id.button3);
    String id="";
    btnStart.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(isStartClicked==false) {
                 setBatchId(statusview.getText().toString());
                 String status = "ongoing";
                updateStatus(status, getBatchId());
                Calendar calendar=Calendar.getInstance();
                int hour=calendar.get(Calendar.HOUR_OF_DAY);
                int minute=calendar.get(Calendar.MINUTE);
                btnStart.setText("End");
                String am_pm;
                if(hour>=12) {
                    am_pm = "pm";
                hour=hour-12;
                }
                else
                    am_pm="am";
                Snackbar.make(v,"Batch Started at: "+""+hour+" : "+minute+" "+am_pm,Snackbar.LENGTH_LONG).setAction(null,null).show();
                setStartClicked(true);
                btnEnd.setVisibility(View.GONE);
                inpAttendence.setVisibility(View.VISIBLE);

            }
          else{
                String input_attendence=inpAttendence.getText().toString();
                btnStart.setVisibility(View.GONE);
                updateStatus("completed",getBatchId(),input_attendence);
                statusview.setText("Batch Completed");
                statusview.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnStart.setVisibility(View.GONE);
                inpAttendence.setVisibility(View.GONE);
                setStartClicked(false);
                FragmentManager manager=getFragmentManager();
                manager.beginTransaction().replace(R.id.changeLayout,new FragmentDashboard()).commit();
            }
//            spinner.setOnItemSelectedListener(false);
        }
    });

    btnEnd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id=statusview.getText().toString();
            String status="cancelled";
            updateStatus(status,id);

        }
    });
    flag=true;
}
    public void updateStatus(final String status, String batch_id,String attendence)
    {
        final DocumentReference sfDocRef = db.collection("batch_status").document(batch_id);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                transaction.update(sfDocRef, "status", status);

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
public void updateStatus(final String status, String batch_id)
{
   final DocumentReference sfDocRef = db.collection("batch_status").document(batch_id);

    db.runTransaction(new Transaction.Function<Void>() {
        @Override
        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
            DocumentSnapshot snapshot = transaction.get(sfDocRef);

            transaction.update(sfDocRef, "status", status);

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
    public void currentuserrealtimeUpdate()
    {
        try {
            Log.d(TAG, "Call Realtime Update. Name: " + name);
            db.collection("batch_status")
                    .whereEqualTo("trainer_name", name)
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
        }catch (Exception e)
        {

        }
    }
public void setSpinner(List<BatchStatusModel> listModel, final List<MergeSheduleUniversity> mlist)
{
    final List<BatchStatusModel> list=listModel;
    Log.d("testmerge","Data: Item "+mlist.toString());
    String[] items = new String[list.size() + 1];
    items[0] = "Select Batch Code";
    int i = 1;
    for (MergeSheduleUniversity model : mlist) {
        items[i] = model.getStatusModel().getBatch_code();
        Log.d("testmerge","Info size: "+mlist.size());
        //i++;
    }
    if(list.size()==mlist.size())
    {
        int j = 1;
        for (MergeSheduleUniversity model : mlist) {
            items[i] = model.getStatusModel().getBatch_code();
            //Log.d("testmerge","Info size: "+mlist.size());
           j++;
        }
    }
    for (BatchStatusModel model : list) {
        items[i] = model.getBatch_code();
        Log.d("testmerge","Batch size: "+list.size());
        i++;
    }
    ArrayAdapter<String> arrayAdapter=null;
    if (flag == true) {

        arrayAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.select_dialog_item, items);


        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //   String s=arrayAdapter.getItem(position);
//                                        btnStart.setText("Start");
//                                        btnEnd.setText("End");
//                                        setStartClicked(false);
//                                        btnStart.setVisibility(View.VISIBLE);
//                                        btnEnd.setVisibility(View.VISIBLE);
                spinnerClicked(spinner, position, list,mlist);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
    @Override
    public void onPause() {
        Log.d(TAG,"on pause state. flag value set to false.");
        flag=false;
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        flag=false;
        Log.d(TAG,"on distroy state. flag value set to false.");
        super.onDestroyView();
    }

    private void spinnerClicked(Spinner spinner, int position, List<BatchStatusModel> list, List<MergeSheduleUniversity> mlist) {
        if(position>0){
        Toast.makeText(context,"University: "+list.get(position-1).getUniversity_name(),Toast.LENGTH_LONG).show();
        fillData(list.get(position-1),mlist.get(position-1));
        }
    }
    public void fillData(BatchStatusModel model,MergeSheduleUniversity mUniversity)
    {
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
        university.setText(model.getUniversity_name());
        location.setText(mUniversity.getUniversity().getLocation());
        start.setText(model.getStart());
        end.setText(model.getEnd());
        attendence.setText("Not Input");
        address.setText(mUniversity.getUniversity().getAddress());
        statusview.setText(model.getId());
    }
    public void getUniversity(final BatchStatusModel bmodel)
    {

        db.collection("university_details")
                .whereEqualTo("university_name",bmodel.getUniversity_name())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document != null) {
                                DocumentSnapshot doc2=null;
                                for(DocumentSnapshot doc:document){

                                    Log.d(TAG, "DocumentSnapshot data:  " + doc.toObject(UniversityDetailsModel.class).toString());
                                    // setUniversityDetailsModel(umodel);
                                    Log.d("mergedata", "University "+doc.toObject(UniversityDetailsModel.class));
                                 doc2=doc;
                                }
                                getTrainerDetails(bmodel,doc2.toObject(UniversityDetailsModel.class));
                                //  UniversityDetailsModel umodel=new UniversityDetailsModel(""+document.get("address"),""+document.get("lat_long"),""+document.get("location"),""+document.get("university_name"));
                                //umodel=document.getData().to
                                //
                            } else {
                                Log.d(TAG, "No such document named "+bmodel.getUniversity_name());
                            }
                        } else {
                            //Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


    }

    private void getTrainerDetails(final BatchStatusModel bmodel,final UniversityDetailsModel universityDetailsModel) {
        Log.d("mergedata", "Query should be trainer details and name:  "+bmodel.getTrainer_name());
       Task<QuerySnapshot> c= db.collection("trainer_details")
                .whereEqualTo("name",bmodel.getTrainer_name())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            if (document != null) {
                                DocumentSnapshot doc2=null;
                                for(DocumentSnapshot doc:document){

                                    Log.d(TAG, "DocumentSnapshot data:  " + doc.toObject(TrainerDetailsModel.class).toString());
                                    // setUniversityDetailsModel(umodel);
                                    Log.d("mergedata1", "Trainer Details "+doc.toObject(TrainerDetailsModel.class).toString());
                                   doc2=doc;

                                }

                                addMergeList(new MergeSheduleUniversity(bmodel,doc2.toObject(TrainerDetailsModel.class),universityDetailsModel));
                                //  UniversityDetailsModel umodel=new UniversityDetailsModel(""+document.get("address"),""+document.get("lat_long"),""+document.get("location"),""+document.get("university_name"));
                                //umodel=document.getData().to
                                //
                            } else {
                                Log.d(TAG, "No such document named "+bmodel.getUniversity_name());
                                Log.d("mergedata1", "No such document named "+bmodel.getTrainer_name());
                            }
                        } else {
                            //Log.d(TAG, "get failed with ", task.getException());
                            Log.d("mergedata1", "Error in trainer_details "+ task.getException());
                        }
                    }
                });

    }

    public void setMergeData(BatchStatusModel batchStatusModel) {
                getUniversity(batchStatusModel);

        Log.d("mergedata", "University "+batchStatusModel.getUniversity_name());
    }
    public void addMergeList(MergeSheduleUniversity mergeSheduleUniversity)
    {
        mergeSheduleUniversityList.add(mergeSheduleUniversity);
        Log.d(TAG, "mergedata2: " + this.mergeSheduleUniversityList.size());
        setSpinner(dataList,mergeSheduleUniversityList);
       // isDataSet=true
    }
    synchronized void spinerFunc ()
    {
     final List<BatchStatusModel> list=getDataList();

    }
    public boolean isTaskComplete=false;
    int currentsize=0;
    public class TaskThread extends Thread
    {
        public void run()
        {

               // Log.d("mergedata3","Current,existing: "+currentsize+" , "+mergeSheduleUniversityList.size());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            while (true) {
                                if(currentsize!=mergeSheduleUniversityList.size()) {
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
}
