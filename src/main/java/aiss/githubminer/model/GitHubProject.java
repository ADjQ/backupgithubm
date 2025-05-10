package aiss.githubminer.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "web_url",
        "commits",
        "issues"
})

public class GitHubProject {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("commits")
    private List<GitHubCommit> commits;

    @JsonProperty("issues")
    private List<GitHubIssue> issues;

    public GitHubProject() {}

    public GitHubProject(String id, String name, String webUrl, List<GitHubCommit> commits, List<GitHubIssue> issues) {
        this.id = (id != null) ? id : "";
        this.name = (name != null) ? name : "";
        this.webUrl = (webUrl != null) ? webUrl : "";
        this.commits = commits;
        this.issues = issues;
    }

    @JsonProperty("id")
    public String getId() { return id; }

    @JsonProperty("id")
    public void setId(String id) { this.id = (id != null) ? id : ""; }

    @JsonProperty("name")
    public String getName() { return name; }

    @JsonProperty("name")
    public void setName(String name) { this.name = (name != null) ? name : ""; }

    @JsonProperty("web_url")
    public String getWebUrl() { return webUrl; }

    @JsonProperty("web_url")
    public void setWebUrl(String webUrl) { this.webUrl = (webUrl != null) ? webUrl : ""; }

    @JsonProperty("commits")
    public List<GitHubCommit> getCommits() { return commits; }

    @JsonProperty("commits")
    public void setCommits(List<GitHubCommit> commits) { this.commits = commits; }

    @JsonProperty("issues")
    public List<GitHubIssue> getIssues() { return issues; }

    @JsonProperty("issues")
    public void setIssues(List<GitHubIssue> issues) { this.issues = issues; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GitHubProject.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id=").append((this.id.isEmpty()) ? "<null>" : this.id).append(',');
        sb.append("name=").append((this.name.isEmpty()) ? "<null>" : this.name).append(',');
        sb.append("webUrl=").append((this.webUrl.isEmpty()) ? "<null>" : this.webUrl).append(',');
        sb.append("commits=").append((this.commits == null) ? "<null>" : this.commits).append(',');
        sb.append("issues=").append((this.issues == null) ? "<null>" : this.issues).append(']');

        return sb.toString();
    }
}