package com.diegoturchi.githubstargazers.githubstargazers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StargazersListController {

    public interface IRequestCallback {

        void processFinish(List<User> lStargazersFound, Result response);
    }

    public enum Result {
        success,
        error,
        notFound
    }

    private List<User> lStargazersFound;
    private String repoOwner, repoName;
    private Integer currPage, totPage;
    private Boolean isLoading = false;
    private IRequestCallback delegate;
    private OkHttpClient client;
    private Request request;

    private void LookForStargazers()
    {
        client = new OkHttpClient();

        request = new Request.Builder()
                .url("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/stargazers?page=" + currPage)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isLoading = false;
                delegate.processFinish(lStargazersFound, Result.error);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    isLoading = false;
                    delegate.processFinish(lStargazersFound, Result.notFound);
                } else {

                    //Solamente la prima volta, vado a prendere dall'header il numero di pagine di stargazers trovati
                    if(totPage == 0) {
                        totPage = Integer.parseInt(response.header("Link").split("page=")[2].split(">")[0]);
                    }

                    //Prendo i risultati dal json, popolo la lista di stargazers trovati e la mando indietro tramite la callback
                    String jsonData = response.body().string();

                    JsonParser parser = new JsonParser();
                    JsonElement jsonTree = parser.parse(jsonData);

                    if(jsonTree.isJsonArray()) {
                        JsonArray jsonArray = jsonTree.getAsJsonArray();
                        for(JsonElement je : jsonArray) {
                            JsonObject jsonObject = je.getAsJsonObject();

                            String stargazerName = jsonObject.get("login").getAsString();
                            String stargazerAvatarUrl = jsonObject.get("avatar_url").getAsString();

                            lStargazersFound.add(new User(stargazerName, stargazerAvatarUrl));
                        }
                    }

                    isLoading = false;
                    delegate.processFinish(lStargazersFound, Result.success);
                }
            }
        });
    }

    public void PageNext(IRequestCallback callback) {

        if(!isLoading && currPage < totPage) {
            isLoading = true;
            delegate = callback;
            currPage++;
            LookForStargazers();
        }
    }

    public void InitializeStargazers(String repoOwner, String repoName, IRequestCallback callback) {
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        lStargazersFound = new ArrayList<>();
        delegate = callback;
        currPage = 1;
        totPage = 0;

        isLoading = true;
        LookForStargazers();
    }
}
