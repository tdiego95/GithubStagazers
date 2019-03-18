package com.diegoturchi.githubstargazers.githubstargazers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.diegoturchi.githubstargazers.githubstargazers.StargazersListController.Result.error;

public class FragmentStargazersList extends Fragment {

    private ProgressDialog dialog;
    private StargazersListController requestController;
    private String repoOwner;
    private String repoName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stargazers_list, container, false);

        //Button back
        Button buttonScan = (Button) view.findViewById(R.id.btnBack);
        buttonScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSearch()).commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Prendo i dati in input e inizio la ricerca
        if(getArguments() != null && getArguments().containsKey("repoOwner") && getArguments().containsKey("repoName")) {
            repoOwner = getArguments().getString("repoOwner");
            repoName = getArguments().getString("repoName");

            dialog = ProgressDialog.show(getContext(), "Looking for stargazers", "Loading.. Wait..", true);

            requestController = new StargazersListController();
            requestController.InitializeStargazers(repoOwner, repoName, new StargazersListController.IRequestCallback() {

                @Override
                public void processFinish(List<User> lStargazersFound, StargazersListController.Result response) {
                    runProcessFinish(lStargazersFound, response);
                }
            });
        }
    }

    private UsersListAdapter listViewAdapter;
    private List<User> usersArrayList;
    private Boolean loading = false;
    private Integer prevTotal = 0;
    private void runProcessFinish(List<User> lStargazersFound, StargazersListController.Result response)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                //Se non ho risultati vado a controllare se c'è stato un errore o non è stato trovato il repository e torno indietro
                if (lStargazersFound == null || lStargazersFound.isEmpty()) {

                    FragmentSearch fragmentSearch = new FragmentSearch();

                    switch (response) {
                        case error:
                            Toast.makeText(getContext(), getString(R.string.error_connecting_server), Toast.LENGTH_LONG).show();
                            break;
                        case notFound:
                            Bundle bundle = new Bundle();
                            bundle.putString("repoOwner", repoOwner);
                            bundle.putString("repoName", repoName);
                            fragmentSearch.setArguments(bundle);
                            break;
                    }

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentSearch).commit();
                    return;
                }

                usersArrayList = new ArrayList<>(lStargazersFound);

                //Riempio la listview con i risultati della ricerca
                ListView listView = getView().findViewById(R.id.list_view);
                listViewAdapter = new UsersListAdapter(getContext(), R.layout.user_layout, usersArrayList);
                listView.setAdapter(listViewAdapter);

                prevTotal = usersArrayList.size();

                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            //Questo evento parte molto frequentemente quindi utilizzo le variabili loading e prevtotal per assicurarmi di
                            //fare una nuova richiesta solo una volta e solo in un preciso momento
                            if (loading) {
                                if (totalItemCount > prevTotal) {
                                    loading = false;
                                    prevTotal = totalItemCount;
                                }
                            }
                            //Quando mi trovo a metà dello scorrimento della lista inizio ad aggiornare i nuovi elementi
                            if (!loading && (firstVisibleItem > totalItemCount/2)) {
                                loading = true;

                                requestController.PageNext(new StargazersListController.IRequestCallback() {
                                    @Override
                                    public void processFinish(List<User> lStargazersFound, StargazersListController.Result response) {
                                        //Aggiorno il dataset della listview per evitare che mi torni al primo elemento quando aggiungo i nuovi
                                        UpdateDataset(lStargazersFound);
                                    }
                                });
                            }

                    }

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }
                });
            }
        });
    }

    private void UpdateDataset(List<User> lStargazersFound) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                usersArrayList.clear();
                usersArrayList.addAll(lStargazersFound);
                listViewAdapter.notifyDataSetChanged();
            }
        });

    }
}
