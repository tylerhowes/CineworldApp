package com.example.cineworldapp;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class ScreenCardAdapter extends RecyclerView.Adapter<ScreenCardAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ScreenCardModel> screenCardModelArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userInitials;

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


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials =  documentSnapshot.getString("initials");
                });

        holder.check1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.layout_screen_check_popup, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                popupView.findViewById(R.id.completeCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.check1Button.setBackgroundColor(context.getResources().getColor(R.color.green));
                        popupWindow.dismiss();
                        holder.check1Initials.setText(userInitials);
                        //Add code to save completed check in firebase
                    }
                });

                popupView.findViewById(R.id.cancelCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do something
                        popupWindow.dismiss();
                    }
                });
            }
        });

        holder.check2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.layout_screen_check_popup, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                popupView.findViewById(R.id.completeCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.check2Button.setBackgroundColor(context.getResources().getColor(R.color.green));
                        popupWindow.dismiss();
                        holder.check2Initials.setText(userInitials);
                        //Add code to save completed check in firebase
                    }
                });

                popupView.findViewById(R.id.cancelCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do something
                        popupWindow.dismiss();
                    }
                });
            }
        });

        holder.check3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.layout_screen_check_popup, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                popupView.findViewById(R.id.completeCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.check3Button.setBackgroundColor(context.getResources().getColor(R.color.green));
                        popupWindow.dismiss();
                        holder.check3Initials.setText(userInitials);

                        //Add code to save completed check in firebase
                    }
                });

                popupView.findViewById(R.id.cancelCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do something
                        popupWindow.dismiss();
                    }
                });
            }
        });
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

        private final Button check1Button;
        private final Button check2Button;
        private final Button check3Button;

        private final TextView check1Initials;
        private final TextView check2Initials;
        private final TextView check3Initials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            screen = itemView.findViewById(R.id.screenNumber);
            title = itemView.findViewById(R.id.filmTitle);
            startTime = itemView.findViewById(R.id.startTime);
            featureTime = itemView.findViewById(R.id.featureTime);
            finishTime = itemView.findViewById(R.id.endTime);
            check1Button = itemView.findViewById(R.id.check1);
            check2Button = itemView.findViewById(R.id.check2);
            check3Button = itemView.findViewById(R.id.check3);
            check1Initials = itemView.findViewById(R.id.check1Initials);
            check2Initials = itemView.findViewById(R.id.check2Initials);
            check3Initials = itemView.findViewById(R.id.check3Initials);
        }
    }
}
