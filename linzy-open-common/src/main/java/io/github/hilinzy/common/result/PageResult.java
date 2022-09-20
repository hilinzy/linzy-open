package io.github.hilinzy.common.result;

import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
  _     _  __  _ _____ __  __
 | |__ | ||  \| |` / /_\ \/ /
 |____||_||_|\__| /___/ |__|
 * @description  自定义分页数据结果集
 * @author linzy
 * @date  2022/9/20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

  /*序列化标识*/
  private static final long serialVersionUID = 1L;

  /*业务代码*/
  private Integer code;
  /*提示语*/
  private String message;
  /*分页数据对象*/
  private PageInfo<T> data;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PageInfo<T> implements Serializable{


    private static final long serialVersionUID = 4627515164385331418L;
    /*当前页*/
    private Integer page;

    /*总页数*/
    private Integer totalPage;

    /*总条数*/
    private Integer count;

    /*数据*/
    private List<T> list;
  }


  public PageResult(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * 返回成功消息
   */
  public static <T> PageResult<T> success() {
    return new PageResult<>(HttpStatus.HTTP_OK, "success");
  }

  public static <T> PageResult<T> success(PageInfo<T> data) {
    PageResult<T> result = new PageResult<>();
    result.setCode(HttpStatus.HTTP_OK);
    if (StrUtil.isBlank(result.getMessage())) result.setMessage("success");
    result.setData(data);
    return result;
  }

  public static <T> PageResult<T> success(List<T> data, Integer page, Integer totalPage, Integer count) {
    return success("success", data, page, totalPage, count);
  }

  public static <T> PageResult<T> success(String msg, List<T> data, Integer page, Integer totalPage, Integer count) {
    return new PageResult<>(HttpStatus.HTTP_OK, msg, new PageInfo<>(page, totalPage, count, data));
  }

  /**
   * -----------------------------------------------------------------------------------------
   * ------------------------------------- 分页 -----------------------------------------------
   * -----------------------------------------------------------------------------------------
   */

  public static Integer getTotalPage(Integer total, Integer pageSize) {
    return PageUtil.totalPage(total, pageSize);
  }
}
