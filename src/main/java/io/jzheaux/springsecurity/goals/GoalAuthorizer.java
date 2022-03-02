package io.jzheaux.springsecurity.goals;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("post")
public class GoalAuthorizer {

    public boolean authorize(MethodSecurityExpressionOperations operations) {

        Optional<Goal> goalReturned = (Optional<Goal>) operations.getReturnObject();

        return goalReturned.filter(g -> g.getOwner().equals(operations.getAuthentication().getName()))
                .isPresent();

    }

}
