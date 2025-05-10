package aiss.githubminer.controller;

import aiss.githubminer.model.GitHubProject;
import aiss.githubminer.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/github")
public class ProjectController {

    @Autowired
    private RestTemplate restTemplate;

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    @Value("${githubminer.sincecommits}")
    private int defaultSinceCommits;

    @Value("${githubminer.sinceissues}")
    private int defaultSinceIssues;

    @Value("${githubminer.maxpages}")
    private int defaultmaxPages;

    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<?> getProject(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) Integer sinceCommits,
            @RequestParam(required = false) Integer sinceIssues,
            @RequestParam(required = false) Integer maxPages) {
        try {
            GitHubProject project = projectService.getProject(
                    owner, repo,
                    sinceCommits != null ? sinceCommits : defaultSinceCommits,
                    sinceIssues != null ? sinceIssues : defaultSinceIssues,
                    maxPages != null ? maxPages : defaultmaxPages
            );

            if (project == null) {
                return ResponseEntity.status(404).body("Proyecto no encontrado.");
            }
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }

    @PostMapping("/{owner}/{repo}")
    public ResponseEntity<String> sendProject(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) Integer sinceCommits,
            @RequestParam(required = false) Integer sinceIssues,
            @RequestParam(required = false) Integer maxPages
    ) throws JsonProcessingException {

        GitHubProject project = projectService.getProject(
                owner, repo,
                sinceCommits != null ? sinceCommits : defaultSinceCommits,
                sinceIssues != null ? sinceIssues : defaultSinceIssues,
                maxPages != null ? maxPages : defaultmaxPages
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GitHubProject> requestEntity = new HttpEntity<>(project, headers);

        try {
            String gitMinerUrl = "http://localhost:8080/gitminer/projects";
            ResponseEntity<String> response = restTemplate.postForEntity(gitMinerUrl, requestEntity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending data to the main API: " + e.getMessage());
        }
    }
}