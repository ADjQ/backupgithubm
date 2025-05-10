package aiss.githubminer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({
        "id",
        "title",
        "description",
        "state",
        "created_at",
        "updated_at",
        "closed_at",
        "labels",
        "author",
        "assignee",
        "votes",
        "comments"
})

public class GitHubIssue {

    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("state")
    private String state;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("closed_at")
    private String closedAt;
    @JsonProperty("labels")
    private List<String> labels;
    @JsonProperty("author")
    private GitHubUser author;
    @JsonProperty("assignee")
    private GitHubUser assignee;
    @JsonProperty("votes")
    private Integer votes;
    @JsonProperty("comments")
    private List<GitHubComment> comments;

    @JsonProperty("id")
    public String getId() { return id; }

    @JsonProperty("id")
    public void setId(String id) { this.id = id; }

    @JsonProperty("title")
    public String getTitle() { return title; }

    @JsonProperty("title")
    public void setTitle(String title) { this.title = title; }

    @JsonProperty("description")
    public String getDescription() { return description; }

    @JsonProperty("description")
    public void setDescription(String description) { this.description = description; }

    @JsonProperty("state")
    public String getState() { return state; }

    @JsonProperty("state")
    public void setState(String state) { this.state = state; }

    @JsonProperty("created_at")
    public String getCreatedAt() { return createdAt; }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @JsonProperty("updated_at")
    public String getUpdatedAt() { return updatedAt; }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @JsonProperty("closed_at")
    public String getClosedAt() { return closedAt; }

    @JsonProperty("closed_at")
    public void setClosedAt(String closedAt) { this.closedAt = closedAt; }

    @JsonProperty("labels")
    public List<String> getLabels() { return labels; }

    @JsonProperty("labels")
    public void setLabels(List<String> labels) { this.labels = labels; }

    @JsonProperty("author")
    public GitHubUser getAuthor() { return author; }

    @JsonProperty("author")
    public void setAuthor(GitHubUser author) { this.author = author; }

    @JsonProperty("assignee")
    public GitHubUser getAssignee() { return assignee; }

    @JsonProperty("assignee")
    public void setAssignee(GitHubUser assignee) { this.assignee = assignee; }

    @JsonProperty("votes")
    public Integer getVotes() { return votes; }

    @JsonProperty("votes")
    public void setVotes(Integer votes) { this.votes = votes; }

    @JsonProperty("comments")
    public List<GitHubComment> getComments() { return comments; }

    @JsonProperty("comments")
    public void setComments(List<GitHubComment> comments) { this.comments = comments; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GitHubIssue.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id=").append((this.id == null) ? "<null>" : this.id).append(',');
        sb.append("title=").append((this.title == null) ? "<null>" : this.title).append(',');
        sb.append("description=").append((this.description == null) ? "<null>" : this.description).append(',');
        sb.append("state=").append((this.state == null) ? "<null>" : this.state).append(',');
        sb.append("createdAt=").append((this.createdAt == null) ? "<null>" : this.createdAt).append(',');
        sb.append("updatedAt=").append((this.updatedAt == null) ? "<null>" : this.updatedAt).append(',');
        sb.append("closedAt=").append((this.closedAt == null) ? "<null>" : this.closedAt).append(',');
        sb.append("labels=").append((this.labels == null) ? "<null>" : this.labels).append(',');
        sb.append("author=").append((this.author == null) ? "<null>" : this.author).append(',');
        sb.append("assignee=").append((this.assignee == null) ? "<null>" : this.assignee).append(',');
        sb.append("votes=").append((this.votes == null) ? "<null>" : this.votes).append(',');
        sb.append("comments=").append((this.comments == null) ? "<null>" : this.comments);

        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setCharAt(sb.length() - 1, ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
