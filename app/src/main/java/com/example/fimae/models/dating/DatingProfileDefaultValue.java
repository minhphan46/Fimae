package com.example.fimae.models.dating;

import com.example.fimae.adapters.Report.ReportAdapterItem;

import java.util.ArrayList;

public class DatingProfileDefaultValue {
    private DatingProfileDefaultValue() {
    }
    public static final int minAge = 18;
    public static final int maxAge = 60;
    public static final int minHeight = 120;
    public static final int maxHeight = 200;
    public static final int distance = 1000;
    public static final GenderOptions gender = GenderOptions.UNIMPORTANT;
    public static final RelationshipType relationshipType = RelationshipType.UNIMPORTANT;
    public static final EducationalLevel educationalLevel = EducationalLevel.UNIMPORTANT;


    public static final ArrayList<ReportAdapterItem> distanceOptions = new ArrayList<ReportAdapterItem>() {
        {
            for (int i = 100; i <= DatingProfileDefaultValue.distance; i += 100) {
                add(new ReportAdapterItem(String.valueOf(i), i + " km"));
            }
        }
    };
    public static final ArrayList<ReportAdapterItem> genderOptions = new ArrayList<ReportAdapterItem>() {
        {
            add(new ReportAdapterItem("1", "Không quan trọng"));
            add(new ReportAdapterItem("2", "Nam"));
            add(new ReportAdapterItem("3", "Nữ"));

        }
    };
    public static final ArrayList<ReportAdapterItem> relationshipOptions = new ArrayList<ReportAdapterItem>() {
        {
            add(new ReportAdapterItem("1", "Không quan trọng"));
            add(new ReportAdapterItem("2", "Người trò chuyện"));
            add(new ReportAdapterItem("3", "Quan hệ bạn bè"));
            add(new ReportAdapterItem("4", "Kiểu hẹn hò không ràng buộc"));
            add(new ReportAdapterItem("5", "Mối quan hệ lâu dài"));
        }
    };
    public static final ArrayList<ReportAdapterItem> educationLevelOptions = new ArrayList<ReportAdapterItem>() {
        {
            add(new ReportAdapterItem("1", "Không quan trọng"));
            add(new ReportAdapterItem("2", "Bằng trung học"));
            add(new ReportAdapterItem("3", "Bằng cao đẳng/đại học"));
            add(new ReportAdapterItem("4", "Bằng cao học"));
        }
    };
}
