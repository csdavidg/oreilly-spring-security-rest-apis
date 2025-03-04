package io.jzheaux.springsecurity.goals;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@RestController
public class GoalController {
	private final GoalRepository goals;
	private final UserRepository users;

	public GoalController(GoalRepository goals, UserRepository users) {
		this.goals = goals;
		this.users = users;
	}

	@GetMapping("/goals")
	@PreAuthorize("hasAuthority('goal:read')")
	public Iterable<Goal> read() {
		Iterable<Goal> goals = this.goals.findAll();
		for (Goal goal : goals) {
			addName(goal);
		}
		return goals;
	}

	@GetMapping("/goal/{id}")
	@PreAuthorize("hasAuthority('goal:read')")
	//@PostAuthorize("returnObject.orElse(null)?.owner == authentication.name")//VALIDATES IF THE OBJECT RETURNED BELONGS TO THE USER THAT REQUEST THE GOALS
	@PostAuthorize("@post.authorize(#root)")//SEE GoalAuthorizer.java
	public Optional<Goal> read(@PathVariable("id") UUID id) {
		return this.goals.findById(id).map(this::addName);
	}

	@PostMapping("/goal")
	@PreAuthorize("hasAuthority('goal:write')")
	/*public Goal make(@CurrentSecurityContext(expression = "authentication.principal") User user,
					 @RequestBody String text) {
					 SEE CurrentSecurityContextArgumentResolver class
					 */
	public Goal make(@CurrentSecurityContext(expression = "authentication.name") String owner,
					 @RequestBody String text) {
//		String owner = "user";
		Goal goal = new Goal(text, owner);
		return this.goals.save(goal);
	}

	@PutMapping(path="/goal/{id}/revise")
	@PreAuthorize("hasAuthority('goal:write')")
	@Transactional
	public Optional<Goal> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.goals.revise(id, text);
		return read(id);
	}

	@PutMapping("/goal/{id}/complete")
	@Transactional
	@PreAuthorize("hasAuthority('goal:write')")
	public Optional<Goal> complete(@PathVariable("id") UUID id) {
		this.goals.complete(id);
		return read(id);
	}

	@PutMapping("/goal/{id}/share")
	@Transactional
	@PreAuthorize("hasAuthority('goal:write')")
	public Optional<Goal> share(@AuthenticationPrincipal User user, @PathVariable("id") UUID id) {
		Optional<Goal> goal = read(id);
		goal.filter(r -> r.getOwner().equals(user.getUsername()))
				.map(Goal::getText).ifPresent(text -> {
			for (User friend : user.getFriends()) {
				this.goals.save(new Goal(text, friend.getUsername()));
			}
		});
		return goal;
	}

	private Goal addName(Goal goal) {
		String name = this.users.findByUsername(goal.getOwner())
				.map(User::getFullName).orElse("none");
		goal.setText(goal.getText() + ", by " + name);
		return goal;
	}
}
