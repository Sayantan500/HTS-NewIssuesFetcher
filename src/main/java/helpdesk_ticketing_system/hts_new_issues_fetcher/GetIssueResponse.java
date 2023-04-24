package helpdesk_ticketing_system.hts_new_issues_fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetIssueResponse
{
    @JsonProperty("count") private Integer count;
    @JsonProperty("records") private List<Issue> records;
    @JsonProperty("posted_on_of_first_record") private Long postedOnValOfFirstDataItem;
    @JsonProperty("posted_on_of_last_record") private Long postedOnValOfLastDataItem;

    public GetIssueResponse() {
    }

    public GetIssueResponse(
            Integer count, List<Issue> records, Long postedOnValOfFirstDataItem, Long postedOnValOfLastDataItem
    ) {
        this.count = count;
        this.records = records;
        this.postedOnValOfFirstDataItem = postedOnValOfFirstDataItem;
        this.postedOnValOfLastDataItem = postedOnValOfLastDataItem;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Issue> getRecords() {
        return records;
    }

    public void setRecords(List<Issue> records) {
        this.records = records;
    }

    public Long getPostedOnValOfFirstDataItem() {
        return postedOnValOfFirstDataItem;
    }

    public void setPostedOnValOfFirstDataItem(Long postedOnValOfFirstDataItem) {
        this.postedOnValOfFirstDataItem = postedOnValOfFirstDataItem;
    }

    public Long getPostedOnValOfLastDataItem() {
        return postedOnValOfLastDataItem;
    }

    public void setPostedOnValOfLastDataItem(Long postedOnValOfLastDataItem) {
        this.postedOnValOfLastDataItem = postedOnValOfLastDataItem;
    }
}
