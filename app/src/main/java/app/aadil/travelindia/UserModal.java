package app.aadil.travelindia;

import com.google.firebase.auth.FirebaseAuth;

public class UserModal {

    private String uid;
    private String imageUrl;
    private String name;
    private String country;
    private String phone;
    private boolean registered;

    public UserModal() {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public UserModal(String url, String name, String country, String phone, boolean registered) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.imageUrl = url;
        this.name = name;
        this.country = country;
        this.phone = phone;
        this.registered = registered;
    }

    public UserModal(String name, String country, String phone, boolean registered) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.name = name;
        this.country = country;
        this.phone = phone;
        this.registered = registered;
    }

    public String getUid() {
        return uid;
    }

    public String getCountry() {
        return country;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean getRegistered() {
        return this.registered;
    }
}
