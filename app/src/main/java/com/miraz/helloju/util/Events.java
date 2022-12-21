package com.miraz.helloju.util;

public class Events {

    // Event used to send message from login notify.
    public static class Login {
        private String login;

        public Login(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    // Event used to send message from login notify.
    public static class EventUpdateDetail {
        private String type;

        public EventUpdateDetail(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    // Event used to favourite data update
    public static class Favourite {

        private String id, type;
        private boolean is_fav;
        private int position;

        public Favourite(String id, String type, boolean is_fav, int position) {
            this.id = id;
            this.type = type;
            this.is_fav = is_fav;
            this.position = position;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public boolean isIs_fav() {
            return is_fav;
        }

        public int getPosition() {
            return position;
        }
    }

    // Event used delete event
    public static class EventDelete {
        private String string;
        private int position;

        public EventDelete(String string, int position) {
            this.string = string;
            this.position = position;
        }

        public String getString() {
            return string;
        }

        public int getPosition() {
            return position;
        }
    }

    // Event used update event data
    public static class EventUpdate {
        private String event_id, event_title, event_date, event_banner_thumb, event_address;
        private int position;

        public EventUpdate(String event_id, String event_title, String event_date, String event_banner_thumb, String event_address, int position) {
            this.event_id = event_id;
            this.event_title = event_title;
            this.event_date = event_date;
            this.event_banner_thumb = event_banner_thumb;
            this.event_address = event_address;
            this.position = position;
        }

        public String getEvent_id() {
            return event_id;
        }

        public String getEvent_title() {
            return event_title;
        }

        public String getEvent_date() {
            return event_date;
        }

        public String getEvent_banner_thumb() {
            return event_banner_thumb;
        }

        public String getEvent_address() {
            return event_address;
        }

        public int getPosition() {
            return position;
        }
    }

    //Event used to update profile
    public static class ProfileUpdate {

        private String string;

        public ProfileUpdate(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

    //Event used to update remove and update image
    public static class ProImage {

        private String string, imagePath;
        private boolean isProfile, isRemove;

        public ProImage(String string, String imagePath, boolean isProfile, boolean isRemove) {
            this.string = string;
            this.imagePath = imagePath;
            this.isProfile = isProfile;
            this.isRemove = isRemove;
        }

        public String getString() {
            return string;
        }

        public String getImagePath() {
            return imagePath;
        }

        public boolean isProfile() {
            return isProfile;
        }

        public boolean isRemove() {
            return isRemove;
        }
    }

}
