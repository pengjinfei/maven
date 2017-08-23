package com.pengjinfei.maven.utils;

import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.example.SelectByExampleMapper;
import tk.mybatis.mapper.common.example.SelectCountByExampleMapper;
import tk.mybatis.mapper.common.example.UpdateByExampleMapper;
import tk.mybatis.mapper.common.example.UpdateByExampleSelectiveMapper;

/**
 * Created on 8/23/17
 *
 * @author Pengjinfei
 */
public interface MyBaseMapper<T> extends BaseMapper<T>, SelectByExampleMapper<T>
        , UpdateByExampleMapper<T>, UpdateByExampleSelectiveMapper<T>,SelectCountByExampleMapper<T> {
}
