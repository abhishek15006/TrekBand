package app.aadil.travelindia;

import com.google.firebase.auth.FirebaseAuth;

public class UserModal {

    private String uid;
    private String imageUrl;
    private String name;
    private String country;
    private String phone;

    public UserModal(String url, String name, String country, String phone) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.imageUrl = url;
        this.name = name;
        this.country = country;
        this.phone = phone;
    }

    public UserModal(String name, String country, String phone) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.name = name;
        this.country = country;
        this.phone = phone;
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
}
