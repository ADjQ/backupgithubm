package aiss.githubminer.controller.unitarios;

import aiss.githubminer.model.GitHubComment;
import aiss.githubminer.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/github")
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/{owner}/{repo}/comments")
    public List<GitHubComment> getComments(@PathVariable String owner, @PathVariable String repo) {
        return commentService.getComment(owner, repo);
    }
}
