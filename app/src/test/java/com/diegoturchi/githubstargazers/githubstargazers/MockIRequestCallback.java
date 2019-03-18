package com.diegoturchi.githubstargazers.githubstargazers;

import java.util.List;

public class MockIRequestCallback implements StargazersListController.IRequestCallback {

    List<User> lStargazersFound;
    StargazersListController.Result response;

    @Override
    public void processFinish(List<User> lStargazersFound, StargazersListController.Result response) {

        this.lStargazersFound = lStargazersFound;
        this.response = response;

        synchronized (this) {
            notifyAll();
        }
    }

    public List<User> getStargazersList() {
        return lStargazersFound;
    }

    public StargazersListController.Result getResponse() {
        return response;
    }
}
