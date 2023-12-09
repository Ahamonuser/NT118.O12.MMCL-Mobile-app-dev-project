package com.example.loginapptest;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class GraphFragment extends Fragment {
    String token = "";
    static String selectedItem;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        // Lấy token từ bundle
        Bundle data = getArguments();
        token = data.getString("token");
        Log.d("Graph", token);

        //Xuất chart
        Button openchart = (Button) v.findViewById(R.id.btn_Chart);
        Spinner AssetName = (Spinner) v.findViewById(R.id.txt_AssetName);
        EditText AttributeName = (EditText) v.findViewById(R.id.txt_AttributeName);
        EditText start = (EditText) v.findViewById(R.id.txt_Start);
        EditText end = (EditText) v.findViewById(R.id.txt_End);
        String[] AssetNameList = {"DHT11 Asset", "Default Weather", "Weather Asset"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(), R.layout.custom_spinner_item, AssetNameList);
        AssetName.setAdapter(adapter);
        AssetName.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                selectedItem = AssetName.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
            }
        });
        openchart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), Chart.class);
                intent.putExtra("token",token);
                intent.putExtra("AssetName", selectedItem);
                intent.putExtra("AttributeName", AttributeName.getText().toString());
                intent.putExtra("start", start.getText().toString());
                intent.putExtra("end", end.getText().toString());
                startActivity(intent);
            }
        });
        return v;
    }
}