package aiss.githubminer.service;

import aiss.githubminer.model.GitHubCommit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommitService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${githubminer.baseuri}")
    private String baseURI;

    @Value("${githubminer.token}")
    private String token;

    public List<GitHubCommit> getAllCommits(String owner, String repo, int sinceCommits, int maxPages) throws Exception {
        String url = baseURI + owner + "/" + repo + "/commits?per_page=" + sinceCommits + "&page=" + maxPages;

        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al obtener los commits: " + response.getStatusCode() + " - " + response.getBody());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        List<GitHubCommit> commits = new ArrayList<>();
        for (JsonNode node : rootNode) {
            GitHubCommit commit = new GitHubCommit();
            commit.setId(node.get("sha").asText());
            commit.setTitle(node.get("commit").get("message").asText().split("\n")[0]);
            commit.setMessage(node.get("commit").get("message").asText());
            commit.setAuthorName(node.get("commit").get("author").get("name").asText());
            commit.setAuthorEmail(node.get("commit").get("author").get("email").asText());
            commit.setAuthoredDate(node.get("commit").get("author").get("date").asText());
            commit.setWebUrl(node.get("html_url").asText());
            commits.add(commit);
        }
        return commits;
    }
}
