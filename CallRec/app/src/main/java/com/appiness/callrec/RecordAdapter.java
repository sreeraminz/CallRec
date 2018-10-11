package com.appiness.callrec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.List;
import static android.support.v4.content.FileProvider.getUriForFile;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {

    List<CallDetails> callDetails;
    Context context;
    SharedPreferences pref;
    String checkDate = "";

    public RecordAdapter(List<CallDetails> callDetails, Context context) {
        this.callDetails = callDetails;
        this.context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView number, time, date,name;

        public MyViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date1);
            name = itemView.findViewById(R.id.name1);
            number = itemView.findViewById(R.id.num);
            time = itemView.findViewById(R.id.time1);
        }

        public void bind(final String dates, final String number, final String times) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Clicked on " + number, Toast.LENGTH_SHORT).show();
                    String path = Environment.getExternalStorageDirectory() + "/My Records/" + dates + "/" + number + "_" + times + ".mp4"  ;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(path);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(getUriForFile(context,"com.example.vs00481543.phonecallrecorder",file), "audio/*");
                    context.startActivity(intent);
                    pref.edit().putBoolean("pauseStateVLC",true).apply();
                    }
            });
        }
    }

    @Override
    public RecordAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder viewHolder = null;
        LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0:
                View v1 = layoutInflator.inflate(R.layout.record_list, parent, false);
                viewHolder = new MyViewHolder(v1);
                break;
            case 2:
                View v3 = layoutInflator.inflate(R.layout.date_layout, parent, false);
                viewHolder = new MyViewHolder(v3);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecordAdapter.MyViewHolder holder, int position) {

        CallDetails cd1 = callDetails.get(position);
        String n=cd1.getNum();
        String name=new CommonMethods().getContactName(n,context);
        String name2="Unknown";
        holder.bind(cd1.getDate1(), cd1.getNum(), cd1.getTime1());
        switch (getItemViewType(position)) {
            case 0:
                if(name!=null && !name.equals("")) {
                    holder.name.setText(name);
                    holder.name.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    holder.name.setText(name2);
                    holder.name.setTextColor(context.getResources().getColor(R.color.red));
                }
                holder.number.setText(callDetails.get(position).getNum());
                holder.time.setText(callDetails.get(position).getTime1());
                break;
            case 2:
                holder.date.setText(callDetails.get(position).getDate1());
                if(name!=null && !name.equals("")) {
                    holder.name.setText(name);
                    holder.name.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    holder.name.setText(name2);
                    holder.name.setTextColor(context.getResources().getColor(R.color.red));
                }
                holder.number.setText(callDetails.get(position).getNum());
                holder.time.setText(callDetails.get(position).getTime1());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return callDetails.size();
    }

    public int getItemViewType(int position) {
        CallDetails cd = callDetails.get(position);
        String dt = cd.getDate1();
        try {
            if (position!=0 && cd.getDate1().equalsIgnoreCase(callDetails.get(position - 1).getDate1())) {
                checkDate = dt;

                return 0;
            } else {
                checkDate = dt;

                return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }
}
