package aiss.githubminer.controller.unitarios;

import aiss.githubminer.model.GitHubCommit;
import aiss.githubminer.service.CommitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/github")
public class CommitController {

    private final CommitService commitService;

    @Value("${githubminer.sincecommits}")
    private int defaultSinceCommits;

    @Value("${githubminer.maxpages}")
    private int defaultMaxPages;

    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping("/{owner}/{repo}/commits")
    public ResponseEntity<?> getAllCommits(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) Integer sinceCommits,
            @RequestParam(required = false) Integer maxPages) {

        int since = (sinceCommits != null) ? sinceCommits : defaultSinceCommits;
        int page = (maxPages != null) ? maxPages : defaultMaxPages;
        try {
            List<GitHubCommit> commits = commitService.getAllCommits(owner, repo, since, page);
            return ResponseEntity.ok(commits);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener los commits: " + e.getMessage());
        }
    }
}