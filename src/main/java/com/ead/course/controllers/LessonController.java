package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    @Autowired
    LessonService lessonService;

    @Autowired
    ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(@PathVariable(value="moduleId") UUID moduleId,
                                             @RequestBody @Valid LessonDto lessonDto){
        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
        if (!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Module Not Found");
        }
        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel); //faz a conversao de dto p model
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC"))); //setar data de criacao
        lessonModel.setModule(moduleModelOptional.get()); //relacionamento entre lição e módulo

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.save(lessonModel));
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value="moduleId")UUID moduleId,
                                               @PathVariable(value="lessonId")UUID lessonId){
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModel(moduleId, lessonId);
        if (!lessonModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lesson Not Found for this MOdule");
        }
        lessonService.delete(lessonModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK)
                .body("Lesson Deleted Successfully");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable(value="moduleId")UUID moduleId,
                                               @PathVariable(value="lessonId")UUID lessonId,
                                               @RequestBody @Valid LessonDto lessonDto){
        Optional<LessonModel> LessonModelOptional = lessonService.findLessonIntoModel(moduleId, lessonId);
        if (!LessonModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lesson Not Found for this Module");
        }

        var lessonModel = LessonModelOptional.get();
        lessonModel.setTitle(lessonDto.getTitle());
        lessonModel.setDescription(lessonDto.getDescription());
        lessonModel.setVideoUrl(lessonDto.getVideoUrl());
        return ResponseEntity.status(HttpStatus.OK)
                .body(lessonService.save(lessonModel));
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLessons(@PathVariable(value="moduleId") UUID moduleId,
                                                           SpecificationTemplate.LessonSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK)
                .body(lessonService.findAllByModule(SpecificationTemplate.lessonModuleId(moduleId)
                        .and(spec), pageable));

    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(@PathVariable(value="moduleId")UUID moduleId,
                                               @PathVariable(value="lessonId")UUID lessonId){
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModel(moduleId, lessonId);
        return lessonModelOptional.<ResponseEntity<Object>>map(lessonModel -> ResponseEntity.status(HttpStatus.OK)
                .body(lessonModel)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Module Not Found for this Course"));

    }















}






