package com.example.fimae.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.activities.AdminReportActivity;
import com.example.fimae.activities.AminDetailReportActivity;
import com.example.fimae.databinding.ReportItemBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Post;
import com.example.fimae.models.ReportAdapterItem;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.repository.ShortsRepository;
import com.example.fimae.service.TimerService;
import com.example.fimae.utils.ReportItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
public class ReportItemAdapter extends RecyclerView.Adapter<ReportItemAdapter.ViewHolder>{
    ArrayList<ReportAdapterItem> reportAdapterItems;
    private ReportItemAdapter.IClickCardUserListener iClickCardUserListener;
    private Context context;
    public interface IClickCardUserListener {
        void onClickUser(Fimaers user);
    }

    public void setData(Context context, ArrayList<ReportAdapterItem> reportAdapterItems, ReportItemAdapter.IClickCardUserListener inIClickCardUserListener) {
        this.context = context;
        this.reportAdapterItems = reportAdapterItems;
        this.iClickCardUserListener = inIClickCardUserListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ReportItemBinding binding;
        public ViewHolder(ReportItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ReportItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReportItemBinding binding = ReportItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReportItemAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportItemAdapter.ViewHolder holder, int position) {
        ReportItemBinding binding = holder.binding;
        ReportAdapterItem reportAdapterItem = reportAdapterItems.get(position);

        if(reportAdapterItem.getReportItem() == ReportItem.POST_ITEM){
            PostRepository.getInstance().getPostById(reportAdapterItem.getReport().getUid()).addOnCompleteListener(new OnCompleteListener<Post>() {
                @Override
                public void onComplete(@NonNull Task<Post> task) {
                    if(task.isSuccessful()){
                        Post post = task.getResult();
                        binding.comment.setText(String.valueOf(post.getNumberOfComments()));
                        binding.itemUserTvName.setText(post.getContent());
                        if(post.getPostImages()!= null && !post.getPostImages().isEmpty()){
                            Glide.with(context).load(post.getPostImages().get(0)).into(binding.imageItem);
                        }
                        binding.type.setText("Bài đăng");
                        binding.numberReport.setText("+" + reportAdapterItem.getReportDetails().size());
                        Glide.with(context).load(reportAdapterItem.getFirstReporterAvatar()).into(binding.reportava);
                        TimerService.setDuration(binding.timeCreated, post.getTimeCreated());
                    }
                }
            });
        }
        else if(reportAdapterItem.getReportItem() == ReportItem.SHORT_ITEM) {
            ShortsRepository.getInstance().getShortById(reportAdapterItem.getReport().getUid()).addOnCompleteListener(new OnCompleteListener<ShortMedia>() {
                @Override
                public void onComplete(@NonNull Task<ShortMedia> task) {
                    if(task.isSuccessful()){
                        ShortMedia shortMedia = task.getResult();
                        binding.comment.setText(String.valueOf(shortMedia.getNumOfComments()));
                        binding.itemUserTvName.setText(shortMedia.getDescription());
                        Glide.with(context).load(shortMedia.getMediaUrl()).into(binding.imageItem);
                        binding.type.setText("Tin đăng");
                        String numOfReport = "+" + (reportAdapterItem.getReportDetails().size() - 1);
                        binding.numberReport.setText(numOfReport);
                        Glide.with(context).load(reportAdapterItem.getFirstReporterAvatar()).into(binding.reportava);
                        TimerService.setDuration(binding.timeCreated, shortMedia.getTimeCreated());

                    }
                }
            });

        }
        binding.frameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AminDetailReportActivity.class);
                intent.putExtra("reportId", reportAdapterItem.getReport().getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (reportAdapterItems != null) {
            return reportAdapterItems.size();
        }
        return 0;
    }
}
