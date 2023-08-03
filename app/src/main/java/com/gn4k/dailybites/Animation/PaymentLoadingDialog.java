package com.gn4k.dailybites.Animation;

import android.app.Activity;
import android.app.Dialog;

import com.gn4k.dailybites.R;

public class PaymentLoadingDialog {
    public PaymentLoadingDialog(Activity activity) {
        this.activity = activity;
    }

    private Activity activity;
    private Dialog dialog;

    public void startLoading(){

        dialog = new Dialog(activity, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(R.layout.payment_loading_dialog);
        dialog.setCancelable(false);
        dialog.show();

    }

    public void stopLoading(){
        dialog.dismiss();
    }




}
