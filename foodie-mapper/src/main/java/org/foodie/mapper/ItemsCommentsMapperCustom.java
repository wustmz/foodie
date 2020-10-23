package org.foodie.mapper;

import org.apache.ibatis.annotations.Param;
import org.foodie.pojo.vo.MyCommentVO;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom {

    void saveComments(Map<String, Object> map);

    List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);

}