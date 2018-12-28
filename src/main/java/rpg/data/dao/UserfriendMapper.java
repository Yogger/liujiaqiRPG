package rpg.data.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import rpg.pojo.Userfriend;
import rpg.pojo.UserfriendExample;

public interface UserfriendMapper {
    int countByExample(UserfriendExample example);

    int deleteByExample(UserfriendExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userfriend record);

    int insertSelective(Userfriend record);

    List<Userfriend> selectByExample(UserfriendExample example);

    Userfriend selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userfriend record, @Param("example") UserfriendExample example);

    int updateByExample(@Param("record") Userfriend record, @Param("example") UserfriendExample example);

    int updateByPrimaryKeySelective(Userfriend record);

    int updateByPrimaryKey(Userfriend record);
}