package com.example.fimae.utils;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;

import com.example.fimae.fragments.CommentEditFragment;
import com.example.fimae.repository.CommentRepository;


public class AsyncTask extends android.os.AsyncTask<Void, String, Void> {

    ProgressDialog progressDialog;
    Context context;
    CommentEditFragment.CommentCallBack callBack;
    Boolean isEdit;
    public AsyncTask(Context context, CommentEditFragment.CommentCallBack callBack) {
        this.context = context;
        this.isEdit = isEdit;
        this.callBack = callBack;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        callBack.callback();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Hiển thị Dialog khi bắt đầu xử lý.
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Hehe");
        progressDialog.setMessage("Dang xu ly...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progressDialog.dismiss();
    }
}
