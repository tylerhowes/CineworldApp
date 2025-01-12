package com.example.cineworldapp;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class ScreenCardAdapter extends RecyclerView.Adapter<ScreenCardAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ScreenCardModel> screenCardModelArrayList;

    public ScreenCardAdapter(Context context, ArrayList<ScreenCardModel> screenCardModelArrayList) {
        this.context = context;
        this.screenCardModelArrayList = screenCardModelArrayList;
    }

    @NonNull
    @Override
    public ScreenCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_screen_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenCardAdapter.ViewHolder holder, int position) {
        ScreenCardModel screenCardModel = screenCardModelArrayList.get(position);
        holder.screen.setText(screenCardModel.getScreen());
        holder.title.setText(screenCardModel.getTitle());
        holder.startTime.setText(screenCardModel.getStartTime());
        holder.featureTime.setText(screenCardModel.getFeatureTime());
        holder.finishTime.setText(screenCardModel.getFinishTime());
    }

    @Override
    public int getItemCount() {
        return screenCardModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView screen;
        private final TextView title;
        private final TextView startTime;
        private final TextView featureTime;
        private final TextView finishTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            screen = itemView.findViewById(R.id.screenNumber);
            title = itemView.findViewById(R.id.filmTitle);
            startTime = itemView.findViewById(R.id.startTime);
            featureTime = itemView.findViewById(R.id.featureTime);
            finishTime = itemView.findViewById(R.id.endTime);
        }
    }
}
