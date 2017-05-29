package com.eadlsync.util.net;

import com.eadlsync.util.YStatementConstants;

/**
 * Created by Tobias on 29.05.2017.
 */
public class SeRepoUrlObject {

    public final String SEREPO_BASE_URL;
    public final String SEREPO_PROJECT;
    public final String SEREPO_COMMIT_ID;
    public final String SEREPO_URL_COMMITS;
    public final String SEREPO_SEITEMS;

    public SeRepoUrlObject(String baseUrl, String repoName, String commitId) {
        this.SEREPO_BASE_URL = baseUrl;
        this.SEREPO_PROJECT = repoName;
        this.SEREPO_COMMIT_ID = commitId;
        this.SEREPO_URL_COMMITS = String.format(YStatementConstants.SEREPO_URL_COMMITS, baseUrl, repoName);
        this.SEREPO_SEITEMS = String.format(YStatementConstants.SEREPO_SEITEMS, baseUrl, repoName, commitId);
    }

    public String generateMetadataUrl(String id) {
        return String.format(YStatementConstants.SEREPO_SEITEM_METADATA, SEREPO_BASE_URL, SEREPO_PROJECT, SEREPO_COMMIT_ID, id);
    }

    public String generateRelationsUrl(String id) {
        return String.format(YStatementConstants.SEREPO_SEITEM_RELATIONS, SEREPO_BASE_URL, SEREPO_PROJECT, SEREPO_COMMIT_ID, id);
    }
}