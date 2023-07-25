//package com.example.fimae.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.fimae.databinding.ReportUserBinding;
//import com.example.fimae.databinding.ReportUserBinding;
//import com.example.fimae.models.Fimaers;
//import com.example.fimae.models.Post;
//import com.example.fimae.models.ReportUserItem;
//import com.example.fimae.models.shorts.ShortMedia;
//import com.example.fimae.service.TimerService;
//import com.example.fimae.utils.ReportItem;
//
//import java.util.ArrayList;
//public class ReportUserAdapter extends RecyclerView.Adapter<ReportUserAdapter.ViewHolder>{
//    ArrayList<ReportUserItem> reportUserItems;
//    private ReportUserAdapter.IClickCardUserListener iClickCardUserListener;
//    private Context context;
//    public interface IClickCardUserListener {
//        void onClickUser(Fimaers user);
//    }
//
//    public void setData(Context context, ArrayList<ReportUserItem> reportUserItems, ReportUserAdapter.IClickCardUserListener inIClickCardUserListener) {
//        this.context = context;
//        this.reportUserItems = reportUserItems;
//        this.iClickCardUserListener = inIClickCardUserListener;
//        notifyDataSetChanged();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        ReportUserBinding binding;
//        public ViewHolder(ReportUserBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }
//
//    @NonNull
//    @Override
//    public ReportUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ReportUserBinding binding = ReportUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new ReportUserAdapter.ViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReportUserAdapter.ViewHolder holder, int position) {
//        ReportUserBinding binding = holder.binding;
//        ReportUserItem reportUserItem = reportUserItems.get(position);
//        Fimaers fimaers = reportUserItem.getFimaers();
//        TimerService.setDuration(binding.timeCreated, reportUserItem.getReport().getTimeCreated());
//        binding.itemUserTvName.setText(fimaers.getName());
//        Glide.with(context).load(fimaers.getAvatarUrl()).into(binding.avatar);
//        binding.numberOfReport.setText("+" + reportUserItem.getReportDetails().size());
//        Glide.with(context).load(reportUserItem.getFirstReporterAvatar()).into(binding.reportava);
//    }
//    @Override
//    public int getItemCount() {
//        if (reportUserItems != null) {
//            return reportUserItems.size();
//        }
//        return 0;
//    }
//}
