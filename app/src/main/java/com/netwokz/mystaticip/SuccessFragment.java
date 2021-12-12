package com.netwokz.mystaticip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class SuccessFragment extends Fragment {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private List<StaticIpRecord> data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        data = getStaticIpList();
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CustomAdapter(getContext(), data);
        recyclerView.setAdapter(adapter);
    }

    private List<StaticIpRecord> getStaticIpList() {
        List<StaticIpRecord> mList = StaticIpRecord.listAll(StaticIpRecord.class);
        return mList;
    }

    public String getRandomIp() {
        String mRandomIp;
        Random random = new Random();
        int x = random.nextInt(155) + 100;
        mRandomIp = "192.168.0." + x;
        return mRandomIp;
    }

    public String getRandomName() {
        String mType;
        String types[] = {"My Main PC", "My Laptop", "My Pixel 3 XL", "Garage Pi", "Pi Hole", "Other"};
        Random rand = new Random();
        mType = types[rand.nextInt(types.length)];
        return mType;
    }

    public String getRandomMacAddress() {
        String mac = "";
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            int n = r.nextInt(255);
            mac += String.format("%02x", n);
        }
        String finishedMac = mac.toUpperCase().replaceAll("..(?!$)", "$0:");

        return finishedMac;
    }

    public StaticIpRecord generateRandomEntry() {
        String mName = getRandomName();
        int mType;
        switch (mName) {
            case "My Main PC":
                mType = 1;
                break;
            case "My Laptop":
                mType = 2;
                break;
            case "My Pixel 3 XL":
                mType = 3;
                break;
            case "Garage Pi":
                mType = 4;
                break;
            case "Pi Hole":
                mType = 5;
                break;
            default:
                mType = 6;
        }

        StaticIpRecord record = new StaticIpRecord(getRandomIp(), getRandomMacAddress(), mType, mName);
        record.save();
        return record;
    }
}
