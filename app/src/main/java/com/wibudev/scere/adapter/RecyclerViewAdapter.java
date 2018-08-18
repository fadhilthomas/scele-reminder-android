package com.wibudev.scere.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wibudev.scere.R;
import com.wibudev.scere.model.Tasks;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Tasks> TaskList;

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

        holder.tvNama.setText(taskInfo.getName());
        holder.tvMatkul.setText(taskInfo.getMatkul());
        holder.tvTanggal.setText(taskInfo.getTgl());
    }

    @Override
    public int getItemCount() {
        return TaskList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNama, tvMatkul, tvTanggal;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNama);
            tvMatkul = itemView.findViewById(R.id.tvMatkul);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);

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
                            .setMessage("Apakah kamu ingin membuka link tugas?")
                            .setCancelable(true)
                            .setPositiveButton("Buka",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    Uri uri = Uri.parse(TaskList.get(getAdapterPosition()).getLink());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Batal",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.dismiss();
                                }
                            });
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
}
