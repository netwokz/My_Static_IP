package com.netwokz.mystaticip;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

public class MainListView extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private List<StaticIpRecord> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        data = getStaticIpList();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CustomAdapter(this, data);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showNewIpDialog());

    }

    public void showNewIpDialog() {
        FragmentManager fm = getFragmentManager();
        NewStaticIpDialog newDialogFragment = NewStaticIpDialog.newInstance();
        newDialogFragment.setOnDismissListener(dialog -> updateIpListView());
        newDialogFragment.show(fm, "fragment_new_ip");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
//                displayMessage("Edit Item");
                showEditIpDialog(item.getGroupId());
                return true;
            case 2:
//                displayMessage("Delete Item");
                Log.d("MainActivity", "Delete Item " + item.getGroupId() + " ID: " + data.get(item.getGroupId()).getId());
                deleteIpRecord(data.get(item.getGroupId()).getId(), item.getGroupId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteIpRecord(final long recordId, final int position) {
        View view = findViewById(R.id.root_view);
        final StaticIpRecord oldRecord = StaticIpRecord.findById(StaticIpRecord.class, recordId);
        data.remove(position);
        oldRecord.delete();
        adapter.notifyItemRemoved(position);
        final Snackbar snackbar = Snackbar
                .make(view, "Record " + oldRecord.getName() + " is removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                    new StaticIpRecord(oldRecord).save();
                    data.add(position, oldRecord);
                    adapter.notifyItemInserted(position);
                    Snackbar snackbar1 = Snackbar.make(view, "Record " + oldRecord.mName + " restored.", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                });
        snackbar.show();
    }

    public void showEditIpDialog(int id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", data.get(id).getId());
        FragmentManager fm = getFragmentManager();
        EditIpDialog editIpDialog = EditIpDialog.newInstance();
        editIpDialog.setArguments(bundle);
        editIpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateIpListView();
            }
        });
        editIpDialog.show(fm, "fragment_edit_ip");
    }

    public void updateIpListView() {
        data.clear();
        data.addAll(getStaticIpList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
//                adapter.notifyDataSetChanged();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(MainListView.this, SettingsActivity.class);
                startActivity(intent);
//                switchFragment(SettingsFragment.class);
                break;
            case R.id.action_add_generated_car:
                data.add(generateRandomEntry());
                adapter.notifyDataSetChanged();
                break;
            case R.id.action_clear_db:
                StaticIpRecord.deleteAll(StaticIpRecord.class);
//                data.clear();
//                adapter.notifyDataSetChanged();
            default:
                break;
        }
        return true;
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
