package com.example.fimae.models;

public class BottomSheetItem {
    String tag;
    int leadingIcon;
    int trailingIcon = Integer.MIN_VALUE;
    String title;
    String subtitle;

    //Write deprecated cmt add tag to constructor
    /**
     * @deprecated Thêm tag vào constructor để xác định item được chọn
     * @param leadingIcon
     * @param title
     */
    @Deprecated
    public BottomSheetItem(int leadingIcon, String title) {
        this.tag = tag;
        this.leadingIcon = leadingIcon;
        this.title = title;
    }
    public BottomSheetItem(String tag, int leadingIcon, String title) {
        this.tag = tag;
        this.leadingIcon = leadingIcon;
        this.title = title;
    }
    public BottomSheetItem(String tag, int leadingIcon, String title, String subtitle) {
        this.tag = tag;
        this.leadingIcon = leadingIcon;
        this.title = title;
        this.subtitle = subtitle;
    }
    public BottomSheetItem(String tag, int leadingIcon, String title,  int trailingIcon) {
        this.tag = tag;
        this.title = title;
        this.leadingIcon = leadingIcon;
        this.trailingIcon = trailingIcon;
    }
    public BottomSheetItem(String tag, int leadingIcon, String title, String subtitle, int trailingIcon) {
        this.tag = tag;
        this.leadingIcon = leadingIcon;
        this.title = title;
        this.subtitle = subtitle;
        this.trailingIcon = trailingIcon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getLeadingIcon() {
        return leadingIcon;
    }

    public void setLeadingIcon(int leadingIcon) {
        this.leadingIcon = leadingIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrailingIcon() {
        return trailingIcon;
    }

    public void setTrailingIcon(int trailingIcon) {
        this.trailingIcon = trailingIcon;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
