package com.pengjinfei.maven.service.batch;

import com.github.pagehelper.PageHelper;
import com.pengjinfei.maven.entity.BatchJobExecution;
import com.pengjinfei.maven.mapper.BatchJobExecutionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created on 8/23/17
 *
 * @author Pengjinfei
 */
@Service
public class BatchJobExecutionService {

    @Autowired
    private BatchJobExecutionMapper jobExecutionMapper;

    public BatchJobExecution selectById(String id) {
        return jobExecutionMapper.selectByPrimaryKey(new BigDecimal(id));
    }

    public List<BatchJobExecution> selectByPage(int pageNum,int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(BatchJobExecution.class);
        example.orderBy("jobInstanceId");
        return jobExecutionMapper.selectByExample(example);
    }

    public BatchJobExecution insert(BatchJobExecution execution) {
        jobExecutionMapper.insertSelective(execution);
        return execution;
    }
}
