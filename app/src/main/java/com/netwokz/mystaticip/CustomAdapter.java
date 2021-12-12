package com.netwokz.mystaticip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<StaticIpViewHolder> {

    List<StaticIpRecord> mStaticIpList;
    Context mContext;

    public CustomAdapter(Context context, List<StaticIpRecord> staticIps) {
        mContext = context;
        mStaticIpList = staticIps;
    }

    @Override
    public StaticIpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new StaticIpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StaticIpViewHolder holder, final int i) {
        final StaticIpRecord tempRecord = mStaticIpList.get(i);
        int type = tempRecord.mType;
//        Log.d("CustomAdapter", String.valueOf(type));
        switch (type) {
            case 1:
                holder.mType.setImageResource(R.drawable.ic_server_24dp);
                break;
            case 2:
                holder.mType.setImageResource(R.drawable.ic_desktop_windows_24px);
                break;
            case 3:
                holder.mType.setImageResource(R.drawable.ic_laptop_24px);
                break;
            case 4:
                holder.mType.setImageResource(R.drawable.ic_phone_android_24px);
                break;
            case 5:
                holder.mType.setImageResource(R.drawable.ic_devices_other_24px);
                break;
            case 6:
                holder.mType.setImageResource(R.drawable.ic_devices_other_24px);
                break;
            default:
                holder.mType.setImageResource(R.drawable.ic_device_unknown_24px);
        }
        holder.mIp.setText(tempRecord.mIpAddress);
        holder.mMac.setText(tempRecord.mMacAddress);
        holder.mName.setText(tempRecord.mName);
//        holder.mDate.setText(tempRecord.mDateAdded);
//        holder.mCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("CustomAdapter", "cardOnClickListener");
//                Log.d("CustomAdapter", tempRecord.mIpAddress);
//                Log.d("CustomAdapter", String.valueOf(i));
//            }
//        });
//        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Log.d("CustomAdapter", "cardOnLongClickListener");
//                return true;
//            }
//        });
    }

    @Override
    public int getItemCount() {
//        Log.d("CustomAdapter", String.valueOf(mStaticIpList.size()));
        return mStaticIpList.size();
    }
}
