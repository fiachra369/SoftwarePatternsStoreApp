package electric.ecomm.store.model;


import java.io.Serializable;

public class Review implements Serializable {
    private String userName;
    private String comment;
    private float stars;

    public Review(String userName, String comment, float stars) {
        this.userName = userName;
        this.comment = comment;
        this.stars = stars;
    }

    public Review() {
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }
}
