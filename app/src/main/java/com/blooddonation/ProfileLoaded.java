package com.blooddonation;

import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public interface ProfileLoaded {
     void OnProfileLoaded(AccountDetails accountDetails, String user, CircleImageView profile_pic, ProgressBar progress_horizontal, RecyclerView.ViewHolder holder);
}
