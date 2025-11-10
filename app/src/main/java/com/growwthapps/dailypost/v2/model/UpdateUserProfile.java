package com.growwthapps.dailypost.v2.model;

import java.io.Serializable;

public class UpdateUserProfile implements Serializable {
    public String code;
    public Data data;

    public static class Data implements Serializable {
        public int user_id;
        public String login_type;
        public String name;
        public String email;
        public String mobile;
        public String logo;
        public String designation;
        public String address;
        public String socialmedia_type;
        public String socialmedia_value;
    }

}
