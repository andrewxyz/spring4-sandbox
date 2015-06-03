package com.hantsylabs.example.spring.web;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hantsylabs.example.spring.jpa.TaskRepository;
import com.hantsylabs.example.spring.model.Status;
import com.hantsylabs.example.spring.model.Task;

/**
 * 
 * @author hantsy
 *
 */
@Controller
@RequestMapping(value = "/tasks")
public class TaskController {
	private static final Logger log = LoggerFactory
			.getLogger(TaskController.class);

	@Inject
	private TaskRepository taskRepository;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String allTask(ModelMap model) {

		List<TaskDetails> detailsList = new ArrayList<>();

		Sort sort = new Sort(Direction.DESC, "lastModifiedDate");
		List<Task> tasks = taskRepository.findAll(sort);

		for (Task task : tasks) {
			TaskDetails details = new TaskDetails();
			details.setName(task.getName());
			details.setDescription(task.getDescription());
			details.setCreatedDate(task.getCreatedDate());
			details.setLastModifiedDate(task.getLastModifiedDate());
			detailsList.add(details);
		}

		model.addAttribute("tasks", detailsList);
		
		return "tasks";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String createTask(@RequestBody TaskForm fm) {
		Task task = new Task();
		task.setName(fm.getName());
		task.setDescription(fm.getDescription());

		task = taskRepository.save(task);

		return "redirect:/tasks";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers="!action")
	public String updateTask(@PathVariable("id") Long id,
			@RequestBody TaskForm fm) {

		Task task = taskRepository.findOne(id);

		if (task == null) {
			throw new TaskNotFoundException(id);
		}

		task.setName(fm.getName());
		task.setDescription(fm.getDescription());

		taskRepository.save(task);

		return "redirect:/tasks";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "action=MARK_DOING")
	public String markTaskDoing(@PathVariable("id") Long id) {

		Task task = taskRepository.findOne(id);

		if (task == null) {
			throw new TaskNotFoundException(id);
		}

		task.setStatus(Status.DOING);

		taskRepository.save(task);

		return "redirect:/tasks";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "action=MARK_DONE")
	public String markTaskDone(@PathVariable("id") Long id) {

		Task task = taskRepository.findOne(id);

		if (task == null) {
			throw new TaskNotFoundException(id);
		}

		task.setStatus(Status.DONE);

		taskRepository.save(task);

		return "redirect:/tasks";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getTask(
			@PathVariable("id") @NotNull Long id, 
			ModelMap model) {

		Task task = taskRepository.findOne(id);

		if (task == null) {
			throw new TaskNotFoundException(id);
		}

		TaskDetails details = new TaskDetails();
		details.setId(task.getId());
		details.setName(task.getName());
		details.setDescription(task.getDescription());
		details.setCreatedDate(task.getCreatedDate());
		details.setLastModifiedDate(task.getLastModifiedDate());

		if (log.isDebugEnabled()) {
			log.debug("task details@" + details);
		}
		
		model.addAttribute("details", details);

		return "details";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String deleteTask(@PathVariable("id") Long id) {

		Task task = taskRepository.findOne(id);

		if (task == null) {
			throw new TaskNotFoundException(id);
		}

		taskRepository.delete(id);

		return "redirect:/tasks";
	}

}
