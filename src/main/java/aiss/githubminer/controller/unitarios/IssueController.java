package aiss.githubminer.controller.unitarios;

import aiss.githubminer.model.GitHubIssue;
import aiss.githubminer.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/github")
public class IssueController {

    private final IssueService issueService;

    @Value("${githubminer.sinceissues}")
    private int defaultSinceIssues;

    @Value("${githubminer.maxpages}")
    private int defaultMaxPages;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/{owner}/{repo}/issues")
    public List<GitHubIssue> getAllIssues(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) Integer sinceIssues,
            @RequestParam(required = false) Integer maxPages) {

        int issues = (sinceIssues != null) ? sinceIssues : defaultSinceIssues;
        int page = (maxPages != null) ? maxPages : defaultMaxPages;

        try {
            return issueService.getAllIssues(owner, repo, issues, page);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los issues: " + e.getMessage());
        }
    }
}