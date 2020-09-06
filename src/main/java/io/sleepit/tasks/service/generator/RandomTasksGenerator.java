package io.sleepit.tasks.service.generator;

import io.sleepit.skills.model.PersistedSkill;
import io.sleepit.skills.repository.SkillsFetchOperations;
import io.sleepit.tasks.model.DefaultTask;
import io.sleepit.tasks.model.Task;
import io.sleepit.tasks.model.Task.Status;
import io.sleepit.tasks.model.Task.ValidityRange;
import io.sleepit.tasks.model.amount.Amount;
import io.sleepit.tasks.model.amount.DurationAmount;
import io.sleepit.tasks.model.amount.MilliliterAmount;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RandomTasksGenerator implements TasksGenerator {

    private final SkillsFetchOperations skillsFetchOperations;

    public RandomTasksGenerator(final SkillsFetchOperations skillsFetchOperations) {
        this.skillsFetchOperations = Objects.requireNonNull(skillsFetchOperations, "skillsFetchOperations can not be null");
    }

    @Override
    public List<Task> generate(final Integer userId, final Integer numberOfTasks) {
        final List<PersistedSkill> skills = new ArrayList<>(skillsFetchOperations.findAll());

        Collections.shuffle(skills);

        return skills.stream()
                .limit(numberOfTasks)
                .map(skill -> {
                    final Amount amount = validAmountForSkill(skill);
                    return new DefaultTask(
                            skill,
                            userId,
                            amount,
                            Status.ACTIVE,
                            new ValidityRange(ZonedDateTime.now(), ZonedDateTime.now().plusDays(1))
                    );
                }).collect(Collectors.toList());
    }

    private Amount validAmountForSkill(final PersistedSkill skill) {
        switch (skill.code()) {
            case SUN:
                return DurationAmount.ofMinutes(10);
            case WATER:
                return MilliliterAmount.of(2000);
            case EXERCISE:
            case NO_TV_PC_SMARTPHONE:
                return DurationAmount.ofMinutes(30);
            default:
                return null;
        }
    }

}