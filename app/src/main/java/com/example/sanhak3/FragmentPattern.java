package com.example.sanhak3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentPattern extends Fragment {

    private Button showBtn;
    private Button inputBtn;
    private TextView showTotal;


    // db에 저장되어 있는 값들을 담는 ArrayList
    private ArrayList<InoutData> inoutDatas = new ArrayList<>();


    //piechart 불러오기
    private PieChart pieChart;

    //category가 있을 경우 해당 카테고리에 값을 더하고
    //없을 경우에는 카테고리를 추가해준다.
    boolean isCategory = false;

    // PieChart에 표시하기 위한 변수들..
    private float food = 0f;
    private float traffic = 0f;
    private float academy = 0f;
    private float market = 0f;
    private float cafe = 0f;
    private float etc = 0f;

    //input
    private TextView tv;
    private EditText et;
    private Spinner s;
    private Button saveBtn;
    private Button backBtn;
    private TextView mosttx;
    String categori[] = {"교통","학원","편의점","카페","기타"};
    String cate;

    ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inoutDatas.clear();
        inoutDatas=ReadInoutData();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pattern, container, false);
        showBtn = view.findViewById(R.id.show_piechart);
        inputBtn = view.findViewById(R.id.input_data);
        showTotal = view.findViewById(R.id.total);

        s = view.findViewById(R.id.category);
        tv = view.findViewById(R.id.using_money);
        et = view.findViewById(R.id.input_price);
        saveBtn = view.findViewById(R.id.saveBtn);
        mosttx = view.findViewById(R.id.mosttext);
        setManuFactureData();
        drawPieChart(view);
        Collections.sort(yValues,new MyComparator());
        for(int i = 0; i < inoutDatas.size(); i++){
            if(yValues.get(i).getLabel().equals("음식")) {
                mosttx.setText("음식에 돈을 많이 쓰셨습니다. 다이어트가 필요해요.");
                break;
            }
            else if (yValues.get(i).getLabel().equals("교통")) {
                mosttx.setText("너무 많이 돌아다니셨어요. 교통비를 절약하셔야 헤요");
                break;
            }
            else if (yValues.get(i).getLabel().equals("학원")) {
                mosttx.setText("학원을 많이 다니시네요. 독학으로 돈을 절약해보세요");
                break;
            }
            else if (yValues.get(i).getLabel().equals("편의점")) {
                mosttx.setText("군것질을 많이 하시네요 편의점에서 쓰는 돈을 줄여야 해요.");
                break;
            }
            else if (yValues.get(i).getLabel().equals("카페")) {
                mosttx.setText("카페인을 너무 많이 섭취하고 있어요. 조금 줄여야 해요");
                break;
            }
        }

        // spinner를 통해 값 선택 후 저장
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tv.setText(parent.getItemAtPosition(position).toString() + "에 들어간 돈 ");
                cate=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inoutDatas.add(new InoutData("temp",cate,et.getText().toString()));
                SaveInoutData(inoutDatas);
                setManuFactureData();
                drawPieChart(view);
                view.findViewById(R.id.input_inout_data).setVisibility(View.GONE);
                view.findViewById(R.id.piechart).setVisibility(View.VISIBLE);
                mosttx.setVisibility(View.VISIBLE);

            }
        });

        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(view.getContext(), SaveDataActivity.class));
                view.findViewById(R.id.piechart).setVisibility(View.GONE);
                mosttx.setVisibility(View.GONE);
                view.findViewById(R.id.input_inout_data).setVisibility(View.VISIBLE);
            }
        });

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inoutDatas.size() != 0) {
                    int temp=0;
                    for(int i=0;i<inoutDatas.size();i++)
                        temp+=Integer.parseInt(inoutDatas.get(i).getPrice());
                    showTotal.setText("총 사용한 돈 : " + temp);
                } else{
                    showTotal.setText("총 사용한 돈 : 0");
                }
                view.findViewById(R.id.input_inout_data).setVisibility(View.GONE);
                view.findViewById(R.id.piechart).setVisibility(View.VISIBLE);
                mosttx.setVisibility(View.VISIBLE);
            }
        });


        return view;
    }

    public void setManuFactureData(){
        food = 0f;
        traffic = 0f;
        academy = 0f;
        market = 0f;
        cafe = 0f;
        etc = 0f;
        for(int i = 0; i < inoutDatas.size(); i++){
            if(inoutDatas.get(i).getCategory().equals("음식")) {
                food += Float.parseFloat(inoutDatas.get(i).getPrice());
            }
            else if (inoutDatas.get(i).getCategory().equals("교통"))
                traffic += Float.parseFloat(inoutDatas.get(i).getPrice());
            else if (inoutDatas.get(i).getCategory().equals("학원"))
                academy += Float.parseFloat(inoutDatas.get(i).getPrice());
            else if (inoutDatas.get(i).getCategory().equals("편의점"))
                market += Float.parseFloat(inoutDatas.get(i).getPrice());
            else if (inoutDatas.get(i).getCategory().equals("카페"))
                cafe += Float.parseFloat(inoutDatas.get(i).getPrice());
            else if (inoutDatas.get(i).getCategory().equals("기타"))
                etc += Float.parseFloat(inoutDatas.get(i).getPrice());
            else{

            }
        }
    }

    public void drawPieChart(View v){
        pieChart = (PieChart)v.findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        Log.d("Tag >>> ", "Food : " + food);
        yValues.clear();
        if (food > 0)
            yValues.add(new PieEntry(food,"음식"));
        if (traffic > 0)
            yValues.add(new PieEntry(traffic,"교통"));
        if (academy > 0)
            yValues.add(new PieEntry(academy,"학원"));
        if (market > 0)
            yValues.add(new PieEntry(market,"편의점"));
        if (cafe > 0)
            yValues.add(new PieEntry(cafe,"카페"));
        if (etc > 0)
            yValues.add(new PieEntry(etc,"기타"));

        Description description = new Description();
        description.setText("소비패턴 분석 결과 (단위 : %)"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        PieDataSet dataSet = new PieDataSet(yValues,"소비패턴");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
    }
    private ArrayList<InoutData> ReadInoutData() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("MyInout", "EMPTY");
        Type type = new TypeToken<ArrayList<InoutData>>(){
        }.getType();
        ArrayList<InoutData> arrayList = gson.fromJson(json, type);
        return arrayList;
    }
    private void SaveInoutData(ArrayList<InoutData> nameservs) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(nameservs);
        editor.putString("MyInout", json);
        editor.commit();
    }
    class MyComparator implements Comparator<PieEntry> {
        @Override
        public int compare(PieEntry p1, PieEntry p2) {
            if (p1.getValue() < p2.getValue()) {
                return 1; // x에 대해서는 오름차순
            }
            else if (p1.getValue() == p2.getValue()) {
                if (p1.getValue() < p2.getValue()) { // y에 대해서는 내림차순
                    return 1;
                }
            }
            return -1;
        }
    }
}