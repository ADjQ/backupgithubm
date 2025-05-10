package aiss.githubminer.service;

import aiss.githubminer.model.GitHubCommit;
import aiss.githubminer.model.GitHubIssue;
import aiss.githubminer.model.GitHubProject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class ProjectService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommitService commitService;

    @Autowired
    private IssueService issueService;

    @Value("${githubminer.baseuri}")
    private String baseURI;

    @Value("${githubminer.token}")
    private String token;

    private final List<GitHubProject> projects = new ArrayList<>();

    public GitHubProject getProject(String owner, String repo, int sinceCommits, int sinceIssues, int maxPages) throws JsonProcessingException {
        String url = baseURI + owner + "/" + repo;

        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al obtener el proyecto: " + response.getStatusCode());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode projectNode = objectMapper.readTree(response.getBody());

        if (projectNode == null) {
            throw new RuntimeException("Error: proyecto no encontrado.");
        }

        List<GitHubCommit> commits = new ArrayList<>();
        List<GitHubIssue> issues = new ArrayList<>();

        try {
            for (int page = 1; page <= maxPages; page++) {
                try {
                    List<GitHubCommit> pageCommits = commitService.getAllCommits(owner, repo, sinceCommits, page);
                    commits.addAll(pageCommits);
                    if (pageCommits.size() < sinceCommits) {
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener commits de la página " + page + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener commits: " + e.getMessage());
        }

        try {
            for (int page = 1; page <= maxPages; page++) {
                try {
                    List<GitHubIssue> pageIssues = issueService.getAllIssues(owner, repo, sinceIssues, page);
                    issues.addAll(pageIssues);
                    if (pageIssues.size() < sinceIssues) {
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener issues de la página " + page + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener issues: " + e.getMessage());
        }

        return new GitHubProject(
                projectNode.has("id") ? projectNode.get("id").asText() : "",
                projectNode.has("name") ? projectNode.get("name").asText() : "",
                projectNode.has("html_url") ? projectNode.get("html_url").asText() : "",
                commits,
                issues
        );
    }

    public GitHubProject createProject(GitHubProject project) {
        projects.add(project);
        return project;
    }
}