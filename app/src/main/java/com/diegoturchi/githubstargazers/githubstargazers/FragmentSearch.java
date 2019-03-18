package com.diegoturchi.githubstargazers.githubstargazers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentSearch extends Fragment {

    private EditText txtOwner;
    private EditText txtRepoName;
    private String cachedRepoOwner;
    private String cachedRepoName;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtOwner = getView().findViewById(R.id.editTxtRepoOwner);
        txtRepoName = getView().findViewById(R.id.editTxtRepoName);

        //Potrei avere dei dati gia settati quando torno indietro da FragmentStargazersList
        //caso in cui il repository non è stato trovato e non voglio cancellare i dati inseriti dall'utente
        if(getArguments() != null && getArguments().containsKey("repoOwner") && getArguments().containsKey("repoName")) {
            cachedRepoOwner = getArguments().getString("repoOwner");
            cachedRepoName = getArguments().getString("repoName");

            txtOwner.setText(cachedRepoOwner);
            txtOwner.setError("Owner or name not found");
            txtRepoName.setText(cachedRepoName);
            txtRepoName.setError("Owner or name not found");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Button buttonScan = (Button) view.findViewById(R.id.btnSearch);
        buttonScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Controllo se è presente la connessione ad internet
                if(!isNetworkAvailable()) {
                    Toast.makeText(getContext(), "Make sure you are connected to the internet!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(txtOwner.getText().toString().isEmpty()) {
                    txtOwner.setError("This field cannot be empty!");
                    return;
                }

                if(txtRepoName.getText().toString().isEmpty()) {
                    txtRepoName.setError("This field cannot be empty!");
                    return;
                }

                //Prendo i dati inseriti e faccio partire la ricerca
                Fragment fragmentToOpen = new FragmentStargazersList();
                Bundle bundle = new Bundle();
                bundle.putString("repoOwner", txtOwner.getText().toString());
                bundle.putString("repoName", txtRepoName.getText().toString());
                fragmentToOpen.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentToOpen).commit();
            }
        });

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
