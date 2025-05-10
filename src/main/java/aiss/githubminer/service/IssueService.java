package aiss.githubminer.service;

import aiss.githubminer.model.GitHubComment;
import aiss.githubminer.model.GitHubIssue;
import aiss.githubminer.model.GitHubUser;
import aiss.githubminer.model.GitHubFullUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class IssueService {

    @com.fasterxml.jackson.annotation.JsonPropertyOrder({
        "id",
        "username",
        "name",
        "avatar_url",
        "web_url"
    })

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommentService commentService;

    @Value("${githubminer.baseuri}")
    private String baseURI;

    @Value("${githubminer.token}")
    private String token;


    public static class GitHubUserWithName extends GitHubUser {
        @JsonProperty("name")
        private String name;

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }
    }

    private GitHubFullUser getFullUserDetails(String login) {
        String url = "https://api.github.com/users/" + login;
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode userNode = objectMapper.readTree(response.getBody());

                GitHubFullUser fullUser = new GitHubFullUser();
                fullUser.setId(userNode.has("id") ? userNode.get("id").asText() : null);
                fullUser.setLogin(userNode.has("login") ? userNode.get("login").asText() : null);
                fullUser.setName(userNode.has("name") ? userNode.get("name").asText() : null);
                fullUser.setAvatarUrl(userNode.has("avatar_url") ? userNode.get("avatar_url").asText() : null);
                fullUser.setUrl(userNode.has("url") ? userNode.get("url").asText() : null);

                return fullUser;
            }
        } catch (Exception e) {
            GitHubFullUser fallbackUser = new GitHubFullUser();
            fallbackUser.setLogin(login);
            return fallbackUser;
        }

        GitHubFullUser fallbackUser = new GitHubFullUser();
        fallbackUser.setLogin(login);
        return fallbackUser;
    }

    public List<GitHubIssue> getAllIssues(String owner, String repo, int sinceIssues, int maxPages) throws Exception {

        String url = baseURI + owner + "/" + repo + "/issues?per_page=" + sinceIssues + "&page=" + maxPages ;
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al obtener los issues: " + response.getStatusCode() + " - " + response.getBody());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        List<GitHubIssue> issues = new ArrayList<>();
        for (JsonNode node : rootNode) {
            GitHubIssue issue = new GitHubIssue();
            issue.setId(node.get("id").asText());
            issue.setTitle(node.get("title").asText());
            issue.setDescription(node.has("body") && !node.get("body").isNull() ? node.get("body").asText() : ""); // Evita null en "body"
            issue.setState(node.get("state").asText());
            issue.setCreatedAt(node.get("created_at").asText());
            issue.setUpdatedAt(node.get("updated_at").asText());
            issue.setClosedAt(node.has("closed_at") && !node.get("closed_at").isNull() ? node.get("closed_at").asText() : null);

            List<String> labels = new ArrayList<>();
            JsonNode labelsNode = node.get("labels");

            if (labelsNode != null && labelsNode.isArray()) {
                for (JsonNode labelNode : labelsNode) {
                    if (labelNode.has("name") && !labelNode.get("name").isNull()) {
                        labels.add(labelNode.get("name").asText());
                    }
                }
            }
            issue.setLabels(labels);

            JsonNode reactionsNode = node.get("reactions");
            Integer vote = (reactionsNode != null && reactionsNode.has("+1") && !reactionsNode.get("+1").isNull())
                    ? reactionsNode.get("+1").asInt()
                    : 0;
            issue.setVotes(vote);

            JsonNode userNode = node.get("user");
            if (userNode != null) {
                String login = userNode.has("login") ? userNode.get("login").asText() : null;
                if (login != null) {
                    GitHubFullUser author = getFullUserDetails(login);
                    author.setId(userNode.has("id") ? userNode.get("id").asText() : null);
                    author.setAvatarUrl(userNode.has("avatar_url") ? userNode.get("avatar_url").asText() : null);
                    author.setUrl(userNode.has("url") ? userNode.get("url").asText() : null);
                    GitHubUserWithName authorUser = new GitHubUserWithName();
                    authorUser.setId(author.getId());
                    authorUser.setLogin(author.getLogin());
                    authorUser.setName(author.getName());
                    authorUser.setAvatarUrl(author.getAvatarUrl());
                    authorUser.setUrl(author.getUrl());
                    issue.setAuthor(authorUser);
                }
            }

            JsonNode assigneeNode = node.get("assignee");
            if (assigneeNode != null && !assigneeNode.isNull()) {
                String login = assigneeNode.has("login") ? assigneeNode.get("login").asText() : null;
                if (login != null) {
                    GitHubFullUser assignee = getFullUserDetails(login);
                    assignee.setId(assigneeNode.has("id") ? assigneeNode.get("id").asText() : null);
                    assignee.setAvatarUrl(assigneeNode.has("avatar_url") ? assigneeNode.get("avatar_url").asText() : null);
                    assignee.setUrl(assigneeNode.has("url") ? assigneeNode.get("url").asText() : null);
                    GitHubUserWithName assigneeUser = new GitHubUserWithName();
                    assigneeUser.setId(assignee.getId());
                    assigneeUser.setLogin(assignee.getLogin());
                    assigneeUser.setName(assignee.getName());
                    assigneeUser.setAvatarUrl(assignee.getAvatarUrl());
                    assigneeUser.setUrl(assignee.getUrl());
                    issue.setAssignee(assigneeUser);
                }
            }

            if (node.has("number")) {
                String issueNumber = node.get("number").asText();
                try {
                    List<GitHubComment> comments = commentService.getIssueComments(owner, repo, issueNumber);
                    issue.setComments(comments);
                } catch (Exception e) {
                    issue.setComments(new ArrayList<>());
                }
            } else {
                issue.setComments(new ArrayList<>());
            }
            issues.add(issue);
        }
        return issues;
    }
}
