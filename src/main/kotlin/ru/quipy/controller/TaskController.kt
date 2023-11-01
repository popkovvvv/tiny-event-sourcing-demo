package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TagAssignedToTaskEvent
import ru.quipy.api.TagUnassignedFromTaskEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.ProjectAggregateState
import ru.quipy.logic.project.addTask
import ru.quipy.logic.project.assignTagToTask
import ru.quipy.logic.project.unassignTag
import java.util.*

@RestController
@RequestMapping("/projects")
class TaskController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
) {

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String): TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/tags/{tagId}")
    fun addTagToTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable tagId: UUID): TagAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignTagToTask(tagId, taskId)
        }
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}/tags/{tagId}")
    fun removeTagFromTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable tagId: UUID): TagUnassignedFromTaskEvent {
        return projectEsService.update(projectId) {
            it.unassignTag(tagId, taskId)
        }
    }
}
