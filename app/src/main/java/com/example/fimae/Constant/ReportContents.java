package com.example.fimae.Constant;

import com.example.fimae.adapters.Report.ReportAdapterItem;

import java.util.ArrayList;

public class ReportContents {
    //Create a ArrayList<ReportAdapterItem>  of ReportAdapterItem for Post
    static public ArrayList<ReportAdapterItem> getPostReportAdapterItems() {
        ArrayList<ReportAdapterItem> reportAdapterItems = new ArrayList<>();
        reportAdapterItems.add(new ReportAdapterItem("spam", "Spam"));
        reportAdapterItems.add(new ReportAdapterItem("harassment", "Quấy rối"));
        reportAdapterItems.add(new ReportAdapterItem("violence", "Bạo lực"));
        reportAdapterItems.add(new ReportAdapterItem("illegal_activity", "Hoạt động bất hợp pháp"));
        reportAdapterItems.add(new ReportAdapterItem("fraud", "Có dấu hiệu lừa đảo"));
        reportAdapterItems.add(new ReportAdapterItem("minor", "Vị thành niên (<13)"));
        reportAdapterItems.add(new ReportAdapterItem("bullying", "Bắt nạt"));
        reportAdapterItems.add(new ReportAdapterItem("harm_to_self", "Tự làm hại bản thân"));
        reportAdapterItems.add(new ReportAdapterItem("hate_language", "Ngôn ngữ gây thù ghét"));
        reportAdapterItems.add(new ReportAdapterItem("other", "Khác"));
        return reportAdapterItems;
    }

}
