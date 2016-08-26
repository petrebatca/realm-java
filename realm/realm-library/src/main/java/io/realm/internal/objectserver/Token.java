package io.realm.internal.objectserver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * This class represents a value from the Realm Authentication Server.
 */
public class Token {

    private final String value;
    private final long expires;
    private final Permission[] permissions;

    public static Token from(JSONObject token) throws JSONException {
        String value = token.getString("token");
        long expires = token.getLong("expires");
        Permission[] permissions;
        JSONArray access = token.getJSONArray("access");
        if (access != null) {
            permissions = new Permission[access.length()];
            for (int i = 0; i < access.length(); i++) {
                try {
                    permissions[i] = Permission.valueOf(access.getString(i));
                } catch (IllegalArgumentException e) {
                    permissions[i] = Permission.UNKNOWN;
                }
            }
        } else {
            permissions = new Permission[0];
        }

        return new Token(value, expires, permissions);
    }

    public Token(String value, long expires, Permission... permissions) {
        this.value = value;
        this.expires = expires;
        this.permissions = permissions;
    }

    public String value() {
        return value;
    }

    public long expires() {
        return expires;
    }

    public Permission[] permissions() {
        return permissions;
    }

    public String toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", value);
            obj.put("expires", expires);
            JSONArray perms = new JSONArray();
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i].toString().toLowerCase(Locale.US));
            }
            obj.put("access", perms);
            return obj.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Could not convert Token to JSON.", e);
        }
    }

    public enum Permission {
        UNKNOWN,
        UPLOAD,
        DOWNLOAD,
        REFRESH,
        MANAGE
    }
}
