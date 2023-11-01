package ru.quipy.logic.project

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc

@StateTransitionFunc
fun ProjectAggregateState.projectCreatedApply(event: ProjectCreatedEvent) {
    projectId = event.projectId
    projectTitle = event.title
    creatorId = event.creatorId
    updatedAt = createdAt
}

@StateTransitionFunc
fun ProjectAggregateState.tagCreatedApply(event: TagCreatedEvent) {
    projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
    updatedAt = createdAt
}

@StateTransitionFunc
fun ProjectAggregateState.tagRemovedApply(event: TagRemovedEvent) {
    projectTags.remove(event.tagId)
    updatedAt = createdAt
}

@StateTransitionFunc
fun ProjectAggregateState.taskCreatedApply(event: TaskCreatedEvent) {
    tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf())
    updatedAt = createdAt
}

@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}

@StateTransitionFunc
fun ProjectAggregateState.tagUnassignedApply(event: TagUnassignedFromTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.remove(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
