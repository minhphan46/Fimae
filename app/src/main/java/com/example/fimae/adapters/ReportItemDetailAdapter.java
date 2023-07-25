package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.databinding.ReportDetailBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.ReportDetail;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReportItemDetailAdapter extends  FirestoreAdapter<ReportItemDetailAdapter.ViewHolder> {
    ArrayList<ReportDetail> reportDetails;
    private ReportItemDetailAdapter.IClickCardUserListener iClickCardUserListener;
    private Context context;

    public ReportItemDetailAdapter(Query query) {
        super(query);
    }

    public interface IClickCardUserListener {
        void onClickUser(Fimaers user);
    }
    public void setData(Context context, ReportItemDetailAdapter.IClickCardUserListener inIClickCardUserListener) {
        this.context = context;
        this.iClickCardUserListener = inIClickCardUserListener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ReportDetailBinding binding;
        public ViewHolder(ReportDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ReportItemDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReportDetailBinding binding = ReportDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReportItemDetailAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportItemDetailAdapter.ViewHolder holder, int position) {
        ReportDetailBinding binding = holder.binding;
        ReportDetail reportDetail = reportDetails.get(position);
        FimaerRepository.getInstance().getFimaerById(reportDetail.getReporter()).addOnCompleteListener(new OnCompleteListener<Fimaers>() {
            @Override
            public void onComplete(@NonNull Task<Fimaers> task) {
                Fimaers fimaer= task.getResult();
                binding.itemUserTvName.setText(fimaer.getName());
                Picasso.get().load(fimaer.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(binding.itemUserAvatarView);
                binding.reportDescription.setText(reportDetail.getReason());
                binding.itemUserTvAge.setText(String.valueOf(fimaer.calculateAge()));
                binding.itemUserLayoutGenderAge.setBackgroundResource(fimaer.isGender() ? R.drawable.shape_gender_border_blue : R.drawable.shape_gender_border_pink);
                binding.itemUserIcGender.setImageResource(fimaer.isGender() ? R.drawable.ic_male : R.drawable.ic_female);
            }
        });
    }

    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots, ArrayList<DocumentChange> documentChanges) {
        if(reportDetails == null) reportDetails = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            ReportDetail reportDetail = documentSnapshot.toObject(ReportDetail.class);
            assert reportDetail != null;
                reportDetails.add(reportDetail);
        }
    }

    @Override
    public int getItemCount() {
        if (reportDetails != null) {
            return reportDetails.size();
        }
        return 0;
    }
}
