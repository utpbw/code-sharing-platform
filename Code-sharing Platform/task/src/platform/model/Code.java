package platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Code {

    @Id
    private String id;

    @Column(columnDefinition = "TEXT")
    private String code;

    private String date;

    private long timeLimit;   // original seconds limit; 0 = no limit

    private int viewsLimit;   // remaining allowed views; 0 = no limit

    private long createdAt;   // epoch ms, for ordering and time checks

    public Code() {
    }

    public Code(String id, String code, String date, long timeLimit, int viewsLimit, long createdAt) {
        this.id = id;
        this.code = code;
        this.date = date;
        this.timeLimit = timeLimit;
        this.viewsLimit = viewsLimit;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }

    public String getCode() { return code; }

    public String getDate() { return date; }

    public long getTimeLimit() { return timeLimit; }

    public int getViewsLimit() { return viewsLimit; }

    public long getCreatedAt() { return createdAt; }

    public void setViewsLimit(int viewsLimit) { this.viewsLimit = viewsLimit; }
}
