package com.example.marketapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    // Server URL
    private static final String BASE_URL = "https://sundaland.herokuapp.com/route";
    private Map<String, String> map;

    public LoginRequest(
            String userEmail,
            String userPassword,
            Response.Listener<String> listener
    ) {
        super(Method.POST, BASE_URL, listener, null);

        map = new HashMap<>();
        map.put("userEmail", userEmail);
        map.put("userPassword", userPassword);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
