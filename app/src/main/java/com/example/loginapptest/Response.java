package com.example.loginapptest;

public class Response {
    private String access_token;
    private String expires_in;
    private String refresh_expires_in;
    private String refresh_token;
    private String token_type;
    private String not_before_policy;
    private String session_state;
    private String scope;

    public Response(String access_token, String expires_in, String refresh_expires_in, String refresh_token, String token_type, String not_before_policy, String session_state, String scope) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.refresh_expires_in = refresh_expires_in;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
        this.not_before_policy = not_before_policy;
        this.session_state = session_state;
        this.scope = scope;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getRefresh_expires_in() {
        return refresh_expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getNot_before_policy() {
        return not_before_policy;
    }

    public String getSession_state() {
        return session_state;
    }

    public String getScope() {
        return scope;
    }
}
