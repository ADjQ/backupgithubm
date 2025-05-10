package aiss.githubminer.service;

import aiss.githubminer.model.GitHubComment;
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
public class CommentService {

    @com.fasterxml.jackson.annotation.JsonPropertyOrder({
        "id",
        "username",
        "name",
        "avatar_url",
        "web_url"
    })

    @Autowired
    RestTemplate restTemplate;

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

    public List<GitHubComment> getComment(String owner, String repo) {
        String url = baseURI + owner + "/" + repo + "/comments";

        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                List<GitHubComment> comments = new ArrayList<>();

                for (JsonNode node : rootNode) {
                    GitHubComment comment = new GitHubComment();
                    comment.setId(node.has("id") ? node.get("id").asText() : null);
                    comment.setBody(node.has("body") ? node.get("body").asText() : null);
                    comment.setCreatedAt(node.has("created_at") ? node.get("created_at").asText() : null);
                    comment.setUpdatedAt(node.has("updated_at") ? node.get("updated_at").asText() : null);

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
                            comment.setUser(authorUser);
                        }
                    }
                    comments.add(comment);
                }
                return comments;
            }
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los comentarios: " + e.getMessage());
        }
    }

    public GitHubComment createComment(GitHubComment comment, String owner, String repo) {
        GitHubComment createdComment = null;
        String url = baseURI + owner + "/" + repo + "/comments";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<GitHubComment> request = new HttpEntity<>(comment, headers);

            ResponseEntity<GitHubComment> response = restTemplate.exchange(url, HttpMethod.POST, request, GitHubComment.class);
            createdComment = response.getBody();
        }
        catch (Exception e) {
            throw new RuntimeException("Error al crear los mensajes: " + e.getMessage());
        }
        return createdComment;
    }

    public List<GitHubComment> getIssueComments(String owner, String repo, String issueNumber) {
        String url = baseURI + owner + "/" + repo + "/issues/" + issueNumber + "/comments";

        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                List<GitHubComment> comments = new ArrayList<>();

                for (JsonNode node : rootNode) {
                    GitHubComment comment = new GitHubComment();
                    comment.setId(node.has("id") ? node.get("id").asText() : null);
                    comment.setBody(node.has("body") ? node.get("body").asText() : null);
                    comment.setCreatedAt(node.has("created_at") ? node.get("created_at").asText() : null);
                    comment.setUpdatedAt(node.has("updated_at") ? node.get("updated_at").asText() : null);

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
                            comment.setUser(authorUser);
                        }
                    }
                    comments.add(comment);
                }
                return comments;
            }
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los comentarios del issue: " + e.getMessage());
        }
    }
}
