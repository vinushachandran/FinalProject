package com.example.elephanttrackapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationSummary extends AppCompatActivity {

    Spinner spinner;
    ArrayList<String> list;
    DatabaseReference spinnerRef;
    ArrayAdapter<String> adapter;
    ValueEventListener listener;

    Button search;

    //RecyclerView
    RecyclerView recyclerView;
    Location_Adapter Radapter;

    /*ArrayList<LocationData> Rlist;
    DatabaseReference databaseReference;
    Location_Adapter myAdapter;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LocationSummary.this,Home.class));
        finish();
    }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_summary);
        //RecyclerView
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<LocationData> options=
                new FirebaseRecyclerOptions.Builder<LocationData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("{devID=D01, eleID=E01}"),LocationData.class)
                        .build();

        Radapter=new Location_Adapter(options);
        recyclerView.setAdapter(Radapter);

        //RecyclerView Upper
        spinner=findViewById(R.id.spinner);
        spinnerRef= FirebaseDatabase.getInstance().getReference("Elephants Details");
        list=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);
        fetchData();

        /*search=findViewById(R.id.btnSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*RecyclerView
                recyclerView=findViewById(R.id.recyclerview2);
                databaseReference= FirebaseDatabase.getInstance().getReference("{devID=D01, eleID=E01}");
                Rlist=new ArrayList<>();
                recyclerView.setLayoutManager(new LinearLayoutManager(LocationSummary.this));
                myAdapter=new Location_Adapter(LocationSummary.this,Rlist);
                recyclerView.setAdapter(myAdapter);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            LocationData lo= dataSnapshot.getValue(LocationData.class);
                            Rlist.add(lo);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                recyclerview End
            }
        });
*/
    }
    //RecyclerView
    @Override
    protected void onStart() {
        super.onStart();
        Radapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Radapter.stopListening();
    }
    //recyclerView
    public void fetchData(){
        listener=spinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mydata: snapshot.getChildren())
                    list.add(mydata.getValue().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}