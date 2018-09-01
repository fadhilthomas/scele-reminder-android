package com.wibudev.scere.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wibudev.scere.R;
import com.wibudev.scere.model.Tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Tasks> TaskList;
    private ArrayList<String> listDone = new ArrayList<>();

    public RecyclerViewAdapter(Context context, List<Tasks> TempList) {
        this.TaskList = TempList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Tasks taskInfo = TaskList.get(position);
        loadDone();
        holder.tvNama.setText(taskInfo.getName());
        holder.tvMatkul.setText(taskInfo.getMatkul());
        holder.tvTanggal.setText(taskInfo.getTgl());

        if(listDone.contains(taskInfo.getLink())){
            holder.cvTask.setCardBackgroundColor(Color.parseColor("#b7495e"));
            holder.tvMatkul.setTextColor(Color.parseColor("#ffffff"));
            holder.tvNama.setTextColor(Color.parseColor("#ffffff"));
            holder.tvTanggal.setTextColor(Color.parseColor("#ffffff"));
            holder.tvMatkul.setText(taskInfo.getMatkul()+"\n\n[Sudah Dikerjakan]");
        }else{
            holder.cvTask.setCardBackgroundColor(Color.parseColor("#ffffff"));
            holder.tvMatkul.setTextColor(Color.parseColor("#aaaaaa"));
            holder.tvNama.setTextColor(Color.parseColor("#555555"));
            holder.tvTanggal.setTextColor(Color.parseColor("#C72B43"));
        }
    }

    @Override
    public int getItemCount() {
        return TaskList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNama, tvMatkul, tvTanggal;
        public CardView cvTask;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNama);
            tvMatkul = itemView.findViewById(R.id.tvMatkul);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            cvTask = itemView.findViewById(R.id.cvTask);

            final Typeface custom_font = Typeface.createFromAsset(itemView.getContext().getAssets(),  "fonts/font.ttf");
            final Typeface custom_font_bold = Typeface.createFromAsset(itemView.getContext().getAssets(),  "fonts/fontBold.ttf");
            tvNama.setTypeface(custom_font);
            tvMatkul.setTypeface(custom_font_bold);
            tvTanggal.setTypeface(custom_font);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(itemView.getContext(), R.style.AppCompatAlertDialogStyle);
                    alertDialogBuilder
                            .setMessage(TaskList.get(getAdapterPosition()).getName())
                            .setCancelable(true)
                            .setPositiveButton("Buka", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Uri uri = Uri.parse(TaskList.get(getAdapterPosition()).getLink());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            });
                    if(listDone.contains(TaskList.get(getAdapterPosition()).getLink())){
                        alertDialogBuilder
                                .setNegativeButton("Belum Dikerjakan", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        loadDone();
                                        listDone.remove(TaskList.get(getAdapterPosition()).getLink());
                                        saveDone();
                                        dialog.dismiss();
                                        notifyDataSetChanged();
                                    }
                                });
                    }else {
                        alertDialogBuilder
                                .setNegativeButton("Sudah Dikerjakan", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        loadDone();
                                        listDone.add(TaskList.get(getAdapterPosition()).getLink());
                                        saveDone();
                                        dialog.dismiss();
                                        notifyDataSetChanged();
                                    }
                                });
                    }
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    TextView pesan = alertDialog.findViewById(android.R.id.message);
                    pesan.setTextSize(15);
                    pesan.setTextColor(itemView.getContext().getResources().getColor(R.color.colorSc));
                    pesan.setTypeface(custom_font);

                    Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                    b.setTypeface(custom_font_bold);

                    Button c = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    c.setTextColor(itemView.getContext().getResources().getColor(R.color.colorSc2));
                    c.setTypeface(custom_font_bold);
                }
            });
        }
    }

    private void saveDone(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listDone);
        prefsEditor.putString("done", json);
        prefsEditor.apply();
    }

    private void loadDone(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("done", "");
        String[] listsDone = gson.fromJson(json, String[].class);
        if(listsDone == null){
            listsDone = new String[1];
            listsDone[0] = "";
        }
        List<String> lists = Arrays.asList(listsDone);
        listDone = new ArrayList<>(lists);
    }
}
