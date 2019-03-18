package com.diegoturchi.githubstargazers.githubstargazers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UsersListAdapter extends ArrayAdapter<User> {

    //Questo adapter mi da la possibilità di inserire il mio oggetto user all'interno della ListView

    private Context context;
    private int resource;

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        //Imposto il nome dello stargazer nella listview
        String currentUserName = "user name not found";
        if(!getItem(position).getName().isEmpty()) {
            currentUserName = getItem(position).getName();
        }

        ((TextView) convertView.findViewById(R.id.txtUserName)).setText(currentUserName);

        //Imposto l'avatar dello stargazer nella listview
        String currentUserAvatarUrl = getItem(position).getAvatarUrl();
        if(!currentUserAvatarUrl.isEmpty()) {
            ImageView avatar = convertView.findViewById(R.id.imgAvatar);
            Picasso.get()
                    .load(currentUserAvatarUrl)
                    .resize(50, 50)
                    .centerCrop()
                    .into(avatar);
        }

        return convertView;
    }
}
