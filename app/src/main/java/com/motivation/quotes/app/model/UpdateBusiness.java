package com.motivation.quotes.app.model;

import java.io.Serializable;

public class UpdateBusiness implements Serializable {
    public String code;
    public Data data;

    public static class Data implements Serializable {
        public int app_user_id;
        public String name;
        public String email;
        public String mobile;
        public String logo;
        public String about;
        public String address;
        public String socialmedia_type;
        public String socialmedia_value;
    }

}
