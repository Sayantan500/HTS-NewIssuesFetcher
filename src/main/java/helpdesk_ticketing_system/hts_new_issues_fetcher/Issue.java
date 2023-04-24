package helpdesk_ticketing_system.hts_new_issues_fetcher;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@SuppressWarnings("ALL")
public class Issue {
    private String _id;
    @JsonProperty("submitted_by") private String submitted_by;
    @JsonProperty("subject") private String subject;
    @JsonProperty("description") private String description;
    @JsonProperty("posted_on") private Long postedOn;

    public Issue() {
    }

    @JsonGetter("issue_id") public String get_id() {
        return _id;
    }

    @JsonSetter("_id") public void set_id(String _id) {
        this._id = _id;
    }

    public String getSubmitted_by() {
        return submitted_by;
    }

    public void setSubmitted_by(String submitted_by) {
        this.submitted_by = submitted_by;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Long postedOn) {
        this.postedOn = postedOn;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "_id='" + _id + '\'' +
                ", submitted_by='" + submitted_by + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", postedOn=" + postedOn +
                '}';
    }
}
