package com.example.teamproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.teamproject.R;
import com.example.teamproject.activity.HotelGuestActivity;
import com.example.teamproject.activity.HotelListActivity;
import com.example.teamproject.activity.MainActivity;
import com.example.teamproject.activity.MotelActivity;
import com.example.teamproject.adapter.ImageAdapter;
import com.example.teamproject.data.StoreInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FragHome extends Fragment {

    private Button btnMotel;
    private RecyclerView recyclerImage;
    private SearchView mainSearchView;
    private static ImageAdapter adapter;
    private static ArrayList<StoreInfo> info = new ArrayList<>();
    private ArrayList<StoreInfo> searchList = new ArrayList<>();

    private FirebaseDatabase db;
    private DatabaseReference dbRf;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.frag_home, container, false);



        findViewByIdFunc(view);

        if(info.size()==0){
            info = MainActivity.getStoreInfoData();
        }

        mainSearchView.setMaxWidth(Integer.MAX_VALUE);


        adapter = new ImageAdapter(getActivity(), info);
        recyclerImage.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerImage.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position)-> {
            Intent intent = new Intent(getActivity(), HotelGuestActivity.class);
            String name = info.get(position).getStoreName();
            intent.putExtra("name", name);
            startActivity(intent);
            });

        mainSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList.clear();
                getSearchData(query);
                Intent intent = new Intent(getActivity(), HotelListActivity.class);
                intent.putExtra("list", searchList);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });



        eventHandlerFunc();






        return view;
    }

    private void getSearchData(String query) {
        for(StoreInfo s : info){
            if(s.getStoreName().contains(query) || s.getLocation_tag().contains(query)){
                searchList.add(s);
            }
        }
    }







    private void eventHandlerFunc() {

        btnMotel.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MotelActivity.class);
            startActivity(intent);
        });
    }

    private void findViewByIdFunc(ViewGroup view) {
        btnMotel = view.findViewById(R.id.btnMotel);
        mainSearchView = view.findViewById(R.id.mainSearchView);

        recyclerImage = view.findViewById(R.id.recyclerImage);
    }

    public static ArrayList<StoreInfo> getList(String[] tag){
        ArrayList<StoreInfo> list = new ArrayList<>();
        for (StoreInfo s : info){
            for(int i = 0; i < tag.length; i++){
                if(tag[i].equals(s.getLocation_tag())){
                    list.add(s);
                }
            }
        }

        return list;
    }

    public static ArrayList<StoreInfo> getSteamedList(String[] tag){
        ArrayList<StoreInfo> list = new ArrayList<>();
        for (StoreInfo s : info){
            for(int i = 0; i < tag.length; i++){
                if(tag[i].equals(s.getStoreName())){
                    list.add(s);
                }
            }
        }


        return list;
    }

    public static void changeInfoList(StoreInfo newStoreInfo, int index){
        info.remove(index);
        info.add(index, newStoreInfo);
        adapter.notifyDataSetChanged();
    }

    public static void setInfo(ArrayList<StoreInfo> infoList){
        info = infoList;
    }
}
