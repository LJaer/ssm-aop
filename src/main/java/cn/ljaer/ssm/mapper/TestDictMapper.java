package cn.ljaer.ssm.mapper;

import cn.ljaer.ssm.po.TestDict;
import cn.ljaer.ssm.po.TestDictExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TestDictMapper {
    int countByExample(TestDictExample example);

    int deleteByExample(TestDictExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TestDict record);

    int insertSelective(TestDict record);

    List<TestDict> selectByExample(TestDictExample example);

    TestDict selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TestDict record, @Param("example") TestDictExample example);

    int updateByExample(@Param("record") TestDict record, @Param("example") TestDictExample example);

    int updateByPrimaryKeySelective(TestDict record);

    int updateByPrimaryKey(TestDict record);
}