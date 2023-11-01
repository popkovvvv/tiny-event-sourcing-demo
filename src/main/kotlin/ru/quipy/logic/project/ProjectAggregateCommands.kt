package ru.quipy.logic.project

import ru.quipy.api.*
import java.util.*

fun create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
}

fun ProjectAggregateState.removeTag(id: UUID): TagRemovedEvent {
    if (!projectTags.containsKey(id)) {
        throw IllegalArgumentException("Tag already deleted: $id")
    }

    if (tasks.any { it.value.tagsAssigned.contains(id) }) {
        throw IllegalArgumentException("Tag assigned, can't remove")
    }

    return TagRemovedEvent(
        projectId = this.getId(), tagId = id, tagName = projectTags[id]?.name
    )
}

fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}

fun ProjectAggregateState.unassignTag(tagId: UUID, taskId: UUID): TagUnassignedFromTaskEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    if (!tasks[taskId]!!.tagsAssigned.contains(tagId)) {
        throw IllegalArgumentException("Tag with id: '$tagId' not assigned to task: '$taskId'")
    }

    return TagUnassignedFromTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}
