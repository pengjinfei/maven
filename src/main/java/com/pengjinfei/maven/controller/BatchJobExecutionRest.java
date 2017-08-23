package com.pengjinfei.maven.controller;

import com.github.pagehelper.PageInfo;
import com.pengjinfei.maven.entity.BatchJobExecution;
import com.pengjinfei.maven.service.batch.BatchJobExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created on 8/23/17
 *
 * @author Pengjinfei
 */
@RestController
@RequestMapping("/batch")
public class BatchJobExecutionRest {

    @Autowired
    private BatchJobExecutionService batchJobExecutionService;

    @GetMapping("/{id}")
    public BatchJobExecution getById(@PathVariable("id") String Id) {
        return batchJobExecutionService.selectById(Id);
    }

    @GetMapping
    public PageInfo<BatchJobExecution> getPage(@RequestParam(name = "pageNum",defaultValue = "1") int pageNum,
                                               @RequestParam(name = "pageSize",defaultValue = "3") int pageSize) {
        return new PageInfo<>(batchJobExecutionService.selectByPage(pageNum, pageSize));
    }

    @PostMapping
    public BatchJobExecution insert(@RequestBody BatchJobExecution execution) {
        return batchJobExecutionService.insert(execution);
    }
}
